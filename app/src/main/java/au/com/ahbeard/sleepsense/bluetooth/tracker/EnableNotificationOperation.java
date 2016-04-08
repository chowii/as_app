package au.com.ahbeard.sleepsense.bluetooth.tracker;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothOperation;

/**
 * Created by neal on 14/01/2014.
 */
public class EnableNotificationOperation extends BluetoothOperation {

    private UUID mServiceUUID;
    private UUID mCharacteristicUUID;

    private static final UUID CLIENT_CHARACTERISTIC_CONFIG =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    public EnableNotificationOperation(UUID serviceUUID, UUID characteristicUUID) {

        mServiceUUID = serviceUUID;
        mCharacteristicUUID = characteristicUUID;

    }

    @Override
    public boolean perform(BluetoothGatt bluetoothGatt) {

        BluetoothGattService service = bluetoothGatt.getService(mServiceUUID);

        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(mCharacteristicUUID);
            bluetoothGatt.setCharacteristicNotification(characteristic, true);

            if (characteristic != null) {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);

                bluetoothGatt.writeDescriptor(descriptor);
            }

            return false;
        }

        return true;
    }
}
