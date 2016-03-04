package au.com.ahbeard.sleepsense.bluetooth;

/**
 * Created by neal on 14/01/2014.
 */
public class BluetoothCommand {

    private byte[] mRequest;
    private byte[] mResponse;
    private int mEnd;
    private boolean mAlign;

    public BluetoothCommand() {
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

    public void setRequest(byte[] request) {
        mRequest = request;
    }

    public byte[] getResponse() {
        return mResponse;
    }

    public void setResponse(byte[] response) {
        mResponse = response;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("request: ");

        for (byte b : mRequest) {
            stringBuilder.append(String.format("%02X ", b));
        }

        stringBuilder.append("response: ");

        if ( mResponse == null ) {
            stringBuilder.append("NULL");
        } else {
            for (byte b : mResponse) {
                stringBuilder.append(String.format("%02X ", b));
            }
        }

        return stringBuilder.toString();
    }


    /**
     * @param node
     * @return
     */
    public static BluetoothCommand toggleAngles(int node) {

        BluetoothCommand command = new BluetoothCommand();

        command.writeByte(0x30);
        command.writeByte(node);
        command.writeByte(0x02);

        // command.appendChecksum();

        return command;

    }

    /**
     * @param node
     * @return
     */
    public static BluetoothCommand angles(int node, int state) {

        BluetoothCommand command = new BluetoothCommand();

        command.writeByte(0x30);
        command.writeByte(node);
        command.writeByte(state);

        // command.appendChecksum();

        return command;

    }

    /**
     * @param node
     * @return
     */
    public static BluetoothCommand setMotor(int node, int scaling, int repeats, int[][] levels) {

        BluetoothCommand command = new BluetoothCommand();

        command.writeByte(0x31);
        command.writeByte(node);
        command.writeByte((scaling & 0x0f) + ((repeats & 0x0f) << 4));

        for (int m = 0; m < levels.length; m++) {

            int packedLevels = 0;

            for (int i = 0; i < levels[m].length && i < 8; i++) {

                packedLevels += (levels[m][i] & 0x07) << (3 * i);

            }

            command.writeByte(packedLevels & 0xff);
            command.writeByte((packedLevels >> 8) & 0xff);
            command.writeByte((packedLevels >> 16) & 0xff);
        }


        return command;

    }

    private static int[] EMPTY_PATTERN = {0, 0, 0, 0, 0, 0, 0, 0};

    /**
     * @param node
     * @return
     */
    public static BluetoothCommand setMotor(int node, int scaling, int repeats, int motor, int[] singleLevels) {
        int[][] levels = new int[4][];

        for (int i = 0; i < levels.length; i++) {
            levels[i] = i == motor ? singleLevels : EMPTY_PATTERN;
        }

        return setMotor(node,scaling,repeats,levels);
    }

}
