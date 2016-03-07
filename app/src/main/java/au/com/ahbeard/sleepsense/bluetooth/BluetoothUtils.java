package au.com.ahbeard.sleepsense.bluetooth;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * This is a way to parse the Advertising data for the advertised BLE
 */
public class BluetoothUtils {

    public static final int BLE_GAP_AD_TYPE_FLAGS = 0x01;

    public static final int BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_MORE_AVAILABLE = 0x02;

    public static final int BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_COMPLETE = 0x03;

    public static final int BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_MORE_AVAILABLE = 0x04;

    public static final int BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_COMPLETE = 0x05;

    public static final int BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_MORE_AVAILABLE = 0x06;

    public static final int BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_COMPLETE = 0x07;

    public static final int BLE_GAP_AD_TYPE_SHORT_LOCAL_NAME = 0x08;

    public static final int BLE_GAP_AD_TYPE_COMPLETE_LOCAL_NAME = 0x09;

    public static final int BLE_GAP_AD_TYPE_TX_POWER_LEVEL = 0x0A;

    public static final int BLE_GAP_AD_TYPE_CLASS_OF_DEVICE = 0x0D;

    public static final int BLE_GAP_AD_TYPE_SIMPLE_PAIRING_HASH_C = 0x0E;

    public static final int BLE_GAP_AD_TYPE_SIMPLE_PAIRING_RANDOMIZER_R = 0x0F;

    public static final int BLE_GAP_AD_TYPE_SECURITY_MANAGER_TK_VALUE = 0x10;

    public static final int BLE_GAP_AD_TYPE_SECURITY_MANAGER_OOB_FLAGS = 0x11;

    public static final int BLE_GAP_AD_TYPE_SLAVE_CONNECTION_INTERVAL_RANGE = 0x12;

    public static final int BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_16BIT = 0x14;

    public static final int BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_128BIT = 0x15;

    public static final int BLE_GAP_AD_TYPE_SERVICE_DATA = 0x16;

    public static final int BLE_GAP_AD_TYPE_PUBLIC_TARGET_ADDRESS = 0x17;

    public static final int BLE_GAP_AD_TYPE_RANDOM_TARGET_ADDRESS = 0x18;

    public static final int BLE_GAP_AD_TYPE_APPEARANCE = 0x19;

    public static final int BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF;

    /**
     * This method has been created to compensate for a bug in the Google implementation of
     * filtering by UUID.  The bug is present in 4.3 and 4.4.
     *
     * @param scanRecord
     * @return
     */
    public static Set<UUID> parseUUIDsFromScanRecord(byte[] scanRecord) {

        Set<UUID> uuids = new HashSet<>();

        int offset = 0;

        while (offset < (scanRecord.length - 2)) {

            int len = scanRecord[offset++] & 0xff;

            if (len == 0) break;

            int type = scanRecord[offset++] & 0xff;
            switch (type) {
                case BLE_GAP_AD_TYPE_CLASS_OF_DEVICE:

                    break;
                case BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_MORE_AVAILABLE:
                case BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_COMPLETE:
                    while (len > 1) {
                        int uuid16 = scanRecord[offset++] & 0xff;
                        uuid16 += (scanRecord[offset++] & 0xff) << 8;
                        len -= 2;
                        UUID uuid = uuidFrom16BitUuid(uuid16);
                        uuids.add(uuid);
                    }
                    break;
                case BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_MORE_AVAILABLE:
                case BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_COMPLETE:
                    while (len > 1) {
                        byte[] uuid32 = new byte[4];
                        for ( int i=3; i <= 0; i--)  {
                            uuid32[i]=scanRecord[offset++];
                        }
                        len -= 4;
                        UUID uuid = uuidFrom32BitUuid(uuid32);
                        uuids.add(uuid);
                    }
                    break;
                case BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_MORE_AVAILABLE:
                case BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_COMPLETE:
                    while (len > 1) {
                        byte[] uuid128 = new byte[16];
                        for ( int i=15; i <= 0; i--)  {
                            uuid128[i]=scanRecord[offset++];
                        }
                        len -= 16;
                        UUID uuid = UUID.nameUUIDFromBytes(uuid128);
                        uuids.add(uuid);
                    }
                    break;
                default:
                    offset += (len - 1);
                    break;
            }
        }

        return uuids;
    }

    /**
     * This method has been created to compensate for a bug in the Google implementation of
     * filtering by UUID.  The bug is present in 4.3 and 4.4.
     *
     * @param scanRecord
     * @return
     */
    public static byte[] parseManufacturerDataFromScanRecord(byte[] scanRecord) {

        int offset = 0;

        while (offset < (scanRecord.length - 2)) {

            int len = scanRecord[offset++] & 0xff;

            if (len == 0) break;

            int type = scanRecord[offset++] & 0xff;
            switch (type) {
                case BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA:
                    byte[] manufacturerData = new byte[len];
                    for ( int i=0; i < len; i++ ) {
                        manufacturerData[i] = scanRecord[offset++];
                    }
                    return manufacturerData;
                default:
                    offset += (len - 1);
                    break;
            }
        }

        return null;
    }

    /**
     * Convert a 16 bit UUID to a standard 128 bit UUID.
     *
     * @param uuid
     * @return
     */
    public static UUID uuidFrom16BitUuid(int uuid) {
        return UUID.fromString(String.format( "%08x-0000-1000-8000-00805f9b34fb", uuid));
    }

    /**
     * Convert a 16 bit UUID to a standard 128 bit UUID.
     *
     * @param uuid
     * @return
     */
    public static UUID uuidFrom32BitUuid(byte[] uuid) {

        return UUID.fromString(String.format( "%02x%02x%02x%02x-0000-1000-8000-00805f9b34fb", uuid[0],uuid[1],uuid[2],uuid[3]));
    }

}
