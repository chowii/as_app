package au.com.ahbeard.sleepsense.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerDevice;
import au.com.ahbeard.sleepsense.hardware.BedHardware;
import au.com.ahbeard.sleepsense.hardware.pump.PumpHardware;

/**
 * Created by neal on 4/03/2016.
 */
public class SleepSenseDeviceFactory {

    private static final boolean FIND_TRACKER = true;

    private static final String PUMP_SCAN_NAME = "2618";
    private static final String BASE_SCAN_NAME = "base-i4";
    private static final String BEDDIT_TRACKER_SCAN_NAME = "beddit";

    public static boolean isSleepSenseDevice(BluetoothEvent.PacketEvent scanPacketEvent) {

        String name = scanPacketEvent.getDevice().getName();

        if (name != null && name.toLowerCase().contains("2618")) {
            Set<UUID> advertisedUUIDs = BluetoothUtils.parseUUIDsFromScanRecord(scanPacketEvent.getAdvertisingData());
//            for (UUID advertisedUUID : advertisedUUIDs) {
//                // Log.d("SleepSenseDeviceFactory", "pump: " + advertisedUUID);
//            }
            return advertisedUUIDs.contains(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
        }

        if (name != null && name.toLowerCase().contains("base-i4")) {
            Set<UUID> advertisedUUIDs = BluetoothUtils.parseUUIDsFromScanRecord(scanPacketEvent.getAdvertisingData());
//            for (UUID advertisedUUID : advertisedUUIDs) {
//                // Log.d("SleepSenseDeviceFactory", "base: " + advertisedUUID);
//            }
            return advertisedUUIDs.contains(UUID.fromString("0000ffb0-0000-1000-8000-00805f9b34fb"));
        }

        if (FIND_TRACKER && name != null && name.toLowerCase().contains("beddit")) {
            Set<UUID> advertisedUUIDs = BluetoothUtils.parseUUIDsFromScanRecord(scanPacketEvent.getAdvertisingData());
//            for (UUID advertisedUUID : advertisedUUIDs) {
//                // Log.d("SleepSenseDeviceFactory", "beddit: " + advertisedUUID);
//            }
            return true;//advertisedUUIDs.contains(UUID.fromString("4ae71336-e44b-39bf-b9d2-752e234818a5"));
        }

        return false;
    }

    static List<Device> factorySleepSenseDevice(Context context, BluetoothEvent.PacketEvent scanPacketEvent) {

        List<Device> mDevices = new ArrayList<>();

        String name = scanPacketEvent.getDevice().getName();

        if (name != null && name.toLowerCase().contains(PUMP_SCAN_NAME)) {
            PumpDevice pumpDevice = new PumpDevice();
            pumpDevice.link(context, scanPacketEvent.getDevice());
            mDevices.add(pumpDevice);
        }

        if (name != null && name.contains(BASE_SCAN_NAME)) {
            BaseDevice baseDevice = new BaseDevice();
            baseDevice.link(context, scanPacketEvent.getDevice());
            mDevices.add(baseDevice);
        }

        if (FIND_TRACKER && name != null && name.toLowerCase().contains(BEDDIT_TRACKER_SCAN_NAME)) {
            TrackerDevice trackerDevice = new TrackerDevice();
            trackerDevice.link(context, scanPacketEvent.getDevice());
            mDevices.add(trackerDevice);
        }


        return mDevices;
    }

    static List<BedHardware> factoryBedHardware(Context context, BluetoothEvent.PacketEvent packet) {
        List<BedHardware> hardwareArray = new ArrayList<>();

        BluetoothDevice device = packet.getDevice();
        String name = device.getName();

        if (name != null) {
            if (name.toLowerCase().contains(PUMP_SCAN_NAME)) {
                hardwareArray.add(new PumpHardware(context, device));
            }
        }

        return hardwareArray;
    }
}
