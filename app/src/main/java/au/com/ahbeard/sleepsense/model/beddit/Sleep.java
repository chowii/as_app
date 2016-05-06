package au.com.ahbeard.sleepsense.model.beddit;

import com.beddit.analysis.BatchAnalysisResult;
import com.beddit.analysis.CalendarDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * This is the local version of the batch analysis result.
 * <p>
 * Created by neal on 3/05/2016.
 */
public class Sleep {

    // The sleep_id of a sleep is based on the mYear * 1000 + mMonth * 100 + mDay.
    private long mSleepId;

    private TimeZone mTimeZone;

    private double mStartTime;
    private double mEndTime;

    private int mDay;
    private int mMonth;
    private int mYear;
    private int mDayOfWeek;

    private List<SleepStage> mSleepStages;

    private List<SleepCycle> mSleepCycles;
    private List<Actigram> mActigrams;
    private List<HeartRate> mHeartRates;
    private List<SnoringEpisode> mSnoringEpisodes;

    private List<String> mTags;

    // resting_heart_rate
    private Float mRestingHeartRate;

    // average_respiration_rate
    private Float mAverageRespirationRate;

    // sleep_time_target
    private Float mSleepTimeTarget;

    // sleep_latency
    private Float mSleepLatency;

    // sleep_efficiency
    private Float mSleepEfficiency;

    // primary_sleep_period_away_episode_count
    private Float mPrimarySleepPeriodAwayEpiodeCount;

    // primary_sleep_period_away_episode_duration
    private Float mPrimarySleepPeriodAwayEpisodeDuration;

    // total_snoring_episode_duration
    private Float mTotalSnoringEpisodeDuration;

    // stage_duration_A
    private Float mAwayTotalTime;

    // stage_duration_S
    private Float mSleepTotalTime;

    // stage_duration_R
    private Float mRestlessTotalTime;

    // stage_duration_W
    private Float mWakeTotalTime;

    // stage_duration_G
    private Float mGapTotalTime;

    // stage_duration_N
    private Float mMissingSignalTotalTime;

    // total_nap_duration
    private Float mTotalNapDuration;

    // activity_index
    private Float mActivityIndex;

    // evening_HRV_index
    private Float mEveningHRVIndex;

    // morning_HRV_index
    private Float mMorningHRVIndex;

    // all_night_HRV_index
    private Float mAllNightHRVIndex;

    // resting_HRV_index
    private Float mRestingHRVIndex;

    // total_sleep_score
    private Float mTotalSleepScore;

    // sleep_score_version
    private Float mSleepScoreVersion;

    public long getSleepId() {
        return mSleepId;
    }

    public int getDay() {
        return mDay;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() {
        return mYear;
    }

    public int getDayOfWeek() {
        return mDayOfWeek;
    }

    public List<Actigram> getActigrams() {
        return mActigrams;
    }

    public List<HeartRate> getHeartRates() {
        return mHeartRates;
    }

    public List<SleepCycle> getSleepCycles() {
        return mSleepCycles;
    }

    public List<SleepStage> getSleepStages() {
        return mSleepStages;
    }

    public List<SnoringEpisode> getSnoringEpisodes() {
        return mSnoringEpisodes;
    }

    public TimeZone getTimeZone() {
        return mTimeZone;
    }

    public double getStartTime() {
        return mStartTime;
    }

    public double getEndTime() {
        return mEndTime;
    }

    public Float getRestingHeartRate() {
        return mRestingHeartRate;
    }

    public Float getAverageRespirationRate() {
        return mAverageRespirationRate;
    }

    public Float getSleepTimeTarget() {
        return mSleepTimeTarget;
    }

    public Float getSleepLatency() {
        return mSleepLatency;
    }

    public Float getSleepEfficiency() {
        return mSleepEfficiency;
    }

    public Float getPrimarySleepPeriodAwayEpiodeCount() {
        return mPrimarySleepPeriodAwayEpiodeCount;
    }

    public Float getPrimarySleepPeriodAwayEpisodeDuration() {
        return mPrimarySleepPeriodAwayEpisodeDuration;
    }

    public Float getTotalSnoringEpisodeDuration() {
        return mTotalSnoringEpisodeDuration;
    }

    public Float getAwayTotalTime() {
        return mAwayTotalTime;
    }

    public Float getSleepTotalTime() {
        return mSleepTotalTime;
    }

    public Float getRestlessTotalTime() {
        return mRestlessTotalTime;
    }

    public Float getWakeTotalTime() {
        return mWakeTotalTime;
    }

    public Float getGapTotalTime() {
        return mGapTotalTime;
    }

    public Float getMissingSignalTotalTime() {
        return mMissingSignalTotalTime;
    }

    public Float getTotalNapDuration() {
        return mTotalNapDuration;
    }

    public Float getActivityIndex() {
        return mActivityIndex;
    }

    public Float getEveningHRVIndex() {
        return mEveningHRVIndex;
    }

    public Float getMorningHRVIndex() {
        return mMorningHRVIndex;
    }

    public Float getAllNightHRVIndex() {
        return mAllNightHRVIndex;
    }

    public float getRestingHRVIndex() {
        return mRestingHRVIndex;
    }

    public Float getTotalSleepScore() {
        return mTotalSleepScore;
    }

    public Float getSleepScoreVersion() {
        return mSleepScoreVersion;
    }

    public List<String> getTags() {
        return mTags;
    }

    public static Sleep fromBatchAnalysisResult(BatchAnalysisResult batchAnalysisResult) {

        Sleep sleep = new Sleep();

        sleep.mDay = batchAnalysisResult.getDate().getDay();
        sleep.mMonth = batchAnalysisResult.getDate().getMonth();
        sleep.mYear = batchAnalysisResult.getDate().getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(sleep.mDay,sleep.mMonth,sleep.mYear);

        sleep.mDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        sleep.mSleepId = sleep.mYear * 10000 + sleep.mMonth * 100 + sleep.mDay;

        sleep.mStartTime = batchAnalysisResult.getStartTime();
        sleep.mEndTime = batchAnalysisResult.getEndTime();
        sleep.mTimeZone = TimeZone.getDefault();

        sleep.mRestingHeartRate = getProperty("resting_heart_rate", batchAnalysisResult);
        sleep.mAverageRespirationRate = getProperty("average_respiration_rate", batchAnalysisResult);
        sleep.mSleepTimeTarget = getProperty("sleep_time_target", batchAnalysisResult);
        sleep.mSleepLatency = getProperty("sleep_latency", batchAnalysisResult);
        sleep.mSleepEfficiency = getProperty("sleep_efficiency", batchAnalysisResult);
        sleep.mPrimarySleepPeriodAwayEpiodeCount = getProperty("primary_sleep_period_away_episode_count", batchAnalysisResult);
        sleep.mPrimarySleepPeriodAwayEpisodeDuration = getProperty("primary_sleep_period_away_episode_duration", batchAnalysisResult);
        sleep.mTotalSnoringEpisodeDuration = getProperty("total_snoring_episode_duration", batchAnalysisResult);
        sleep.mAwayTotalTime = getProperty("stage_duration_A", batchAnalysisResult);
        sleep.mSleepTotalTime = getProperty("stage_duration_S", batchAnalysisResult);
        sleep.mRestlessTotalTime = getProperty("stage_duration_R", batchAnalysisResult);
        sleep.mWakeTotalTime = getProperty("stage_duration_W", batchAnalysisResult);
        sleep.mGapTotalTime = getProperty("stage_duration_G", batchAnalysisResult);
        sleep.mMissingSignalTotalTime = getProperty("stage_duration_N", batchAnalysisResult);
        sleep.mTotalNapDuration = getProperty("total_nap_duration", batchAnalysisResult);
        sleep.mActivityIndex = getProperty("activity_index", batchAnalysisResult);
        sleep.mEveningHRVIndex = getProperty("evening_HRV_index", batchAnalysisResult);
        sleep.mMorningHRVIndex = getProperty("morning_HRV_index", batchAnalysisResult);
        sleep.mAllNightHRVIndex = getProperty("all_night_HRV_index", batchAnalysisResult);
        sleep.mRestingHRVIndex = getProperty("resting_HRV_index", batchAnalysisResult);
        sleep.mTotalSleepScore = getProperty("total_sleep_score", batchAnalysisResult);
        sleep.mSleepScoreVersion = getProperty("sleep_score_version", batchAnalysisResult);

        sleep.mSleepStages = new ArrayList<>();

        if ( batchAnalysisResult.getTimeValueData("sleep_stages") != null ) {

            int position = 0;
            byte[] bytes = batchAnalysisResult.getTimeValueData("sleep_stages").getData();

            while (position < bytes.length) {
                sleep.mSleepStages.add(SleepStage.create(position,bytes));
                position+=SleepStage.getLength();
            }

        }

        sleep.mSleepCycles = new ArrayList<>();

        if ( batchAnalysisResult.getTimeValueData("sleep_cycles") != null ) {

            int position = 0;
            byte[] bytes = batchAnalysisResult.getTimeValueData("sleep_cycles").getData();

            while (position < bytes.length) {
                sleep.mSleepCycles.add(SleepCycle.create(position,bytes));
                position+=SleepCycle.getLength();
            }

        }

        sleep.mHeartRates = new ArrayList<>();

        if ( batchAnalysisResult.getTimeValueData("heart_rate_curve") != null ) {

            int position = 0;
            byte[] bytes = batchAnalysisResult.getTimeValueData("heart_rate_curve").getData();

            while (position < bytes.length) {
                sleep.mHeartRates.add(HeartRate.create(position,bytes));
                position+=HeartRate.getLength();
            }

        }

        sleep.mActigrams = new ArrayList<>();

        if ( batchAnalysisResult.getTimeValueData("actigram_epochwise") != null ) {

            int position = 0;
            byte[] bytes = batchAnalysisResult.getTimeValueData("actigram_epochwise").getData();

            while (position < bytes.length) {
                sleep.mActigrams.add(Actigram.create(position,bytes));
                position+=Actigram.getLength();
            }

        }

        sleep.mSnoringEpisodes = new ArrayList<>();

        if ( batchAnalysisResult.getTimeValueData("snoring_episodes") != null ) {

            int position = 0;
            byte[] bytes = batchAnalysisResult.getTimeValueData("snoring_episodes").getData();

            while (position < bytes.length) {
                sleep.mSnoringEpisodes.add(SnoringEpisode.create(position,bytes));
                position+=SnoringEpisode.getLength();
            }

        }

        sleep.mTags = batchAnalysisResult.getTags();

        return sleep;
    }

    private static Float getProperty(String name, BatchAnalysisResult batchAnalysisResult) {
        if (batchAnalysisResult.getPropertyNames().contains(name)) {
            return batchAnalysisResult.getProperty(name);
        } else {
            return null;
        }
    }

}
