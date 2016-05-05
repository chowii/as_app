package au.com.ahbeard.sleepsense.bluetooth;

import android.content.Context;
import android.util.Log;

import com.beddit.sensor.SensorManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerDevice;
import au.com.ahbeard.sleepsense.services.LogService;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
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

        if ( mPumpDeviceAddress != null ) {
            mPumpDevice = new PumpDevice();
            mPumpDevice.link(context,BluetoothService.instance().createDeviceFromAddress(mPumpDeviceAddress));
        }
        if ( mBaseDeviceAddress != null ) {
            mBaseDevice = new BaseDevice();
            mBaseDevice.link(context,BluetoothService.instance().createDeviceFromAddress(mBaseDeviceAddress));
        }
        if ( mTrackerDeviceAddress != null ) {
            mTrackerDevice = new TrackerDevice();
            mTrackerDevice.link(context,BluetoothService.instance().createDeviceFromAddress(mTrackerDeviceAddress));
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

    public Observable<String> acquireDevices(long milliseconds) {

        final PublishSubject<String> mObservable = PublishSubject.create();

        mChangeEventPublishSubject.onNext(SleepSenseDeviceServiceEvent.StartedSearchingForDevices);

        BluetoothService.instance()
                .startScanning()
                .filter(new ScanningFilter())
                .cast(BluetoothScanEvent.DeviceFoundEvent.class)
                .take(milliseconds, TimeUnit.MILLISECONDS)
                .toList()
                .map(new Func1<List<BluetoothScanEvent.DeviceFoundEvent>, List<Device>>() {
                    @Override
                    public List<Device> call(List<BluetoothScanEvent.DeviceFoundEvent> deviceFoundEvents) {

                        // Sort by RSSI.
                        Collections.sort(deviceFoundEvents, new Comparator<BluetoothScanEvent.DeviceFoundEvent>() {
                            @Override
                            public int compare(BluetoothScanEvent.DeviceFoundEvent lhs,
                                               BluetoothScanEvent.DeviceFoundEvent rhs) {
                                return lhs.getRssi() - rhs.getRssi();
                            }
                        });

                        ArrayList<Device> devices = new ArrayList<>();

                        for (BluetoothScanEvent.DeviceFoundEvent deviceFoundEvent:deviceFoundEvents) {
                            devices.addAll(SleepSenseDeviceFactory.factorySleepSenseDevice(mContext,deviceFoundEvent));
                        }

                        return devices;
                    }
                })
                .subscribe(new Action1<List<Device>>() {
                    @Override
                    public void call(List<Device> devices) {

                        BluetoothService.instance().stopScanning();

                        LogService.d("SleepSenseDeviceService",String.format("found %d SleepSense devices while scanning...", devices.size()));

                        boolean hasChanged = false;

                        // Assign the closest of each device if we don't have one.
                        for (Device device : devices) {

                            if (mBaseDevice == null && device instanceof BaseDevice) {
                                mBaseDevice = (BaseDevice) device;
                                PreferenceService.instance().setBaseDeviceAddress(device.getAddress());
                                LogService.d("SleepSenseDeviceService","found BaseDevice...");
                                hasChanged = true;
                                continue;
                            }

                            if (mPumpDevice == null && device instanceof PumpDevice) {
                                mPumpDevice = (PumpDevice) device;
                                PreferenceService.instance().setPumpDeviceAddress(device.getAddress());
                                LogService.d("SleepSenseDeviceService","found PumpDevice...");
                                hasChanged = true;
                                continue;
                            }

                            if (mTrackerDevice == null && device instanceof TrackerDevice) {
                                mTrackerDevice = (TrackerDevice) device;
                                PreferenceService.instance().setTrackerDeviceAddress(device.getAddress());
                                LogService.d("SleepSenseDeviceService","found TrackerDevice...");
                                hasChanged = true;
                                continue;
                            }
                        }

                        mObservable.onCompleted();

                        if ( hasChanged ) {
                            mChangeEventPublishSubject.onNext(SleepSenseDeviceServiceEvent.DeviceListChanged);
                        }

                        mChangeEventPublishSubject.onNext(SleepSenseDeviceServiceEvent.FinishedSearchingForDevices);
                    }
                });


        return mObservable;
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

    public boolean hasAllDevices() {
        return hasBaseDevice() && hasPumpDevice() && hasTrackerDevice();
    }

    public int countDevices() {
        int numberOfConnections = 0;
        if ( hasBaseDevice() ) {
            numberOfConnections++;
        }
        if ( hasPumpDevice() ) {
            numberOfConnections++;
        }
        if ( hasTrackerDevice() ) {
            numberOfConnections++;
        }
        return numberOfConnections;
    }

    public Observable<String> getLogObservable() {
        return mLogSubject;
    }

    public void clearDevices() {

        PreferenceService.instance().clearBaseDeviceAddress();
        mBaseDevice = null;
        PreferenceService.instance().clearPumpDeviceAddress();
        mPumpDevice = null;
        PreferenceService.instance().clearTrackerDeviceAddress();
        mTrackerDevice = null;

        mChangeEventPublishSubject.onNext(SleepSenseDeviceServiceEvent.DeviceListChanged);
    }

    // The scanning filter.
    class ScanningFilter implements Func1<BluetoothScanEvent, Boolean> {

        Set<String> foundDevices = new HashSet<>();

        @Override
        public Boolean call(BluetoothScanEvent bluetoothScanEvent) {

            if (bluetoothScanEvent instanceof BluetoothScanEvent.DeviceFoundEvent) {

                BluetoothScanEvent.DeviceFoundEvent deviceFoundEvent =
                        (BluetoothScanEvent.DeviceFoundEvent) bluetoothScanEvent;

                String deviceAddress = deviceFoundEvent.getDevice().getAddress();

                if (foundDevices.contains(deviceAddress)) {
                    return false;
                } else {
                    foundDevices.add(deviceAddress);
                    return SleepSenseDeviceFactory.isSleepSenseDevice(deviceFoundEvent);
                }
            }

            return false;
        }
    }



}
