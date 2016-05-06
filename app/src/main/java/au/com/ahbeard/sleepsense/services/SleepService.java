package au.com.ahbeard.sleepsense.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.beddit.analysis.AnalysisException;
import com.beddit.analysis.BatchAnalysis;
import com.beddit.analysis.BatchAnalysisContext;
import com.beddit.analysis.BatchAnalysisResult;
import com.beddit.analysis.CalendarDate;
import com.beddit.analysis.SessionData;
import com.beddit.analysis.TimeValueFragment;
import com.beddit.analysis.TimeValueTrackFragment;
import com.beddit.analysis.TrackFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerUtils;
import au.com.ahbeard.sleepsense.model.beddit.Sleep;
import au.com.ahbeard.sleepsense.model.beddit.SleepProperty;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Service to save the sleep data.
 */
public class SleepService {

    private static SleepService sSleepService;

    private final Context mContext;

    private File mSleepDataStorageDirectory;

    private SleepSQLiteHelper mSleepSQLiteHelper;

    public static void initialize(Context context) {
        sSleepService = new SleepService(context);
    }

    public static SleepService instance() {
        return sSleepService;
    }

    public SleepService(Context context) {
        mContext = context;
        mSleepDataStorageDirectory = context.getFilesDir();
        mSleepSQLiteHelper = new SleepSQLiteHelper(context);
        mSleepSQLiteHelper.getReadableDatabase();
    }


    public static String stringFromCalendarDate(CalendarDate calendarDate) {
        return String.format("%04d%02d%02d", calendarDate.getYear(), calendarDate.getMonth(), calendarDate.getDay());
    }

    public void runBatchAnalysis() {

        Schedulers.computation().createWorker().schedule(new Action0() {
            @Override
            public void call() {

                final long MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                long periodStart = calendar.getTimeInMillis();
                long periodEnd = periodStart + MILLISECONDS_IN_DAY;

                CalendarDate calendarDate = new CalendarDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                List<SessionData> sessionData = readSessionData(periodStart / 1000D, periodEnd / 1000D);

                try {

                    BatchAnalysis batchAnalysis = new BatchAnalysis();

                    Log.d("BatchAnalysis", String.format("ANALYZING DATE: %d/%d/%d", calendarDate.getDay(), calendarDate.getMonth(), calendarDate.getYear()));

                    BatchAnalysisResult batchAnalysisResult = batchAnalysis.analyzeSessions(
                            sessionData,
                            new ArrayList<BatchAnalysisResult>(),
                            calendarDate,
                            new BatchAnalysisContext(PreferenceService.instance().getSleepTargetTime() * 3600f));

                    TrackerUtils.logBatchAnalysisResult(batchAnalysisResult);

                } catch (AnalysisException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    //
    // Where to store each track.
    //
    public File getTrackOutputFile(CalendarDate calendarDate, long startTime, String track, String type) {
        return new File(getSessionOutputDirectory(calendarDate, startTime), track + "." + type + ".bin");
    }

    //
    //
    //
    public File getSessionOutputDirectory(CalendarDate calendarDate, long startTime) {
        File sessionOutputDirectory = new File(getSessionsDirectory(), Long.toString(startTime));
        if (!sessionOutputDirectory.exists()) {
            sessionOutputDirectory.mkdirs();
        }

        return sessionOutputDirectory;
    }

    /**
     * @return
     */
    public File getSessionsDirectory() {
        return new File(mSleepDataStorageDirectory, "sessions");
    }

    /**
     * Run a batch analysis for a calendar date.
     *
     * @param previousBatchResults
     * @param calendarDate
     * @return
     * @throws IOException
     * @throws AnalysisException
     */
    public BatchAnalysisResult runBatchAnalysis(List<BatchAnalysisResult> previousBatchResults, CalendarDate calendarDate) throws IOException, AnalysisException {

        BatchAnalysis batchAnalysis = new BatchAnalysis();

        File dayDirectory = getSessionsDirectory();

        List<SessionData> sessionDataForDay = getSessionDataForDay(dayDirectory);

        BatchAnalysisResult batchAnalysisResult = batchAnalysis.analyzeSessions(
                sessionDataForDay,
                previousBatchResults,
                calendarDate,
                new BatchAnalysisContext(PreferenceService.instance().getSleepTargetTime() * 3600f));

        TrackerUtils.logBatchAnalysisResult(batchAnalysisResult);

        writeSleepToDatabase(Sleep.fromBatchAnalysisResult(batchAnalysisResult));

        return batchAnalysisResult;

    }

    /**
     * Get the sessionData in a given directory.
     *
     * @param dayDirectory
     * @return
     */
    public List<SessionData> getSessionDataForDay(File dayDirectory) throws IOException {

        File[] sessionDirectories = dayDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.matches("\\d+");
            }
        });

        List<SessionData> sessionDatas = new ArrayList<>();

        for (File sessionDirectory : sessionDirectories) {
            SessionData sessionData = getSessionData(sessionDirectory);
            if (sessionData != null) {
                sessionDatas.add(sessionData);
            }
        }

        return sessionDatas;
    }

    public void writeSessionDataToDatabase(File sessionDirectory) throws IOException {
        SessionData sessionData = getSessionData(sessionDirectory);
        writeSessionDataToDatabase(sessionData);
    }


    /**
     * Reconstruct the session data for batch analysis from the database.
     *
     * @param periodStartTime
     * @param periodEndTime
     * @return
     */
    public List<SessionData> readSessionData(double periodStartTime, double periodEndTime) {

        SQLiteDatabase database = mSleepSQLiteHelper.getReadableDatabase();

        List<SessionData> sessionData = new ArrayList<>();

        Cursor sleepSessionCursor = database.rawQuery(SleepSessionContract.JOIN_QUERY,
                new String[]{Double.toString(periodStartTime), Double.toString(periodEndTime)});

        sleepSessionCursor.moveToFirst();

        long currentSessionId = -1;
        SessionData currentSessionData = null;

        while (!sleepSessionCursor.isAfterLast()) {

            long sessionId = sleepSessionCursor.getLong(sleepSessionCursor.getColumnIndex(SleepSessionContract.SleepSession.SESSION_ID));

            if (currentSessionData == null || sessionId != currentSessionId) {

                double startTime = sleepSessionCursor.getDouble(sleepSessionCursor.getColumnIndex(SleepSessionContract.SleepSession.START_TIME));
                double endTime = sleepSessionCursor.getDouble(sleepSessionCursor.getColumnIndex(SleepSessionContract.SleepSession.END_TIME));
                String timeZone = sleepSessionCursor.getString(sleepSessionCursor.getColumnIndex(SleepSessionContract.SleepSession.TIME_ZONE));

                currentSessionId = sessionId;
                currentSessionData = new SessionData(startTime, endTime, new TimeValueFragment());

                sessionData.add(currentSessionData);

            }

            String trackName = sleepSessionCursor.getString(sleepSessionCursor.getColumnIndex(SleepSessionContract.SleepSessionTracks.TRACK_NAME));
            String trackDataType = sleepSessionCursor.getString(sleepSessionCursor.getColumnIndex(SleepSessionContract.SleepSessionTracks.TRACK_DATA_TYPE));
            byte[] trackData = sleepSessionCursor.getBlob(sleepSessionCursor.getColumnIndex(SleepSessionContract.SleepSessionTracks.TRACK_DATA));

            currentSessionData.getRelativeTimeValueData().addTrackFragment(trackName, new TimeValueTrackFragment(trackData, trackDataType));

            sleepSessionCursor.moveToNext();
        }

        sleepSessionCursor.close();

        database.close();

        return sessionData;

    }

    public void writeSessionDataToDatabase(SessionData sessionData) throws IOException {


        SQLiteDatabase database = mSleepSQLiteHelper.getWritableDatabase();

        database.beginTransaction();

        ContentValues values = new ContentValues();

        values.put(SleepSessionContract.SleepSession.START_TIME, sessionData.getStartTime());
        values.put(SleepSessionContract.SleepSession.END_TIME, sessionData.getEndTime());
        values.put(SleepSessionContract.SleepSession.TIME_ZONE, TimeZone.getDefault().getID());

        long sessionId = database.insertOrThrow(SleepSessionContract.SleepSession.TABLE_NAME, null, values);

        for (String trackName : sessionData.getRelativeTimeValueData().getNames()) {

            TrackFragment trackFragment = sessionData.getRelativeTimeValueData().getTrackFragment(trackName);

            ContentValues trackValues = new ContentValues();

            trackValues.put(SleepSessionContract.SleepSession.SESSION_ID, sessionId);
            trackValues.put(SleepSessionContract.SleepSessionTracks.TRACK_NAME, trackName);
            trackValues.put(SleepSessionContract.SleepSessionTracks.TRACK_DATA_TYPE, trackFragment.getItemType());
            trackValues.put(SleepSessionContract.SleepSessionTracks.TRACK_DATA, trackFragment.getData());

            long rowId = database.insertOrThrow(SleepSessionContract.SleepSessionTracks.TABLE_NAME, null, trackValues);

            Log.d("SleepService", "ROW ID: " + rowId);

        }

        database.setTransactionSuccessful();
        database.endTransaction();

        database.close();

    }

    public void writeSleepToDatabase(Sleep sleep) throws IOException {

        SQLiteDatabase database = mSleepSQLiteHelper.getWritableDatabase();

        database.beginTransaction();

        ContentValues values = new ContentValues();

        values.put(SleepContract.Sleep.SLEEP_ID, sleep.getSleepId());
        values.put(SleepContract.Sleep.TIME_ZONE, TimeZone.getDefault().getID());
        values.put(SleepContract.Sleep.START_TIME, sleep.getStartTime());
        values.put(SleepContract.Sleep.END_TIME, sleep.getEndTime());
        values.put(SleepContract.Sleep.DAY, sleep.getDay());
        values.put(SleepContract.Sleep.MONTH, sleep.getMonth());
        values.put(SleepContract.Sleep.YEAR, sleep.getYear());
        values.put(SleepContract.Sleep.DAY_OF_WEEK, sleep.getDayOfWeek());
        values.put(SleepContract.Sleep.RESTING_HEART_RATE, sleep.getRestingHeartRate());
        values.put(SleepContract.Sleep.AVERAGE_RESPIRATION_RATE, sleep.getAverageRespirationRate());
        values.put(SleepContract.Sleep.SLEEP_TIME_TARGET, sleep.getSleepTimeTarget());
        values.put(SleepContract.Sleep.SLEEP_LATENCY, sleep.getSleepLatency());
        values.put(SleepContract.Sleep.SLEEP_EFFICIENCY, sleep.getSleepEfficiency());
        values.put(SleepContract.Sleep.AWAY_EPISODE_COUNT, sleep.getPrimarySleepPeriodAwayEpiodeCount());
        values.put(SleepContract.Sleep.AWAY_EPISODE_DURATION, sleep.getPrimarySleepPeriodAwayEpisodeDuration());
        values.put(SleepContract.Sleep.SNORING_EPISODE_DURATION, sleep.getTotalSnoringEpisodeDuration());
        values.put(SleepContract.Sleep.AWAY_TOTAL_TIME, sleep.getAwayTotalTime());
        values.put(SleepContract.Sleep.SLEEP_TOTAL_TIME, sleep.getSleepTotalTime());
        values.put(SleepContract.Sleep.RESTLESS_TOTAL_TIME, sleep.getRestlessTotalTime());
        values.put(SleepContract.Sleep.WAKE_TOTAL_TIME, sleep.getWakeTotalTime());
        values.put(SleepContract.Sleep.GAP_TOTAL_TIME, sleep.getGapTotalTime());
        values.put(SleepContract.Sleep.MISSING_SIGNAL_TOTAL_TIME, sleep.getMissingSignalTotalTime());
        values.put(SleepContract.Sleep.TOTAL_NAP_DURATION, sleep.getTotalNapDuration());
        values.put(SleepContract.Sleep.ACTIVITY_INDEX, sleep.getActivityIndex());
        values.put(SleepContract.Sleep.EVENING_HRV_INDEX, sleep.getEveningHRVIndex());
        values.put(SleepContract.Sleep.MORNING_HRV_INDEX, sleep.getMorningHRVIndex());
        values.put(SleepContract.Sleep.ALL_NIGHT_HRV_INDEX, sleep.getAllNightHRVIndex());
        values.put(SleepContract.Sleep.RESTING_HRV_INDEX, sleep.getRestingHRVIndex());
        values.put(SleepContract.Sleep.TOTAL_SLEEP_SCORE, sleep.getTotalSleepScore());
        values.put(SleepContract.Sleep.SLEEP_SCORE_VERSION, sleep.getSleepScoreVersion());

        long id = database.insertOrThrow(SleepContract.Sleep.TABLE_NAME, null, values);


        database.setTransactionSuccessful();
        database.endTransaction();

        database.close();


    }

    /**
     * Get the sessionData for a single session.
     *
     * @param sessionDirectory
     * @return
     */
    public SessionData getSessionData(File sessionDirectory) throws IOException {

        File metadataFile = new File(sessionDirectory, "metadata.dat");

        if (!metadataFile.exists()) {
            return null;
        }

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(metadataFile));

        long startTime = objectInputStream.readLong();
        long endTime = objectInputStream.readLong();

        objectInputStream.close();

        TimeValueFragment timeValueFragment = new TimeValueFragment();

        File[] trackFiles = sessionDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.matches(".+\\.bin$");
            }
        });

        Pattern pattern = Pattern.compile("^(.+)\\.(.+)\\.bin$");

        for (File trackFile : trackFiles) {
            Matcher matcher = pattern.matcher(trackFile.getName());

            if (matcher.matches()) {

                Log.d("SleepService", "reading track file: " + trackFile);

                String trackName = matcher.group(1);
                String valueType = matcher.group(2);

                FileInputStream fileInputStream = new FileInputStream(trackFile);
                ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[2048];
                int bytesRead = -1;

                while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                }

                fileInputStream.close();
                dataOutputStream.close();

                TimeValueTrackFragment timeValueTrackFragment = new TimeValueTrackFragment(dataOutputStream.toByteArray(), valueType);

                timeValueFragment.addTrackFragment(trackName, timeValueTrackFragment);

            }
        }

        return new SessionData(startTime / 1000D, endTime / 1000D, timeValueFragment);

    }

    public List<SleepProperty> readSleepScores(int daysBeforeToday, int length) {

        SQLiteDatabase database = mSleepSQLiteHelper.getReadableDatabase();

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, -daysBeforeToday);

        int endSleepId = calendar.get(Calendar.YEAR) * 10000 + (calendar.get(Calendar.MONTH) + 1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.DAY_OF_YEAR, -length);
        int startSleepId = calendar.get(Calendar.YEAR) * 10000 + (calendar.get(Calendar.MONTH) + 1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);

        Cursor sleepSessionCursor = database.rawQuery("select sleep_id, sleep_score from sleep where sleep_id > ? and sleep_id <= ? order by sleep_id asc",
                new String[]{Integer.toString(startSleepId), Integer.toString(endSleepId)});

        sleepSessionCursor.moveToFirst();

        List<SleepProperty> mSleepScores = new ArrayList<>();

        while ( ! sleepSessionCursor.isAfterLast() ) {
            int sleepId = sleepSessionCursor.getInt(sleepSessionCursor.getColumnIndex(SleepContract.Sleep.SLEEP_ID));
            float sleepScore = sleepSessionCursor.getFloat(sleepSessionCursor.getColumnIndex(SleepContract.Sleep.SLEEP_ID));
            mSleepScores.add(new SleepProperty(sleepId,sleepScore));
            sleepSessionCursor.moveToNext();
        }

        return mSleepScores;
    }
}
