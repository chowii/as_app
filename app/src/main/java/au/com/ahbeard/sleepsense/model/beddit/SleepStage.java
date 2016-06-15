package au.com.ahbeard.sleepsense.model.beddit;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by neal on 20/04/2016.
 */
public class SleepStage {

    public boolean includeInGraph() {
        return getStage()==Stage.Away||getStage()==Stage.Wake;//||getStage()==Stage.Sleep||getStage()==Stage.Restless;
    }

    public enum Stage {
        Away,
        Restless,
        Sleep,
        Wake,
        NoSignal,
        Gap,
        Unknown
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
    private Stage mCalculateStage;

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

        if ( mCalculateStage == null ) {
            switch (mStage) {
                case 65:
                    mCalculateStage = Stage.Away;
                    break;
                case 82:
                    mCalculateStage =  Stage.Restless;
                    break;
                case 87:
                    mCalculateStage =  Stage.Wake;
                    break;
                case 83:
                    mCalculateStage =  Stage.Sleep;
                    break;
                case 78:
                    mCalculateStage =  Stage.NoSignal;
                    break;
                case 71:
                    mCalculateStage =  Stage.Gap;
                    break;
                default:
                    mCalculateStage =  Stage.Unknown;
                    break;
            }
        }

        return mCalculateStage;


    }
}
