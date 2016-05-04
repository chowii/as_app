package au.com.ahbeard.sleepsense.model.beddit;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by neal on 20/04/2016.
 */
public class Actigram {


    private double mTimestamp;
    private float mActigram;

    public Actigram(double timestamp, float actigram) {
        mTimestamp = timestamp;
        mActigram = actigram;
    }

    public static int getLength() {
        return 12;
    }

    public static Actigram create(int position, byte[] bytes) {
        double timestamp = ByteBuffer.wrap(bytes,position,8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        float actigram = ByteBuffer.wrap(bytes,position+8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return new Actigram(timestamp,actigram);
    }

    public double getTimestamp() {
        return mTimestamp;
    }

    public float getActigram() {
        return mActigram;
    }


}
