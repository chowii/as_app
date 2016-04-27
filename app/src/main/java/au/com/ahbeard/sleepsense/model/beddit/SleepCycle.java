package au.com.ahbeard.sleepsense.model.beddit;

/**
 * Created by neal on 20/04/2016.
 */
public class SleepCycle {


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
    private float mCycle;

    public SleepCycle(double timestamp, float cycle) {
        mTimestamp = timestamp;
        mCycle = cycle;
    }

    public double getTimestamp() {
        return mTimestamp;
    }

    public float getCycle() {
        return mCycle;
    }


}
