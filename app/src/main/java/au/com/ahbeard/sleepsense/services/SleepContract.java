package au.com.ahbeard.sleepsense.services;

import android.provider.BaseColumns;

/**
 * Created by neal on 3/05/2016.
 */
public class SleepContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String LONG_TYPE = " LONG";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String BLOB_TYPE = " BLOB";


    private static final String COMMA_SEP = ",";

    /*
     * Inner class that defines the table contents.
     */
    public static abstract class Sleep implements BaseColumns {

        public static final String TABLE_NAME = "sleep";

        public static final String SLEEP_ID = "sleep_id";
        public static final String TIME_ZONE = "time_zone";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String DAY = "day";
        public static final String MONTH = "month";
        public static final String YEAR = "year";
        public static final String DAY_OF_WEEK = "day_of_week";
        public static final String RESTING_HEART_RATE = "resting_heart_rate";
        public static final String AVERAGE_RESPIRATION_RATE = "average_respiration_rate";
        public static final String SLEEP_TIME_TARGET = "sleep_time_target";
        public static final String SLEEP_LATENCY = "sleep_latency";
        public static final String SLEEP_EFFICIENCY = "sleep_efficiency";
        public static final String AWAY_EPISODE_COUNT = "away_episode_count";
        public static final String AWAY_EPISODE_DURATION = "away_episode_duration";
        public static final String SNORING_EPISODE_DURATION = "snoring_episode_duration";
        public static final String AWAY_TOTAL_TIME = "away_total_time";
        public static final String SLEEP_TOTAL_TIME = "sleep_total_time";
        public static final String RESTLESS_TOTAL_TIME = "restless_total_time";
        public static final String WAKE_TOTAL_TIME = "wake_total_time";
        public static final String GAP_TOTAL_TIME = "gap_total_time";
        public static final String MISSING_SIGNAL_TOTAL_TIME = "missing_signal_total_time";
        public static final String TOTAL_NAP_DURATION = "total_nap_duration";
        public static final String ACTIVITY_INDEX = "activity_index";
        public static final String EVENING_HRV_INDEX = "evening_hrv_index";
        public static final String MORNING_HRV_INDEX = "morning_hrv_index";
        public static final String ALL_NIGHT_HRV_INDEX = "all_night_hrv_index";
        public static final String RESTING_HRV_INDEX = "resting_hrv_index";
        public static final String TOTAL_SLEEP_SCORE = "total_sleep_score";
        public static final String SLEEP_SCORE_VERSION = "sleep_score_version";

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        SLEEP_ID + INTEGER_TYPE + COMMA_SEP +
                        TIME_ZONE + TEXT_TYPE + COMMA_SEP +
                        START_TIME + DOUBLE_TYPE + COMMA_SEP +
                        END_TIME + DOUBLE_TYPE + COMMA_SEP +
                        DAY + INTEGER_TYPE + COMMA_SEP +
                        MONTH + INTEGER_TYPE + COMMA_SEP +
                        YEAR + INTEGER_TYPE + COMMA_SEP +
                        DAY_OF_WEEK + INTEGER_TYPE + COMMA_SEP +
                        RESTING_HEART_RATE + FLOAT_TYPE + COMMA_SEP +
                        AVERAGE_RESPIRATION_RATE + FLOAT_TYPE + COMMA_SEP +
                        SLEEP_TIME_TARGET + FLOAT_TYPE + COMMA_SEP +
                        SLEEP_LATENCY + FLOAT_TYPE + COMMA_SEP +
                        SLEEP_EFFICIENCY + FLOAT_TYPE + COMMA_SEP +
                        AWAY_EPISODE_COUNT + FLOAT_TYPE + COMMA_SEP +
                        AWAY_EPISODE_DURATION + FLOAT_TYPE + COMMA_SEP +
                        SNORING_EPISODE_DURATION + FLOAT_TYPE + COMMA_SEP +
                        AWAY_TOTAL_TIME + FLOAT_TYPE + COMMA_SEP +
                        SLEEP_TOTAL_TIME + FLOAT_TYPE + COMMA_SEP +
                        RESTLESS_TOTAL_TIME + FLOAT_TYPE + COMMA_SEP +
                        WAKE_TOTAL_TIME + FLOAT_TYPE + COMMA_SEP +
                        GAP_TOTAL_TIME + FLOAT_TYPE + COMMA_SEP +
                        MISSING_SIGNAL_TOTAL_TIME + FLOAT_TYPE + COMMA_SEP +
                        TOTAL_NAP_DURATION + FLOAT_TYPE + COMMA_SEP +
                        ACTIVITY_INDEX + FLOAT_TYPE + COMMA_SEP +
                        EVENING_HRV_INDEX + FLOAT_TYPE + COMMA_SEP +
                        MORNING_HRV_INDEX + FLOAT_TYPE + COMMA_SEP +
                        ALL_NIGHT_HRV_INDEX + FLOAT_TYPE + COMMA_SEP +
                        RESTING_HRV_INDEX + FLOAT_TYPE + COMMA_SEP +
                        TOTAL_SLEEP_SCORE + FLOAT_TYPE + COMMA_SEP +
                        SLEEP_SCORE_VERSION + FLOAT_TYPE +
                " )";

        public static final String SQL_DROP =
                "DROP TABLE IF EXISTS " + TABLE_NAME;



    }

    public static abstract class SleepTracks implements BaseColumns {

        public static final String TABLE_NAME = "sleep_tracks";

        public static final String SLEEP_ID = "sleep_id";
        public static final String TRACK_NAME = "name";
        public static final String TRACK_DATA_TYPE = "data_type";
        public static final String TRACK_DATA = "data";

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        SLEEP_ID + LONG_TYPE + COMMA_SEP +
                        TRACK_NAME + TEXT_TYPE + COMMA_SEP +
                        TRACK_DATA_TYPE + TEXT_TYPE + COMMA_SEP +
                        TRACK_DATA + BLOB_TYPE +
                        " )";


        public static final String SQL_DROP =
                "DROP TABLE IF EXISTS " + TABLE_NAME;


    }

}
