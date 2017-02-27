package au.com.ahbeard.sleepsense.bluetooth.bleService;

import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothEvent;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceFactory;
import au.com.ahbeard.sleepsense.services.log.SSLog;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;

/**
 * Created by luisramos on 22/02/2017.
 */

public class SleepsenseScanningFilter implements Predicate<BluetoothEvent> {

    // Check the address of the device.
    private Map<String, BluetoothEvent.PacketEvent> scannedDevices = Collections.synchronizedMap(new HashMap<String, BluetoothEvent.PacketEvent>());

    @Override
    public boolean test(@NonNull BluetoothEvent bluetoothEvent) throws Exception {
        if (bluetoothEvent instanceof BluetoothEvent.PacketEvent) {

            BluetoothEvent.PacketEvent scanPacketEvent =
                    (BluetoothEvent.PacketEvent) bluetoothEvent;

            String deviceAddress = scanPacketEvent.getDevice().getAddress();

//            logPacket(scanPacketEvent);

            // Check if packet was already scanned
            if (scannedDevices.containsKey(deviceAddress)) {
                scannedDevices.get(deviceAddress).addRssi(scanPacketEvent.getRssi());
            }
            // else check if it is a SS device
            else if (SleepSenseDeviceFactory.isSleepSenseDevice(scanPacketEvent)) {
                scannedDevices.put(deviceAddress, scanPacketEvent);
                return true;
            }
        }

        return false;
    }

    private void logPacket(BluetoothEvent.PacketEvent scanPacketEvent) {
        SSLog.d("SleepSenseDeviceService",
                String.format("received device with address: %s name: %s",
                        scanPacketEvent.getDevice().getAddress(),
                        scanPacketEvent.getDevice().getName()));
    }
}
