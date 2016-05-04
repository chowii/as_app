package au.com.ahbeard.sleepsense.model.beddit;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by neal on 20/04/2016.
 */
public class SleepStage {

    public enum Stage {
        Away,
        Restless,
        Sleep,
        Wake,
        NoSignal,
        Gap
    }


   /*
    * The following stages are defined:
    *
    * 65=away represents the start time of a period when the user is away from
    *    the sensor.
    * 82=restless represents restless sleep
    * 83=sleep represents plain sleep
    * 87=wake represents the start time of the period when the user is awake and
    *    present on top of the sensor.
    * 78=no signal represents a period of missing signal.
    * 71=gap represents a proposed division between two separate sleeps, e.g. day
    *    nap and night sleep. The separation is proposed when no signal is received
    *    for longer than four hours.
    */

    private double mTimestamp;
    private int mStage;

    public SleepStage(double timestamp, int stage) {
        mTimestamp = timestamp;
        mStage = stage;
    }

    public static int getLength() {
        return 9;
    }

    public static SleepStage create(int position, byte[] bytes) {
        double timestamp = ByteBuffer.wrap(bytes, position, 8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        int stage = bytes[position + 8];
        return new SleepStage(timestamp, stage);
    }

    public double getTimestamp() {
        return mTimestamp;
    }

    public int getRawStage() {
        return mStage;
    }

    public String getPresenceDescription() {

        switch (mStage) {
            case 65:
                return "away";
            case 82:
                return "restless";
            case 87:
                return "wake";
            case 83:
                return "sleep";
            case 78:
                return "no signal";
            case 71:
                return "gap";
            default:
                return "unknown";
        }

    }
    public Stage getStage() {

        switch (mStage) {
            case 65:
                return Stage.Away;
            case 82:
                return Stage.Restless;
            case 87:
                return Stage.Wake;
            case 83:
                return Stage.Sleep;
            case 78:
                return Stage.NoSignal;
            case 71:
                return Stage.Gap;
            default:
                return null;
        }

    }
}
