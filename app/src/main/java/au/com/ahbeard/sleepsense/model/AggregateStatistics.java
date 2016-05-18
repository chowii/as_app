package au.com.ahbeard.sleepsense.model;

import java.util.Calendar;

/**
 * Created by neal on 17/05/2016.
 */
public class AggregateStatistics {

    private Float mAverageSleepScore;
    private Calendar mOptimalBedtime;
    private Integer mOptimalMattressFirmness;
    private Calendar mBestNight;
    private Calendar mWorstNight;

    public Float getAverageSleepScore() {
        return mAverageSleepScore;
    }

    public void setAverageSleepScore(Float averageSleepScore) {
        mAverageSleepScore = averageSleepScore;
    }

    public Calendar getOptimalBedtime() {
        return mOptimalBedtime;
    }

    public void setOptimalBedtime(Calendar optimalBedtime) {
        mOptimalBedtime = optimalBedtime;
    }

    public Integer getOptimalMattressFirmness() {
        return mOptimalMattressFirmness;
    }

    public void setOptimalMattressFirmness(Integer optimalMattressFirmness) {
        mOptimalMattressFirmness = optimalMattressFirmness;
    }

    public Calendar getBestNight() {
        return mBestNight;
    }

    public void setBestNight(Calendar bestNight) {
        mBestNight = bestNight;
    }

    public Calendar getWorstNight() {
        return mWorstNight;
    }

    public void setWorstNight(Calendar worstNight) {
        mWorstNight = worstNight;
    }
}
