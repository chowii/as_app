package au.com.ahbeard.sleepsense.utils;

/**
 * Created by neal on 9/12/2015.
 */
public class ByteUtils {

    public static boolean equals(byte[] lhs, byte[] rhs) {

        if ( lhs == null && rhs == null ) {
            return true;
        } else if ( lhs == null || rhs == null) {
            return false;
        } else if ( lhs.length != rhs.length ) {
            return false;
        } else {

            for ( int i=0; i < lhs.length; i++ ) {
                if ( lhs[i] != rhs[i] ) {
                    return false;
                }
            }

            return true;
        }

    }

    static public int generateChecksumCRC16(byte bytes[]) {

        int crc = 0xFFFF;

        for (int j = 0; j < bytes.length ; j++) {
            crc = ((crc  >>> 8) | (crc  << 8) )& 0xffff;
            crc ^= (bytes[j] & 0xff);//byte to int, trunc sign
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xFF) << 5) & 0xffff;
        }
        crc &= 0xffff;
        return crc;

    }

    static public int readLEUint8(int position, byte[] value) {

        int decodeValue = 0;

        decodeValue+= ((int)value[position]) & 0xff;

        return decodeValue;

    }

    static public int readLEUint16(int position, byte[] value) {

        int decodeValue = 0;

        decodeValue+= ((int)value[position]) & 0xff;
        decodeValue+= (((int)value[position+1]) & 0xff ) << 8;

        return decodeValue;

    }

    static public int readLEUint32(int position, byte[] value) {

        int decodeValue = 0;

        decodeValue+= ((int)value[position]) & 0xff;
        decodeValue+= (((int)value[position+1]) & 0xff) << 8;
        decodeValue+= (((int)value[position+2]) & 0xff) << 16;
        decodeValue+= (((int)value[position+3]) & 0xff) << 24;

        return decodeValue;

    }

}
