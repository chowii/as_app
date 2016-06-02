package au.com.ahbeard.sleepsense.services;

import android.provider.BaseColumns;

/**
 * Created by neal on 3/05/2016.
 */
public class MattressFirmnessContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String LONG_TYPE = " LONG";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String BLOB_TYPE = " BLOB";

    private static final String COMMA_SEP = ",";

    public static String JOIN_QUERY = "select sleep_session.session_id, start_time, end_time, time_zone, track_name, track_data, track_data_type " +
            "from sleep_session, sleep_session_tracks " +
            "where sleep_session.session_id = sleep_session_tracks.session_id " +
                "and end_time >= ? and end_time < ? order by sleep_session.session_id, end_time";

    /*
     * Inner class that defines the table contents.
     */
    public static abstract class MattressFirmness implements BaseColumns {

        public static final String TABLE_NAME = "mattress_firmness";

        public static final String TIME_READ = "time_read";
        public static final String PRESSURE = "pressure";

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        TIME_READ + DOUBLE_TYPE + COMMA_SEP +
                        PRESSURE + DOUBLE_TYPE  +
                " )";

        public static final String SQL_DROP =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

}
