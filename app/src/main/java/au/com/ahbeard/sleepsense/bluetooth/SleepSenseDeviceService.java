package au.com.ahbeard.sleepsense.bluetooth;

import android.content.Context;
import android.util.Log;

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
import au.com.ahbeard.sleepsense.services.LogService;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import rx.Observable;
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

    private PumpDevice mPumpDevice;
    private BaseDevice mBaseDevice;
    private TrackerDevice mTrackerDevice;

    private SleepSenseDeviceService(Context context) {

        mContext = context;

        SensorManager.init(context);

        mPumpDeviceAddress = PreferenceService.instance().getPumpDeviceAddress();
        mBaseDeviceAddress = PreferenceService.instance().getBaseDeviceAddress();
        mTrackerDeviceAddress = PreferenceService.instance().getTrackerDeviceAddress();

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

    public Observable<SleepSenseDeviceAquisition> newAcquireDevices(long milliseconds) {

        final PublishSubject<SleepSenseDeviceAquisition> mObservable = PublishSubject.create();

        mChangeEventPublishSubject.onNext(SleepSenseDeviceServiceEvent.StartedSearchingForDevices);

        BluetoothService.instance()
                .startScanning()
                .observeOn(Schedulers.io())
                .filter(new ScanningFilter())
                .cast(BluetoothScanEvent.ScanPacketEvent.class)
                .take(milliseconds, TimeUnit.MILLISECONDS)
                .toList()
                .map(new Func1<List<BluetoothScanEvent.ScanPacketEvent>, List<Device>>() {
                    @Override
                    public List<Device> call(List<BluetoothScanEvent.ScanPacketEvent> scanPacketEvents) {

                        BluetoothService.instance().stopScanning();

                        // Sort by RSSI.
                        Collections.sort(scanPacketEvents, new Comparator<BluetoothScanEvent.ScanPacketEvent>() {
                            @Override
                            public int compare(BluetoothScanEvent.ScanPacketEvent lhs,
                                               BluetoothScanEvent.ScanPacketEvent rhs) {
                                return rhs.getRssi() - lhs.getRssi();
                            }
                        });

                        ArrayList<Device> devices = new ArrayList<>();

                        for (BluetoothScanEvent.ScanPacketEvent scanPacketEvent : scanPacketEvents) {
                            devices.addAll(SleepSenseDeviceFactory.factorySleepSenseDevice(mContext, scanPacketEvent));
                        }

                        return devices;
                    }
                })
                .subscribe(new Action1<List<Device>>() {
                    @Override
                    public void call(List<Device> devices) {

                        LogService.d("SleepSenseDeviceService",
                                String.format("found %d SleepSense devices while scanning...", devices.size()));

                        SleepSenseDeviceAquisition sleepSenseDeviceAquisition = new SleepSenseDeviceAquisition();

                        // Assign the closest of each device if we don't have one.
                        for (Device device : devices) {
                            Log.d("SleepSenseDeviceService", String.format("device found: %s", device.getName()));
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
                                continue;
                            }
                        }

                        mObservable.onNext(sleepSenseDeviceAquisition);
                        mObservable.onCompleted();

                    }
                });


        return mObservable;
    }

    public void setDevices(BaseDevice baseDevice, PumpDevice pumpDevice, TrackerDevice trackerDevice) {

        mBaseDevice = baseDevice;

        if (baseDevice != null) {
            PreferenceService.instance().setBaseDeviceAddress(baseDevice.getAddress());
        } else {
            PreferenceService.instance().clearBaseDeviceAddress();
        }

        mPumpDevice = pumpDevice;

        if (pumpDevice != null) {
            PreferenceService.instance().setPumpDeviceAddress(pumpDevice.getAddress());
        } else {
            PreferenceService.instance().clearPumpDeviceAddress();
        }

        mTrackerDevice = trackerDevice;

        if (trackerDevice != null) {
            PreferenceService.instance().setTrackerDeviceAddress(trackerDevice.getAddress());
            PreferenceService.instance().setTrackerDeviceName(trackerDevice.getName());
        } else {
            PreferenceService.instance().clearTrackerDeviceAddress();
            PreferenceService.instance().clearTrackerDeviceName();
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

    public Observable<String> getLogObservable() {
        return mLogSubject;
    }

    // The scanning filter.
    class ScanningFilter implements Func1<BluetoothScanEvent, Boolean> {

        // Check the address of the device.
        Map<String, BluetoothScanEvent.ScanPacketEvent> scannedDevices = Collections.synchronizedMap(new HashMap<String, BluetoothScanEvent.ScanPacketEvent>());

        @Override
        public Boolean call(BluetoothScanEvent bluetoothScanEvent) {

            if (bluetoothScanEvent instanceof BluetoothScanEvent.ScanPacketEvent) {

                BluetoothScanEvent.ScanPacketEvent scanPacketEvent =
                        (BluetoothScanEvent.ScanPacketEvent) bluetoothScanEvent;

                String deviceAddress = scanPacketEvent.getDevice().getAddress();

                Log.d("SleepSenseDeviceService", String.format("received device with address: %s name: %s", scanPacketEvent.getDevice().getAddress(), scanPacketEvent.getDevice().getName()));

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
