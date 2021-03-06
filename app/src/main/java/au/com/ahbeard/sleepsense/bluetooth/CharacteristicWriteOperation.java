package au.com.ahbeard.sleepsense.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

import au.com.ahbeard.sleepsense.services.log.SSLog;
import au.com.ahbeard.sleepsense.utils.ByteUtils;
import au.com.ahbeard.sleepsense.utils.ConversionUtils;

/**
 * Created by neal on 14/01/2014.
 */
public class CharacteristicWriteOperation extends BluetoothOperation {

    private UUID mServiceUUID;
    private UUID mCharacteristicUUID;
    private byte[] mValue;

    private int mLength;
    private boolean mAlign;

    public CharacteristicWriteOperation(UUID serviceUUID, UUID characteristicUUID) {

        mServiceUUID = serviceUUID;
        mCharacteristicUUID = characteristicUUID;
        mValue = new byte[15];

        mLength = 0;

    }

    /**
     * Write a byte.
     *
     * @param value
     */
    public void writeByte(int value) {
        mValue[mLength++] = (byte) (value & 0xff);
    }

    /**
     * Write a short.
     *
     * @param value
     */
    public void writeShort(int value) {

        // Align on 2 byte boundary.
        if (mAlign && mLength % 2 != 0) {
            mValue[mLength++] = 0x00;
        }

        mValue[mLength++] = (byte) (value & 0xff);
        mValue[mLength++] = (byte) ((value >> 8) & 0xff);
    }

    /**
     * Write a short at a given location.
     *
     * @param location
     * @param value
     */
    public void writeShort(int location, int value) {

        // Align on 2 byte boundary.
        if (mAlign && location % 2 == 0) {
            location++;
        }

        mValue[location++] = (byte) (value & 0xff);
        mValue[location] = (byte) ((value >> 8) & 0xff);

    }

    /**
     * Write a byte array.
     *
     * @param bytes
     */
    public void writeBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            mValue[mLength++] = bytes[i];
        }
    }

    /**
     * Write a uint32 in little endian format.
     * @param value
     */
    public void writeUInt32(int value) {
        mValue[mLength++] = (byte) (value & 0xff);
        mValue[mLength++] = (byte) ((value >> 8) & 0xff);
        mValue[mLength++] = (byte) ((value >> 16) & 0xff);
        mValue[mLength++] = (byte) ((value >> 24) & 0xff);
    }


    /**
     * Write a character.
     *
     * @param value
     */
    public void writeChar(int value) {
        mValue[mLength++] = (byte) (value & 0xff);
    }

    public int getLength() {
        return mLength - 1;
    }

    public static String getReadableStringFromByteArray(byte[] cardID) {
        String result = "";

        for (byte b : cardID) {
            result += String.format("%02X ", b);
        }

        return result;
    }

    public byte[] getValue() {
        byte[] _value = new byte[mLength];
        for (int i=0; i< mLength;i++) {
            _value[i]=mValue[i];
        }
        return _value;
    }


    @Override
    public boolean replacesOperation(BluetoothOperation bluetoothOperation) {

        if ( bluetoothOperation instanceof  CharacteristicWriteOperation ) {
            CharacteristicWriteOperation characteristicWriteOperation = (CharacteristicWriteOperation) bluetoothOperation;
            return ByteUtils.equals(characteristicWriteOperation.getValue(),getValue());
        }

        return false;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("writeOperation: ");

        for (int i = 0; i < mValue.length && i < mLength; i++ ) {
            stringBuilder.append(String.format("%02X ", mValue[i]));
        }


        return stringBuilder.toString();
    }

    @Override
    public boolean perform(BluetoothGatt bluetoothGatt) {
        SSLog.d("Performing write to device");
        BluetoothGattService service = bluetoothGatt.getService(mServiceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(mCharacteristicUUID);
            if (characteristic != null) {
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                characteristic.setValue(getValue());
                SSLog.d("Writing value: %s", ConversionUtils.byteArrayToString(getValue()," "));

                bluetoothGatt.writeCharacteristic(characteristic);
            }
        }
        return false;
    }


}
