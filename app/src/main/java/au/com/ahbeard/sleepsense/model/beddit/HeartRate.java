package au.com.ahbeard.sleepsense.model.beddit;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by neal on 20/04/2016.
 */
public class HeartRate {

    private double mTimestamp;
    private float mHeartRate;

    public HeartRate(double timestamp, float cycle) {
        mTimestamp = timestamp;
        mHeartRate = cycle;
    }

    public static int getLength() {
        return 12;
    }

    public static HeartRate create(int position, byte[] bytes) {
        double timestamp = ByteBuffer.wrap(bytes,position,8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        float heartRate = ByteBuffer.wrap(bytes,position+8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return new HeartRate(timestamp,heartRate);
    }

    public double getTimestamp() {
        return mTimestamp;
    }

    public float getHeartRate() {
        return mHeartRate;
    }


}
