package au.com.ahbeard.sleepsense.model.beddit;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The timestamp represents the time of a snoring episode and the value is the length of the
 * episode. The snoring episodes give a high-level view of snoring activity and
 * have around 10-minute resolution.
 */
public class SnoringEpisode {

    private double mTimestamp;
    private float mValue;

    public SnoringEpisode(double timestamp, float value) {
        mTimestamp = timestamp;
        mValue = value;
    }

    public static int getLength() {
        return 12;
    }

    public static SnoringEpisode create(int position, byte[] bytes) {
        double timestamp = ByteBuffer.wrap(bytes, position, 8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        float heartRate = ByteBuffer.wrap(bytes, position + 8, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return new SnoringEpisode(timestamp, heartRate);
    }

    public double getTimestamp() {
        return mTimestamp;
    }

    public float getValue() {
        return mValue;
    }


}
