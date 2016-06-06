package au.com.ahbeard.sleepsense.model.beddit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.beddit.analysis.BatchAnalysisResult;
import com.beddit.analysis.TimeValueTrackFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import au.com.ahbeard.sleepsense.services.SleepContract;
import au.com.ahbeard.sleepsense.services.SleepSQLiteHelper;

/**
 * This is the local version of the batch analysis result.
 * <p/>
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

    // resting_HRV_index
    private Float mMattressFirmness;

    // total_sleep_score
    Float mTotalSleepScore;

    // sleep_score_version
    Float mSleepScoreVersion;

    Map<String, TrackData> mTrackDataByName;

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

    public Float getRestingHRVIndex() {
        return mRestingHRVIndex;
    }

    public Float getTotalSleepScore() {
        return mTotalSleepScore;
    }

    public void setTotalSleepScore(Float totalSleepScore) {
        mTotalSleepScore = totalSleepScore;
    }

    public void setSleepScoreVersion(Float sleepScoreVersion) {
        mSleepScoreVersion = sleepScoreVersion;
    }

    public Float getSleepScoreVersion() {
        return mSleepScoreVersion;
    }

    public Float getMattressFirmness() {
        return mMattressFirmness;
    }

    public Map<String, TrackData> getTrackDataByName() {
        return mTrackDataByName;
    }

    public List<String> getTags() {
        return mTags;
    }

    /**
     * Create a sleep given a batch analysis result.
     *
     * @param batchAnalysisResult
     * @return
     */
    public static Sleep fromBatchAnalysisResult(BatchAnalysisResult batchAnalysisResult) {

        Sleep sleep = new Sleep();

        sleep.mDay = batchAnalysisResult.getDate().getDay();
        sleep.mMonth = batchAnalysisResult.getDate().getMonth();
        sleep.mYear = batchAnalysisResult.getDate().getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(sleep.mYear, sleep.mMonth - 1, sleep.mDay, 0, 0, 0);

        Log.d("Sleep", "calendar: " + calendar + " " + calendar.getTime());

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

        sleep.mTrackDataByName = new HashMap<>();

        for (String name : batchAnalysisResult.getTimeValueDataNames()) {
            TimeValueTrackFragment trackData = batchAnalysisResult.getTimeValueData(name);
            sleep.mTrackDataByName.put(name, new TrackData(name, trackData.getItemType(), trackData.getData()));
        }

        sleep.mTags = new ArrayList<>(batchAnalysisResult.getTags());

        return sleep;
    }

    /**
     * Convenience method to read multiple sleeps from the database.  Really need to move this somewhere else, but it's useful here.
     * This requires 3 queries to read a range of sleeps.
     *
     * @param sleepSQLiteHelper
     * @return
     */
    public static Sleep fromDatabase(SleepSQLiteHelper sleepSQLiteHelper, long sleepId) {
        Map<Long,Sleep> sleepBySleepId = fromDatabase(sleepSQLiteHelper,sleepId,sleepId);

        if ( sleepBySleepId.isEmpty() ) {
            return null;
        } else {
            return new ArrayList<>(sleepBySleepId.values()).get(0);
        }
    }

    /**
     * Convenience method to read multiple sleeps from the database.  Really need to move this somewhere else, but it's useful here.
     * This requires 3 queries to read a range of sleeps.
     *
     * @param sleepSQLiteHelper
     * @return
     */
    public static Map<Long,Sleep> fromDatabase(SleepSQLiteHelper sleepSQLiteHelper, long startSleepId, long endSleepId) {

        Map<Long,Sleep> sleepBySleepId = new HashMap<>();

        SQLiteDatabase database = null;

        try {

            database = sleepSQLiteHelper.getReadableDatabase();

            Cursor cursor = null;

            try {

                cursor = database.rawQuery("select * from " + SleepContract.Sleep.TABLE_NAME +
                        " where " + SleepContract.Sleep.SLEEP_ID + " >= ? AND " + SleepContract.Sleep.SLEEP_ID + " <= ?" +
                        " ORDER BY " + SleepContract.Sleep.SLEEP_ID + " ASC",
                        new String[]{Long.toString(startSleepId),Long.toString(endSleepId)});
                cursor.moveToFirst();

                if ( ! cursor.isAfterLast() ) {

                    Sleep sleep = getSleepFromCursor(cursor);

                    sleepBySleepId.put(sleep.getSleepId(),sleep);
                }


            } finally {
                if ( cursor != null) {
                    cursor.close();
                }
            }

            Cursor trackCursor = null;

            try {

                trackCursor = database.rawQuery("select * from " + SleepContract.SleepTracks.TABLE_NAME +
                                " where " + SleepContract.Sleep.SLEEP_ID + " >= ? AND " + SleepContract.Sleep.SLEEP_ID + " <= ?" +
                                " ORDER BY " + SleepContract.Sleep.SLEEP_ID + " ASC",
                        new String[]{Long.toString(startSleepId),Long.toString(endSleepId)});

                trackCursor.moveToFirst();

                if ( ! trackCursor.isAfterLast() )  {

                    long sleepId = trackCursor.getLong(trackCursor.getColumnIndex(SleepContract.Sleep.SLEEP_ID));
                    String name = trackCursor.getString(trackCursor.getColumnIndex(SleepContract.SleepTracks.TRACK_NAME));
                    String dataType = trackCursor.getString(trackCursor.getColumnIndex(SleepContract.SleepTracks.TRACK_DATA_TYPE));
                    byte[] data = trackCursor.getBlob(trackCursor.getColumnIndex(SleepContract.SleepTracks.TRACK_DATA));

                    Sleep sleep = sleepBySleepId.get(sleepId);

                    sleep.mTrackDataByName.put(name, new TrackData(name,dataType,data));

                    trackCursor.moveToNext();
                }

            } finally {
                if ( trackCursor!=null) {
                    trackCursor.close();
                }
            }


        } finally {
            // Don't close the DB connection.
        }

        return sleepBySleepId;

    }

    /**
     * Create a sleep given the cursor with the row for a sleep.
     *
     * @param cursor
     * @return
     */
    @NonNull
    private static Sleep getSleepFromCursor(Cursor cursor) {

        Sleep sleep = new Sleep();

        sleep.mSleepId = cursor.getLong(cursor.getColumnIndex(SleepContract.Sleep.SLEEP_ID));
        sleep.mDay = cursor.getInt(cursor.getColumnIndex(SleepContract.Sleep.DAY));
        sleep.mMonth = cursor.getInt(cursor.getColumnIndex(SleepContract.Sleep.MONTH));
        sleep.mYear = cursor.getInt(cursor.getColumnIndex(SleepContract.Sleep.YEAR));
        sleep.mDayOfWeek = cursor.getInt(cursor.getColumnIndex(SleepContract.Sleep.DAY_OF_WEEK));
        sleep.mStartTime = cursor.getDouble(cursor.getColumnIndex(SleepContract.Sleep.START_TIME));
        sleep.mEndTime = cursor.getDouble(cursor.getColumnIndex(SleepContract.Sleep.END_TIME));
        sleep.mTimeZone = TimeZone.getTimeZone(cursor.getString(cursor.getColumnIndex(SleepContract.Sleep.TIME_ZONE)));

        // TODO: Add null check for column.
        sleep.mRestingHeartRate = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.RESTING_HEART_RATE));
        sleep.mAverageRespirationRate = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.AVERAGE_RESPIRATION_RATE));
        sleep.mSleepTimeTarget = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.SLEEP_TIME_TARGET));
        sleep.mSleepLatency = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.SLEEP_LATENCY));
        sleep.mSleepEfficiency = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.SLEEP_EFFICIENCY));
        sleep.mPrimarySleepPeriodAwayEpiodeCount = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.AWAY_EPISODE_COUNT));
        sleep.mPrimarySleepPeriodAwayEpisodeDuration = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.AWAY_EPISODE_DURATION));
        sleep.mTotalSnoringEpisodeDuration = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.SNORING_EPISODE_DURATION));
        sleep.mAwayTotalTime = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.AWAY_TOTAL_TIME));
        sleep.mSleepTotalTime = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.SLEEP_TOTAL_TIME));
        sleep.mRestlessTotalTime = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.RESTLESS_TOTAL_TIME));
        sleep.mWakeTotalTime = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.WAKE_TOTAL_TIME));
        sleep.mGapTotalTime = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.GAP_TOTAL_TIME));
        sleep.mMissingSignalTotalTime = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.MISSING_SIGNAL_TOTAL_TIME));
        sleep.mTotalNapDuration = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.TOTAL_NAP_DURATION));
        sleep.mActivityIndex = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.ACTIVITY_INDEX));
        sleep.mEveningHRVIndex = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.EVENING_HRV_INDEX));
        sleep.mMorningHRVIndex = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.MORNING_HRV_INDEX));
        sleep.mAllNightHRVIndex = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.ALL_NIGHT_HRV_INDEX));
        sleep.mRestingHRVIndex = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.RESTING_HRV_INDEX));
        sleep.mTotalSleepScore = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.TOTAL_SLEEP_SCORE));
        sleep.mSleepScoreVersion = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.SLEEP_SCORE_VERSION));
        sleep.mMattressFirmness = cursor.getFloat(cursor.getColumnIndex(SleepContract.Sleep.MATTRESS_FIRMNESS));

        sleep.mTrackDataByName = new HashMap<>();

        return sleep;
    }

    private static Float getProperty(String name, BatchAnalysisResult batchAnalysisResult) {
        if (batchAnalysisResult.getPropertyNames().contains(name)) {
            return batchAnalysisResult.getProperty(name);
        } else {
            return null;
        }
    }

    public List<SleepStage> getSleepStages() {

        List<SleepStage> sleepStages = new ArrayList<>();

        if (mTrackDataByName.containsKey("sleep_stages")) {

            int position = 0;
            byte[] bytes = mTrackDataByName.get("sleep_stages").getData();

            while (position < bytes.length) {
                sleepStages.add(SleepStage.create(position, bytes));
                position += SleepStage.getLength();
            }

        }

        return sleepStages;


    }

    public List<SnoringEpisode> getSnoringEpisodes() {

        List<SnoringEpisode> snoringEpisodes = new ArrayList<>();

        if (mTrackDataByName.containsKey("snoring_episodes")) {

            int position = 0;
            byte[] bytes = mTrackDataByName.get("snoring_episodes").getData();

            while (position < bytes.length) {
                snoringEpisodes.add(SnoringEpisode.create(position, bytes));
                position += SnoringEpisode.getLength();
            }

        }

        return snoringEpisodes;

    }

    public List<SleepCycle> getSleepCycles() {

        List<SleepCycle> sleepCycles = new ArrayList<>();

        if (mTrackDataByName.containsKey("sleep_cycles")) {

            int position = 0;
            byte[] bytes = mTrackDataByName.get("sleep_cycles").getData();

            while (position < bytes.length) {
                sleepCycles.add(SleepCycle.create(position, bytes));
                position += SleepCycle.getLength();
            }

        }

        return sleepCycles;
    }


    public List<HeartRate> getHeartRates() {

        List<HeartRate> heartRates = new ArrayList<>();

        if (mTrackDataByName.containsKey("heart_rate_curve")) {

            int position = 0;
            byte[] bytes = mTrackDataByName.get("heart_rate_curve").getData();

            while (position < bytes.length) {
                heartRates.add(HeartRate.create(position, bytes));
                position += HeartRate.getLength();
            }

        }

        return heartRates;

    }

    public List<Actigram> getActigrams() {
        List<Actigram> actigrams = new ArrayList<>();

        if (mTrackDataByName.containsKey("actigram_epochwise")) {

            int position = 0;
            byte[] bytes = mTrackDataByName.get("actigram_epochwise").getData();

            while (position < bytes.length) {
                actigrams.add(Actigram.create(position, bytes));
                position += Actigram.getLength();
            }

        }

        return actigrams;

    }

    public static Sleep generateRandom(int sleepId, Random random) {
        Sleep sleep = new Sleep();
        sleep.mSleepId=sleepId;
        sleep.mTotalSleepScore = random.nextFloat() * 60f + 20f;
        sleep.mSleepScoreVersion=666.0f;
        return sleep;
    }
}
