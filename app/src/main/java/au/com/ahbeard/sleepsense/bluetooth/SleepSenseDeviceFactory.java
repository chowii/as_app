package au.com.ahbeard.sleepsense.bluetooth;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.bluetooth.base.DummyBaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.DummyPumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.tracker.DummyTrackerDevice;

/**
 * Created by neal on 4/03/2016.
 */
public class SleepSenseDeviceFactory {

    public static boolean isSleepSenseDevice(BluetoothScanEvent.DeviceFoundEvent deviceFoundEvent) {

        // Neal's FitBit.
        if ("ED:FD:CB:C2:05:91".equals(deviceFoundEvent.getDevice().getAddress())) {
            return true;
        }

        return false;
    }

    public static List<Device> factorySleepSenseDevice(Context context, BluetoothScanEvent.DeviceFoundEvent deviceFoundEvent) {

        List<Device> mDevices = new ArrayList<>();

        if ( false ) {
            new Device(context,deviceFoundEvent.getDevice());
        }

        if ("ED:FD:CB:C2:05:91".equals(deviceFoundEvent.getDevice().getAddress())) {
            mDevices.add(new DummyBaseDevice());
            mDevices.add(new DummyPumpDevice());
            mDevices.add(new DummyTrackerDevice());
        }


        return mDevices;
    }
}
