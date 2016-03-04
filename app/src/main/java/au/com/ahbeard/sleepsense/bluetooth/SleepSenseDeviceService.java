package au.com.ahbeard.sleepsense.bluetooth;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.base.DummyBaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.DummyPumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.tracker.DummyTrackerDevice;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerDevice;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by neal on 4/01/2016.
 */
public class SleepSenseDeviceService {

    private static SleepSenseDeviceService sSleepSenseDeviceService;

    public static void initialize(Context context) {
        sSleepSenseDeviceService = new SleepSenseDeviceService(context);
    }

    public static SleepSenseDeviceService instance() {
        return sSleepSenseDeviceService;
    }

    private final Context mContext;

    private String mBaseDeviceAddress;
    private String mPumpDeviceAddress;
    private String mTrackerDeviceAddress;

    private BaseDevice mBaseDevice;
    private PumpDevice mPumpDevice;
    private TrackerDevice mTrackerDevice;

    private SleepSenseDeviceService(Context context) {
        mContext = context;
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

    public Observable<String> acquireDevices(long milliseconds) {

        final PublishSubject<String> mObservable = PublishSubject.create();

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
                            // Neal's FitBit... emit out 3 dummy devices.
                            devices.addAll(SleepSenseDeviceFactory.factorySleepSenseDevice(mContext,deviceFoundEvent));
                        }

                        return devices;
                    }
                })
                .subscribe(new Action1<List<Device>>() {
                    @Override
                    public void call(List<Device> devices) {

                        BluetoothService.instance().stopScanning();

                        // Assign the closest of each device if we don't have one.
                        for (Device device : devices) {
                            if (mBaseDevice == null && device instanceof BaseDevice) {
                                mBaseDevice = (BaseDevice) device;
                                break;
                            }
                            if (mPumpDevice == null && device instanceof PumpDevice) {
                                mPumpDevice = (PumpDevice) device;
                                break;
                            }
                            if (mTrackerDevice == null && device instanceof TrackerDevice) {
                                mTrackerDevice = (TrackerDevice) device;
                                break;
                            }
                        }

                        mObservable.onCompleted();

                    }
                });


        return mObservable;
    }

    public boolean hasBaseDevice() {
        return mBaseDevice != null && mBaseDeviceAddress != null;
    }

    public boolean hasPumpDevice() {
        return mPumpDevice != null && mPumpDeviceAddress != null;
    }

    public boolean hasTrackerDevice() {
        return mTrackerDevice != null && mTrackerDeviceAddress != null;
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

    // The scanning filter.
    class ScanningFilter implements Func1<BluetoothScanEvent, Boolean> {

        Set<String> foundDevices = new HashSet<>();

        @Override
        public Boolean call(BluetoothScanEvent bluetoothScanEvent) {

            if (bluetoothScanEvent instanceof BluetoothScanEvent.DeviceFoundEvent) {

                BluetoothScanEvent.DeviceFoundEvent deviceFoundEvent =
                        (BluetoothScanEvent.DeviceFoundEvent) bluetoothScanEvent;

                if (foundDevices.contains(deviceFoundEvent.getDevice().getAddress())) {
                    return false;
                } else {
                    return SleepSenseDeviceFactory.isSleepSenseDevice(deviceFoundEvent);
                }
            }

            return false;
        }
    }

}
