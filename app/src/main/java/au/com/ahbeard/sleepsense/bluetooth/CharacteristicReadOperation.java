package au.com.ahbeard.sleepsense.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.UUID;

/**
 * Created by neal on 14/03/2016.
 */
public class CharacteristicReadOperation extends BluetoothOperation {

    private UUID mServiceUUID;
    private UUID mCharacteristicUUID;

    private byte[] mValue;

    public CharacteristicReadOperation(UUID serviceUUID, UUID characteristicUUID) {
        mServiceUUID = serviceUUID;
        mCharacteristicUUID = characteristicUUID;
    }

    @Override
    public boolean perform(BluetoothGatt bluetoothGatt) {
        Log.d("CharacteristicRead","reading characteristic: " + mCharacteristicUUID);
        BluetoothGattService service = bluetoothGatt.getService(mServiceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(mCharacteristicUUID);
            if (characteristic != null) {
                bluetoothGatt.readCharacteristic(characteristic);
            }
        }
        return false;
    }

}
