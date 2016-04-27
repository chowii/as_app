package au.com.ahbeard.sleepsense.model.beddit;

/**
 * Created by neal on 20/04/2016.
 */
public class SleepStage {


    /*

    The following stages are defined:
* 65=away represents the start time of a period when the user is away from
  the sensor.
* 82=restless represents restless sleep
* 83=sleep represents plain sleep
* 87=wake represents the start time of the period when the user is awake and
  present on top of the sensor.
* 78=no signal represents a period of missing signal.
* 71=gap represents a proposed division between two separate sleeps, e.g. day
  nap and night sleep. The separation is proposed when no signal is received
  for longer than four hours.

     */

    private double mTimestamp;
    private int mPresence;

    public SleepStage(double timestamp, int presence) {
        mTimestamp = timestamp;
        mPresence = presence;
    }

    public double getTimestamp() {
        return mTimestamp;
    }

    public int getPresence() {
        return mPresence;
    }

    public String getPresenceDescription() {

        switch (mPresence) {
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
}
