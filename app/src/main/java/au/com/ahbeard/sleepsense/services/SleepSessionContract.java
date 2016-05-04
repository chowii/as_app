package au.com.ahbeard.sleepsense.services;

import android.provider.BaseColumns;

/**
 * Created by neal on 3/05/2016.
 */
public class SleepSessionContract {

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
    public static abstract class SleepSession implements BaseColumns {

        public static final String TABLE_NAME = "sleep_session";

        public static final String SLEEP_ID = "sleep_id";
        public static final String TIME_ZONE = "time_zone";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        SLEEP_ID + LONG_TYPE + COMMA_SEP +
                        TIME_ZONE + TEXT_TYPE + COMMA_SEP +
                        START_TIME + LONG_TYPE + COMMA_SEP +
                        END_TIME + LONG_TYPE + COMMA_SEP +
                " )";

        public static final String SQL_DROP =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static abstract class SleepSessionTracks implements BaseColumns {

        public static final String TABLE_NAME = "sleep_tracks";

        public static final String SLEEP_ID = "sleep_id";
        public static final String TRACK_NAME = "track_name";
        public static final String TRACK_DATA_TYPE = "track_data_type";
        public static final String TRACK_DATA = "track_data";

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        SLEEP_ID + LONG_TYPE + COMMA_SEP +
                        TRACK_NAME + TEXT_TYPE + COMMA_SEP +
                        TRACK_DATA_TYPE + TEXT_TYPE + COMMA_SEP +
                        TRACK_DATA + BLOB_TYPE + COMMA_SEP +
                        " )";



    }

}
