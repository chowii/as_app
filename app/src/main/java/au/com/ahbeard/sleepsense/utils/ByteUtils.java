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

}
