package au.com.ahbeard.sleepsense.bluetooth;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.base.DummyBaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.DummyPumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
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

        String name = deviceFoundEvent.getDevice().getName();

        if ("BLE Mini".equals(name)) {
            return true;
        }

        if ("2618BL".equals(name)) {
            return true;
        }

        if (name != null && name.contains("base-i4")) {
            return true;
        }

        return false;
    }

    public static List<Device> factorySleepSenseDevice(Context context, BluetoothScanEvent.DeviceFoundEvent deviceFoundEvent) {

        List<Device> mDevices = new ArrayList<>();

//        if ("ED:FD:CB:C2:05:91".equals(deviceFoundEvent.getDevice().getAddress())) {
//            mDevices.add(new DummyBaseDevice());
//            DummyPumpDevice dummyPumpDevice = new DummyPumpDevice();
//            dummyPumpDevice.link(context,deviceFoundEvent.getDevice());
//            mDevices.add(dummyPumpDevice);
//            mDevices.add(new DummyTrackerDevice());
//        }

        String name = deviceFoundEvent.getDevice().getName();

        if ("BLE Mini".equals(name)) {
            DummyPumpDevice dummyPumpDevice = new DummyPumpDevice();
            dummyPumpDevice.link(context, deviceFoundEvent.getDevice());
            mDevices.add(dummyPumpDevice);
        }

        if ("2618BL".equals(name)) {
            PumpDevice pumpDevice = new PumpDevice();
            pumpDevice.link(context, deviceFoundEvent.getDevice());
            mDevices.add(pumpDevice);
        }

        if (name != null && name.contains("base-i4")) {
            BaseDevice baseDevice = new BaseDevice();
            baseDevice.link(context, deviceFoundEvent.getDevice());
            mDevices.add(baseDevice);
        }


        return mDevices;
    }
}
