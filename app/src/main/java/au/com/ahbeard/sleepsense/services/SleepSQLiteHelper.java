package au.com.ahbeard.sleepsense.services;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by neal on 3/05/2016.
 */
public class SleepSQLiteHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 17;
    public static final String DATABASE_NAME = "Sleep.db";

    public SleepSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, new DatabaseErrorHandler() {
            @Override
            public void onCorruption(SQLiteDatabase database) {
            }
        });
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL(SleepContract.Sleep.SQL_CREATE);
        database.execSQL(SleepContract.SleepTracks.SQL_CREATE);
        database.execSQL(SleepSessionContract.SleepSession.SQL_CREATE);
        database.execSQL(SleepSessionContract.SleepSessionTracks.SQL_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        if ( oldVersion < 4 ) {
            database.execSQL(SleepSessionContract.SleepSession.SQL_DROP);
            database.execSQL(SleepSessionContract.SleepSession.SQL_CREATE);
        }

        if (oldVersion < 5 ) {
            database.execSQL(SleepContract.Sleep.SQL_DROP);
            database.execSQL(SleepContract.Sleep.SQL_CREATE);
        }

        if (oldVersion < 6 ) {
            database.execSQL(SleepContract.SleepTracks.SQL_DROP);
            database.execSQL(SleepContract.SleepTracks.SQL_CREATE);
        }

        if (oldVersion < 17 ) {
            database.execSQL(SleepSessionContract.SleepSession.SQL_DROP);
            database.execSQL(SleepSessionContract.SleepSession.SQL_CREATE);
            database.execSQL(SleepSessionContract.SleepSessionTracks.SQL_DROP);
            database.execSQL(SleepSessionContract.SleepSessionTracks.SQL_CREATE);
        }

    }




}
