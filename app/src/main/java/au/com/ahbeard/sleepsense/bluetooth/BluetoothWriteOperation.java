package au.com.ahbeard.sleepsense.bluetooth;

/**
 * Created by neal on 14/01/2014.
 */
public class BluetoothWriteOperation extends BluetoothOperation {

    private byte[] mRequest;
    private int mEnd;
    private boolean mAlign;

    public BluetoothWriteOperation() {
        mRequest = new byte[15];
        mEnd = 0;
    }

    public void writeByte(int value) {
        mRequest[mEnd++] = (byte)(value & 0xff);
    }

    public void writeShort(int value) {

        // Align on 2 byte boundary.
        if ( mAlign && mEnd % 2 != 0 ) {
            mRequest[mEnd++] = 0x00;
        }

        mRequest[mEnd++] = (byte)(value & 0xff);
        mRequest[mEnd++] = (byte)((value >> 8) & 0xff);
    }

    public void writeShort(int location, int value) {

        // Align on 2 byte boundary.
        if (mAlign && location % 2 == 0) {
            location++;
        }

        mRequest[location++] = (byte)(value & 0xff);
        mRequest[location] = (byte)((value >> 8) & 0xff);

    }

    public void writeBytes( byte[] bytes ) {
        for ( int i=0; i < bytes.length; i++  ) {
            mRequest[mEnd++] = bytes[i];
        }
    }

    public void writeUInt32(int value) {
        mRequest[mEnd++]=(byte)(value & 0xff);
        mRequest[mEnd++]=(byte)((value >> 8) & 0xff);
        mRequest[mEnd++]=(byte)((value >> 16) & 0xff);
        mRequest[mEnd++]=(byte)((value >> 24) & 0xff);
    }


    public void writeChar(int value) {
        mRequest[mEnd++] = (byte)(value & 0xff);
    }

    public int getLength() {
        return mEnd -1;
    }

    public static String getReadableStringFromByteArray(byte[] cardID) {
        String result = "";

        for (byte b : cardID) {
            result += String.format("%02X ", b);
        }

        return result;
    }

    public byte[] getRequest() {
        return mRequest;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("writeOperation: ");

        for (byte b : mRequest) {
            stringBuilder.append(String.format("%02X ", b));
        }

        return stringBuilder.toString();
    }

}
