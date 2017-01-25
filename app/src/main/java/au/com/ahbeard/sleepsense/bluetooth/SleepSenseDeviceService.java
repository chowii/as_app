package au.com.ahbeard.sleepsense.bluetooth;

import android.content.Context;

import com.beddit.sensor.SensorManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerDevice;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.services.log.SSLog;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by neal on 4/01/2016.
 */
public class SleepSenseDeviceService {

    public enum SleepSenseDeviceServiceEvent {
        StartedSearchingForDevices,
        FinishedSearchingForDevices,
        DeviceListChanged
    }

    private static SleepSenseDeviceService sSleepSenseDeviceService;

    public static void initialize(Context context) {
        sSleepSenseDeviceService = new SleepSenseDeviceService(context);
    }

    public static SleepSenseDeviceService instance() {
        return sSleepSenseDeviceService;
    }

    private PublishSubject<String> mLogSubject = PublishSubject.create();

    private PublishSubject<SleepSenseDeviceServiceEvent> mChangeEventPublishSubject = PublishSubject.create();

    private final Context mContext;

    private String mPumpDeviceAddress;
    private String mBaseDeviceAddress;
    private String mTrackerDeviceAddress;
    private String mAltTrackerDeviceAddress;

    private PumpDevice mPumpDevice;
    private BaseDevice mBaseDevice;
    private TrackerDevice mTrackerDevice;
    private TrackerDevice mAltTrackerDevice;

    private SleepSenseDeviceService(Context context) {

        mContext = context;

        SensorManager.init(context);

        mPumpDeviceAddress = PreferenceService.instance().getPumpDeviceAddress();
        mBaseDeviceAddress = PreferenceService.instance().getBaseDeviceAddress();
        mTrackerDeviceAddress = PreferenceService.instance().getTrackerDeviceAddress();
        mAltTrackerDeviceAddress = PreferenceService.instance().getAltTrackerDeviceAddress();

        if (mPumpDeviceAddress != null) {
            mPumpDevice = new PumpDevice();
            mPumpDevice.link(context, BluetoothService.instance().createDeviceFromAddress(mPumpDeviceAddress));
        }
        if (mBaseDeviceAddress != null) {
            mBaseDevice = new BaseDevice();
            mBaseDevice.link(context, BluetoothService.instance().createDeviceFromAddress(mBaseDeviceAddress));
        }
        if (mTrackerDeviceAddress != null) {
            mTrackerDevice = new TrackerDevice();
            mTrackerDevice.link(context, BluetoothService.instance().createDeviceFromAddress(mTrackerDeviceAddress));
        }
        if (mAltTrackerDeviceAddress != null) {
            mAltTrackerDevice = new TrackerDevice();
            mAltTrackerDevice.link(context, BluetoothService.instance().createDeviceFromAddress(mAltTrackerDeviceAddress));
        }
    }

    public Observable<SleepSenseDeviceServiceEvent> getEventObservable() {
        return mChangeEventPublishSubject;
    }

    public BaseDevice getBaseDevice() {
        return mBaseDevice;
    }

    public PumpDevice getPumpDevice() {
        return mPumpDevice;
    }

    public TrackerDevice getTrackerDevice() {
        return mTrackerDevice;
    }

    public void log(int level, String message) {
        mLogSubject.onNext(message);
    }

    public Observable<SleepSenseDeviceAquisition> newAcquireDevices(final long milliseconds) {
        return Observable.create(new Observable.OnSubscribe<SleepSenseDeviceAquisition>() {
            @Override
            public void call(final Subscriber<? super SleepSenseDeviceAquisition> subscriber) {
                SSLog.d("Starting devices scan...");

                mChangeEventPublishSubject.onNext(SleepSenseDeviceServiceEvent.StartedSearchingForDevices);

                BluetoothService.instance()
                        .startScanning()
                        .observeOn(Schedulers.computation())
                        .filter(new ScanningFilter())
                        .cast(BluetoothEvent.PacketEvent.class)
                        .take(milliseconds, TimeUnit.MILLISECONDS)
                        .toList()
                        .map(new Func1<List<BluetoothEvent.PacketEvent>, List<Device>>() {
                            @Override
                            public List<Device> call(List<BluetoothEvent.PacketEvent> scanPacketEvents) {

                                BluetoothService.instance().stopScanning();

                                // Sort by RSSI.
                                Collections.sort(scanPacketEvents, new Comparator<BluetoothEvent.PacketEvent>() {
                                    @Override
                                    public int compare(BluetoothEvent.PacketEvent lhs,
                                                       BluetoothEvent.PacketEvent rhs) {
                                        return rhs.getRssi() - lhs.getRssi();
                                    }
                                });

                                ArrayList<Device> devices = new ArrayList<>();

                                for (BluetoothEvent.PacketEvent scanPacketEvent : scanPacketEvents) {
                                    devices.addAll(SleepSenseDeviceFactory.factorySleepSenseDevice(mContext, scanPacketEvent));
                                }

                                return devices;
                            }
                        })
                        .subscribe(new Action1<List<Device>>() {
                            @Override
                            public void call(List<Device> devices) {

                                SSLog.d("Found %d devices", devices.size());

                                SleepSenseDeviceAquisition sleepSenseDeviceAquisition = new SleepSenseDeviceAquisition();

                                // Assign the closest of each device if we don't have one.
                                for (Device device : devices) {
                                    SSLog.d(device.getName());
                                    // Log.d("SleepSenseDeviceService", String.format("device found: %s", device.getName()));
                                    if (device instanceof BaseDevice) {
                                        sleepSenseDeviceAquisition.getBaseDevices().add((BaseDevice) device);
                                        continue;
                                    }

                                    if (device instanceof PumpDevice) {
                                        sleepSenseDeviceAquisition.getPumpDevices().add((PumpDevice) device);
                                        continue;
                                    }

                                    if (device instanceof TrackerDevice) {
                                        sleepSenseDeviceAquisition.getTrackerDevices().add((TrackerDevice) device);
                                    }
                                }

                                subscriber.onNext(sleepSenseDeviceAquisition);
                                subscriber.onCompleted();
                            }
                        });
            }
        });
    }

    public void setDevices(BaseDevice baseDevice, PumpDevice pumpDevice, TrackerDevice trackerDevice, TrackerDevice altTrackerDevice) {

        mBaseDevice = baseDevice;

        if (baseDevice != null) {
            mBaseDeviceAddress = mBaseDevice.getAddress();
            PreferenceService.instance().setBaseDeviceAddress(baseDevice.getAddress());
        } else {
            mBaseDeviceAddress = null;
            PreferenceService.instance().clearBaseDeviceAddress();
        }

        mPumpDevice = pumpDevice;

        if (pumpDevice != null) {
            mPumpDeviceAddress = mPumpDevice.getAddress();
            PreferenceService.instance().setPumpDeviceAddress(pumpDevice.getAddress());
        } else {
            mPumpDeviceAddress = null;
            PreferenceService.instance().clearPumpDeviceAddress();
        }

        mTrackerDevice = trackerDevice;

        if (trackerDevice != null) {
            mTrackerDeviceAddress = mTrackerDevice.getAddress();
            PreferenceService.instance().setTrackerDeviceAddress(trackerDevice.getAddress());
            PreferenceService.instance().setTrackerDeviceName(trackerDevice.getName());
        } else {
            mTrackerDeviceAddress = null;
            PreferenceService.instance().clearTrackerDeviceAddress();
            PreferenceService.instance().clearTrackerDeviceName();
        }

        mAltTrackerDevice = altTrackerDevice;

        if (altTrackerDevice != null ) {
            mAltTrackerDeviceAddress = mAltTrackerDevice.getAddress();
            PreferenceService.instance().setAltTrackerDeviceAddress(altTrackerDevice.getAddress());
            PreferenceService.instance().setAltTrackerDeviceName(altTrackerDevice.getName());
        } else {
            mAltTrackerDeviceAddress = null;
            PreferenceService.instance().clearAltTrackerDeviceAddress();
            PreferenceService.instance().clearAltTrackerDeviceName();
        }
    }

    public boolean hasBaseDevice() {
        return mBaseDevice != null || mBaseDeviceAddress != null;
    }

    public boolean hasPumpDevice() {
        return mPumpDevice != null || mPumpDeviceAddress != null;
    }

    public boolean hasTrackerDevice() {
        return mTrackerDevice != null || mTrackerDeviceAddress != null;
    }

    public boolean hasAltTrackerDevice() {
        return mAltTrackerDevice != null || mAltTrackerDeviceAddress != null;
    }

    public Observable<String> getLogObservable() {
        return mLogSubject;
    }

    long defaultSearchingTimer = 2500;

//    public Observable<PumpDevice> findPumpDevices() {
//        return newAcquireDevices(defaultSearchingTimer)
//                .map(new Func1<SleepSenseDeviceAquisition, PumpDevice>() {
//                    @Override
//                    public PumpDevice call(SleepSenseDeviceAquisition sleepSenseDeviceAquisition) {
//
//                    }
//                })
//    }

    // The scanning filter.
    private static class ScanningFilter implements Func1<BluetoothEvent, Boolean> {

        // Check the address of the device.
        Map<String, BluetoothEvent.PacketEvent> scannedDevices = Collections.synchronizedMap(new HashMap<String, BluetoothEvent.PacketEvent>());

        @Override
        public Boolean call(BluetoothEvent bluetoothEvent) {

            if (bluetoothEvent instanceof BluetoothEvent.PacketEvent) {

                BluetoothEvent.PacketEvent scanPacketEvent =
                        (BluetoothEvent.PacketEvent) bluetoothEvent;

                String deviceAddress = scanPacketEvent.getDevice().getAddress();

                // Log.d("SleepSenseDeviceService", String.format("received device with address: %s name: %s", scanPacketEvent.getDevice().getAddress(), scanPacketEvent.getDevice().getName()));

                if (scannedDevices.containsKey(deviceAddress)) {
                    scannedDevices.get(deviceAddress).addRssi(scanPacketEvent.getRssi());
                } else {
                    if (SleepSenseDeviceFactory.isSleepSenseDevice(scanPacketEvent)) {
                        scannedDevices.put(deviceAddress, scanPacketEvent);
                        return true;
                    }
                }
            }

            return false;
        }
    }


}
