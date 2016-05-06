package au.com.ahbeard.sleepsense.model.beddit;

/**
 * Created by neal on 6/05/2016.
 */
public class SleepProperty {

    private int mSleepId;
    private float mValue;

    public SleepProperty(int sleepId, float value) {
        mSleepId = sleepId;
        mValue = value;
    }

    public int getSleepId() {
        return mSleepId;
    }

    public float getValue() {
        return mValue;
    }
}
