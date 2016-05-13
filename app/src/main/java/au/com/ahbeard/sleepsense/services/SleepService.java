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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerUtils;
import au.com.ahbeard.sleepsense.model.beddit.Sleep;
import au.com.ahbeard.sleepsense.model.beddit.SleepProperty;
import au.com.ahbeard.sleepsense.model.beddit.TrackData;
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

    public void runBatchAnalysis() {

//        try {
//            checkSessionData();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        Schedulers.computation().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                Calendar calendar = Calendar.getInstance();

                calendar.add(Calendar.DAY_OF_YEAR, -7);

                for (int i = 0; i < 7; i++) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    runBatchAnalysis(calendar);
                }
            }
        });

    }

    public void runBatchAnalysis(final Calendar calendar) {

        final long MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long periodStart = calendar.getTimeInMillis();
        long periodEnd = periodStart + MILLISECONDS_IN_DAY;

        CalendarDate calendarDate = new CalendarDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        List<SessionData> sessionData = readSessionData(periodStart / 1000D, periodEnd / 1000D);

        if (sessionData.size() > 0) {

            try {

                BatchAnalysis batchAnalysis = new BatchAnalysis();

                Log.d("BatchAnalysis", String.format("ANALYZING DATE: %d/%d/%d", calendarDate.getDay(), calendarDate.getMonth(), calendarDate.getYear()));

                BatchAnalysisResult batchAnalysisResult = batchAnalysis.analyzeSessions(
                        sessionData,
                        new ArrayList<BatchAnalysisResult>(),
                        calendarDate,
                        new BatchAnalysisContext(PreferenceService.instance().getSleepTargetTime() * 3600f));

                TrackerUtils.logBatchAnalysisResult(batchAnalysisResult);

                writeSleepToDatabase(Sleep.fromBatchAnalysisResult(batchAnalysisResult));

            } catch (AnalysisException e) {
                // TODO: Log this exception online somewhere. There's not actually anything we can do to recover.
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //
    // Where to store each track.
    //
    public File getTrackOutputFile(long startTime, String track, String type) {
        return new File(getSessionOutputDirectory(startTime), track + "." + type + ".bin");
    }

    //
    //
    //
    public File getSessionOutputDirectory(long startTime) {
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

    public void writeSessionDataToDatabase(File sessionDirectory) throws IOException {
        SessionData sessionData = getSessionData(sessionDirectory);
        if ( sessionData != null ) {
            writeSessionDataToDatabase(sessionData);
        }
    }

    public void checkSessionData() throws IOException {

        File[] sessions = getSessionsDirectory().listFiles();

        for (File session : sessions) {
            SessionData sessionData = getSessionData(session);
            if ( sessionData != null ) {
                writeSessionDataToDatabase(sessionData);
            }
        }

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

        // Remove any existing table entry.
        database.delete(SleepContract.Sleep.TABLE_NAME, SleepContract.Sleep.SLEEP_ID + "=?", new String[]{Long.toString(sleep.getSleepId())});
        database.delete(SleepContract.SleepTracks.TABLE_NAME, SleepContract.Sleep.SLEEP_ID + "=?", new String[]{Long.toString(sleep.getSleepId())});

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

        Log.d("SleepService", "id=" + id);

        if ( sleep.getTrackDataByName() != null ) {

            for (TrackData trackData : sleep.getTrackDataByName().values()) {
                ContentValues trackDataValues = new ContentValues();
                trackDataValues.put(SleepContract.Sleep.SLEEP_ID, sleep.getSleepId());
                trackDataValues.put(SleepContract.SleepTracks.TRACK_NAME, trackData.getName());
                trackDataValues.put(SleepContract.SleepTracks.TRACK_DATA_TYPE, trackData.getType());
                trackDataValues.put(SleepContract.SleepTracks.TRACK_DATA, trackData.getData());

                long trackId = database.insertOrThrow(SleepContract.SleepTracks.TABLE_NAME, null, trackDataValues);

                Log.d("SleepService", "trackId=" + trackId);
            }

        }


        database.setTransactionSuccessful();
        database.endTransaction();

        database.close();


    }

    public Sleep getSleepFromDatabase(long sleepId) {
        return Sleep.fromDatabase(mSleepSQLiteHelper,sleepId);
    }

    /**
     * Get the sessionData for a single session.
     *
     * @param sessionDirectory
     * @return
     */
    public SessionData getSessionData(File sessionDirectory) throws IOException {

        File metadataFile = new File(sessionDirectory, "metadata.dat");

        long startTime;
        long endTime;

        if (metadataFile.exists()) {

            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(metadataFile));

            startTime = objectInputStream.readLong();
            endTime = objectInputStream.readLong();

            objectInputStream.close();

        } else {

            startTime = Long.parseLong(sessionDirectory.getName());
            endTime = 0;

            for (File file : sessionDirectory.listFiles() ) {
                endTime = Math.max(endTime,file.lastModified());
            }

            if ( endTime == 0 ) {
                return null;
            }

        }


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

    /**
     *
     * @param startSleepId
     * @param endSleepId
     * @return
     */
    public float[] readSleepScores(int startSleepId, int endSleepId) {

        Log.d("SleepService", String.format("startSleepId: %d endSleepId: %d", startSleepId, endSleepId));

        SQLiteDatabase database = mSleepSQLiteHelper.getReadableDatabase();

        Cursor sleepSessionCursor = database.rawQuery("select sleep_id, total_sleep_score from sleep where sleep_id >= ? and sleep_id <= ? order by sleep_id asc",
                new String[]{Integer.toString(startSleepId), Integer.toString(endSleepId)});

        sleepSessionCursor.moveToFirst();


        Map<Integer, SleepProperty> sleepScoreBySleepId = new HashMap<>();

        while (!sleepSessionCursor.isAfterLast()) {
            int sleepId = sleepSessionCursor.getInt(sleepSessionCursor.getColumnIndex(SleepContract.Sleep.SLEEP_ID));
            float sleepScore = sleepSessionCursor.getFloat(sleepSessionCursor.getColumnIndex(SleepContract.Sleep.TOTAL_SLEEP_SCORE));
            Log.d("Dashboard", String.format("sleep_id: %d sleep_score: %f", sleepId, sleepScore));
            sleepScoreBySleepId.put(sleepId, new SleepProperty(sleepId, sleepScore));
            sleepSessionCursor.moveToNext();
        }

        List<Integer> sleepIdRange = generateSleepIdRange(startSleepId, endSleepId);

        float[] sleepScores = new float[sleepIdRange.size()];
        int i = 0;

        for (Integer sleepId : sleepIdRange) {
            sleepScores[i++] = sleepScoreBySleepId.containsKey(sleepId) ? sleepScoreBySleepId.get(sleepId).getValue() : -1;
        }

        return sleepScores;
    }

    /**
     * Generate a range
     *
     * @param startSleepId
     * @param endSleepId
     * @return
     */
    public static List<Integer> generateSleepIdRange(int startSleepId, int endSleepId) {

        List<Integer> sleepIds = new ArrayList<>();

        Calendar calendar = getCalendar(startSleepId);

        do {
            sleepIds.add(getSleepId(calendar));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        } while (getSleepId(calendar) <= endSleepId);


        return sleepIds;

    }

    /**
     * Generate a range
     *
     * @param startSleepId
     * @param endSleepId
     * @return
     */
    public static String[] generateLabels(int startSleepId, int endSleepId, DateFormat dateFormat) {

        List<String> labels = new ArrayList<>();

        Calendar calendar = getCalendar(startSleepId);

        do {
            labels.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        } while (getSleepId(calendar) <= endSleepId);

        return labels.toArray(new String[labels.size()]);
    }

    /**
     * @param calendar
     * @return
     */
    public static int getSleepId(Calendar calendar) {
        return calendar.get(Calendar.YEAR) * 10000 + (calendar.get(Calendar.MONTH) + 1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @param sleepId
     * @return
     */
    public static Calendar getCalendar(int sleepId) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, sleepId / 10000);
        calendar.set(Calendar.MONTH, ((sleepId / 100) % 100) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, sleepId % 100);

        return calendar;
    }

    /**
     *
     * @param calendar
     * @param days
     */
    public void generateFakeData(Calendar calendar, int days) {

        int endSleepId = getSleepId(calendar);
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        int startSleepId = getSleepId(calendar);

        Random random = new Random(12412);

        try {
            for (int sleepId : generateSleepIdRange(startSleepId, endSleepId)) {
                if ( random.nextFloat() > 0.2f ) {
                    Sleep sleep = Sleep.generateRandom(sleepId,random);
                    writeSleepToDatabase(sleep);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
