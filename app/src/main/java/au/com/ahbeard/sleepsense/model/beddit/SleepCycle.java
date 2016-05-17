package au.com.ahbeard.sleepsense.model.beddit;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The timestamp and value represents the sleep depth at that time. The value is a floating
 * point number between 0.0 and 1.0. Value 0.0 corresponds to deepest
 * possible sleep and 1.0 to lightest possible sleep. There will be one
 * data point every 2 minutes, beginning from the moment the subject is
 * deemed to be sleeping and ending when the subject ultimately wakes up.
 */
public class SleepCycle implements TimestampAndFloat {


    private double mTimestamp;
    private float mCycle;

    public SleepCycle(double timestamp, float cycle) {
        mTimestamp = timestamp;
        mCycle = cycle;
    }

    public static int getLength() {
        return 12;
    }

    public static SleepCycle create(int position, byte[] bytes) {
        double timestamp = ByteBuffer.wrap(bytes,position,8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        float cycle = ByteBuffer.wrap(bytes,position+8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return new SleepCycle(timestamp,cycle);
    }

    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(mTimestamp).array());
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(mCycle).array());
    }

    public double getTimestamp() {
        return mTimestamp;
    }

    @Override
    public float getValue() {
        return mCycle;
    }

    @Override
    public void setValue(float value) {
        mCycle = value;
    }

    public float getCycle() {
        return mCycle;
    }


}
