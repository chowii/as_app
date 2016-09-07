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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerUtils;
import au.com.ahbeard.sleepsense.model.AggregateStatistics;
import au.com.ahbeard.sleepsense.model.Firmness;
import au.com.ahbeard.sleepsense.model.beddit.Sleep;
import au.com.ahbeard.sleepsense.model.beddit.SleepProperty;
import au.com.ahbeard.sleepsense.model.beddit.TrackData;
import au.com.ahbeard.sleepsense.services.log.SSLog;
import au.com.ahbeard.sleepsense.utils.OptimalBedtimeCalculator;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Service to save the sleep data.
 */
public class SleepService {

    private static SleepService sSleepService;

    private final Context mContext;

    private File mSleepDataStorageDirectory;

    private SleepSQLiteHelper mSleepSQLiteHelper;
    private AggregateStatistics mAggregateStatistics = new AggregateStatistics();

    private PublishSubject<Integer> mDataChangePublishSubject = PublishSubject.create();
    private PublishSubject<Integer> mSleepIdSelectedSubject = PublishSubject.create();

    private BehaviorSubject<AggregateStatistics> mAggregateStatisticsBehaviourSubject = BehaviorSubject.create();
    private boolean mAggregateStatisticsCalculated;

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

    /**
     * Run a batch analysis for today.
     */
    public void runBatchAnalysis() {

        SSLog.d("Running sleep analysis");

        try {
            checkSessionData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Schedulers.computation().createWorker().schedule(new Action0() {
            @Override
            public void call() {

                Sleep sleep = runBatchAnalysis(Calendar.getInstance());

                if ( sleep != null ) {

                    AnalyticsService.instance().logSleep(sleep);
                    SSLog.d("Saved sleep with %.0f", sleep.getTotalSleepScore());
                } else {
                    SSLog.d("Sleep after analysis is null");
                }

                PreferenceService.instance().setHasRecordedASleep(true);

                mDataChangePublishSubject.onNext(-1);

                updateAggregateStatistics();

            }
        });

    }

//    /**
//     * Run a batch analysis for a set number of days.
//     *
//     * @param numberOfDays
//     */
//    public void runBatchAnalysis(final int numberOfDays) {
//
//        try {
//            checkSessionData();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Schedulers.computation().createWorker().schedule(new Action0() {
//            @Override
//            public void call() {
//
//                Calendar calendar = Calendar.getInstance();
//
//                calendar.add(Calendar.DAY_OF_YEAR, -numberOfDays);
//
//                for (int i = 0; i < numberOfDays; i++) {
//                    calendar.add(Calendar.DAY_OF_YEAR, 1);
//                    runBatchAnalysis(calendar);
//                }
//
//                PreferenceService.instance().setHasRecordedASleep(true);
//
//                mDataChangePublishSubject.onNext(-1);
//
//                updateAggregateStatistics();
//
//            }
//        });
//    }

    /**
     * Run a batch analysis for a given calendar date.
     *
     * @param calendar
     */
    public Sleep runBatchAnalysis(final Calendar calendar) {

        final long MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long periodStart = calendar.getTimeInMillis();
        long periodEnd = periodStart + MILLISECONDS_IN_DAY;

        CalendarDate calendarDate = new CalendarDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        List<SessionData> sessionDatas = readSessionData(periodStart / 1000D, periodEnd / 1000D);

        HashSet<Double> sessionDataStarts = new HashSet<>();

        // Fix any duplicates caused by re-running batch analysis and grabbing the same session again (really only a problem on the test device I'm using).
        for (Iterator<SessionData> i = sessionDatas.iterator(); i.hasNext(); ) {
            SessionData sessionData = i.next();
            if (sessionDataStarts.contains(sessionData.getStartTime())) {
                i.remove();
            } else {
                sessionDataStarts.add(sessionData.getStartTime());
            }
        }

        if (sessionDatas.size() > 0) {

            try {

                BatchAnalysis batchAnalysis = new BatchAnalysis();

                // Log.d("BatchAnalysis", String.format("ANALYZING DATE: %d/%d/%d", calendarDate.getDay(), calendarDate.getMonth(), calendarDate.getYear()));

                for (SessionData sessionData1 : sessionDatas) {
                    // Log.d("BatchAnalysis", String.format("SESSSION DATA: %s to %s", new Date((long) sessionData1.getStartTime() * 1000), new Date((long) sessionData1.getEndTime() * 1000)));
                }

                BatchAnalysisResult batchAnalysisResult = batchAnalysis.analyzeSessions(
                        sessionDatas,
                        new ArrayList<BatchAnalysisResult>(),
                        calendarDate,
                        new BatchAnalysisContext(PreferenceService.instance().getSleepTargetTime() * 3600f));

//                TrackerUtils.logBatchAnalysisResult(batchAnalysisResult);

                Sleep sleep = Sleep.fromBatchAnalysisResult(batchAnalysisResult);

                // Find the first mattress pressure in the date range. We really need to figure out WTF to do here
                LinkedHashMap<Double, Float> mattressPressures = readMattressPressures(batchAnalysisResult.getStartTime(),batchAnalysisResult.getEndTime());

                if ( mattressPressures.size() > 0 ) {
                    for (Map.Entry<Double, Float> mattressPressure : mattressPressures.entrySet()) {
                        sleep.setMattressFirmness(mattressPressure.getValue());
                        break;
                    }
                } else {
                    sleep.setMattressFirmness(20.0f);
                }

                writeSleepToDatabase(sleep);

                return sleep;

            } catch (AnalysisException e) {
                Log.e("SleepService","Exception performing batch analysis...",e);
            } catch (IOException e) {
                Log.e("SleepService","Exception performing batch analysis...",e);
            }
        }

        return null;

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
    public File getBackupSessionsDirectory() {
        return new File(mSleepDataStorageDirectory, "backup");
    }

    /**
     * @return
     */
    public File getSessionsDirectory() {
        return new File(mSleepDataStorageDirectory, "sessions");
    }

    /**
     * Write the session data for a given File in the database.
     *
     * @param sessionDirectory
     * @throws IOException
     */
    public void writeSessionDataToDatabase(File sessionDirectory) throws IOException {
        SessionData sessionData = getSessionData(sessionDirectory);
        if (sessionData != null) {
            writeSessionDataToDatabase(sessionData);
            getBackupSessionsDirectory().mkdirs();
            File backupSessionDirectory = new File(getBackupSessionsDirectory(), sessionDirectory.getName());
            sessionDirectory.renameTo(backupSessionDirectory);
        }
    }

    /**
     * Put any sessions that aren't in the database in the database.
     *
     * @throws IOException
     */
    public void checkSessionData() throws IOException {

        File[] sessions = getSessionsDirectory().listFiles();

        if (sessions != null) {
            for (File session : sessions) {
                SessionData sessionData = getSessionData(session);
                if (sessionData != null) {
                    writeSessionDataToDatabase(sessionData);
                    getBackupSessionsDirectory().mkdirs();
                    File backupSessionDirectory = new File(getBackupSessionsDirectory(), session.getName());
                    session.renameTo(backupSessionDirectory);
                }
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

        return sessionData;

    }

    /**
     * Write session data to the database.
     *
     * @param sessionData
     * @throws IOException
     */
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

        }

        database.setTransactionSuccessful();
        database.endTransaction();

    }

    public void clearSleepDatabase() throws IOException {

        SQLiteDatabase database = mSleepSQLiteHelper.getWritableDatabase();

        database.execSQL("delete from " + SleepContract.Sleep.TABLE_NAME );
        database.execSQL("delete from " + SleepContract.SleepTracks.TABLE_NAME );


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
        values.put(SleepContract.Sleep.MATTRESS_FIRMNESS, sleep.getMattressFirmness());

        long id = database.insertOrThrow(SleepContract.Sleep.TABLE_NAME, null, values);

        if (sleep.getTrackDataByName() != null) {

            for (TrackData trackData : sleep.getTrackDataByName().values()) {
                ContentValues trackDataValues = new ContentValues();
                trackDataValues.put(SleepContract.Sleep.SLEEP_ID, sleep.getSleepId());
                trackDataValues.put(SleepContract.SleepTracks.TRACK_NAME, trackData.getName());
                trackDataValues.put(SleepContract.SleepTracks.TRACK_DATA_TYPE, trackData.getType());
                trackDataValues.put(SleepContract.SleepTracks.TRACK_DATA, trackData.getData());

                long trackId = database.insertOrThrow(SleepContract.SleepTracks.TABLE_NAME, null, trackDataValues);
            }

        }

        database.setTransactionSuccessful();
        database.endTransaction();

    }

    public Sleep getSleepFromDatabase(long sleepId) {
        return Sleep.fromDatabase(mSleepSQLiteHelper, sleepId);
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

            for (File file : sessionDirectory.listFiles()) {
                endTime = Math.max(endTime, file.lastModified());
            }

            if (endTime == 0) {
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

                // Log.d("SleepService", "reading track file: " + trackFile);

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
     * @param startSleepId
     * @param endSleepId
     * @return
     */
    public Float[] readSleepScores(int startSleepId, int endSleepId) {

        // Log.d("SleepService", String.format("startSleepId: %d endSleepId: %d", startSleepId, endSleepId));

        SQLiteDatabase database = null;
        Cursor sleepSessionCursor = null;

        Float[] sleepScores;

        try {
            database = mSleepSQLiteHelper.getReadableDatabase();

            sleepSessionCursor = database.rawQuery(
                    "select sleep_id, total_sleep_score from sleep where sleep_id >= ? and sleep_id <= ? order by sleep_id asc",
                    new String[]{Integer.toString(startSleepId), Integer.toString(endSleepId)});

            sleepSessionCursor.moveToFirst();


            Map<Integer, SleepProperty> sleepScoreBySleepId = new HashMap<>();

            while (!sleepSessionCursor.isAfterLast()) {
                int sleepId = sleepSessionCursor.getInt(sleepSessionCursor.getColumnIndex(SleepContract.Sleep.SLEEP_ID));
                float sleepScore = sleepSessionCursor.getFloat(sleepSessionCursor.getColumnIndex(SleepContract.Sleep.TOTAL_SLEEP_SCORE));
                // Log.d("Dashboard", String.format("sleep_id: %d sleep_score: %f", sleepId, sleepScore));
                sleepScoreBySleepId.put(sleepId, new SleepProperty(sleepId, sleepScore));
                sleepSessionCursor.moveToNext();
            }

            List<Integer> sleepIdRange = generateSleepIdRange(startSleepId, endSleepId);

            sleepScores = new Float[sleepIdRange.size()];
            int i = 0;

            for (Integer sleepId : sleepIdRange) {
                sleepScores[i++] = sleepScoreBySleepId.containsKey(sleepId) ? sleepScoreBySleepId.get(sleepId).getValue() : null;
            }

        } finally {
            if (sleepSessionCursor != null) {
                sleepSessionCursor.close();
            }

        }

        return sleepScores;
    }

    public Observable<Float[]> readSleepScoresAsync(final int startSleepId, final int endSleepId) {

        return makeObservable(new Callable<Float[]>() {
            @Override
            public Float[] call() throws Exception {
                return readSleepScores(startSleepId, endSleepId);
            }
        });

    }

    private static <T> Observable<T> makeObservable(final Callable<T> func) {
        return Observable.create(
                new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try {
                            subscriber.onNext(func.call());
                            subscriber.onCompleted();
                        } catch (Exception ex) {
                            subscriber.onError(ex);
                        }
                    }
                });
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
     * @param calendar
     * @return
     */
    public static String getYYYYMMDDD(Calendar calendar) {
        return String.format("%04d-%02d-%02d",calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
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


    public Observable<Integer> getChangeObservable() {
        return mDataChangePublishSubject;
    }

    public void notifySleepIdSelected(Integer id) {
        mSleepIdSelectedSubject.onNext(id);
    }

    public Observable<Integer> getSleepIdSelectedObservable() {
        return mSleepIdSelectedSubject;
    }

    public Observable<AggregateStatistics> getAggregateStatisticsObservable() {

        if (!mAggregateStatisticsCalculated) {
            mAggregateStatisticsCalculated = true;
            Schedulers.computation().createWorker().schedule(new Action0() {
                @Override
                public void call() {
                    updateAggregateStatistics();
                }
            });
        }

        return mAggregateStatisticsBehaviourSubject;
    }

    public void updateAggregateStatistics() {

        mAggregateStatisticsCalculated = true;

        SQLiteDatabase database = null;
        Cursor sleepSessionCursor = null;

        try {
            database = mSleepSQLiteHelper.getReadableDatabase();

            Calendar calendar = Calendar.getInstance();

            int endSleepId = getSleepId(calendar);
            calendar.add(Calendar.DAY_OF_YEAR, -89);
            int startSleepId = getSleepId(calendar);

            sleepSessionCursor = database.rawQuery(
                    "select sleep_id, total_sleep_score, mattress_firmness, start_time from sleep where sleep_id >= ? and sleep_id <= ? order by sleep_id asc",
                    new String[]{Integer.toString(startSleepId), Integer.toString(endSleepId)});

            sleepSessionCursor.moveToFirst();

            float smallestSleepScore = Float.MAX_VALUE;
            int smallestSleepScoreSleepId = 0;
            float highestSleepScore = Float.MIN_VALUE;
            float highestSleepScoreMattressFirmness = 0;
            int highestSleepScoreSleepId = 0;
            float sumOfSleepScores = 0;
            int countOfSleepScores = 0;

            OptimalBedtimeCalculator optimalBedtimeCalculator = new OptimalBedtimeCalculator();

            while (!sleepSessionCursor.isAfterLast()) {

                double startTime = sleepSessionCursor.getDouble(sleepSessionCursor.getColumnIndex(SleepContract.Sleep.START_TIME));
                int sleepId = sleepSessionCursor.getInt(sleepSessionCursor.getColumnIndex(SleepContract.Sleep.SLEEP_ID));
                float sleepScore = sleepSessionCursor.getFloat(sleepSessionCursor.getColumnIndex(SleepContract.Sleep.TOTAL_SLEEP_SCORE));
                float mattressFirmness = sleepSessionCursor.getFloat(sleepSessionCursor.getColumnIndex(SleepContract.Sleep.MATTRESS_FIRMNESS));

                countOfSleepScores += 1;
                sumOfSleepScores += sleepScore;

                optimalBedtimeCalculator.add(startTime, sleepScore);

                if (sleepScore < smallestSleepScore) {
                    smallestSleepScore = sleepScore;
                    smallestSleepScoreSleepId = sleepId;
                }

                if (sleepScore > highestSleepScore) {
                    highestSleepScore = sleepScore;
                    highestSleepScoreSleepId = sleepId;
                    highestSleepScoreMattressFirmness = mattressFirmness;
                }

                sleepSessionCursor.moveToNext();

            }

            if (countOfSleepScores > 0) {
                mAggregateStatistics.setAverageSleepScore(sumOfSleepScores / countOfSleepScores);
            } else {
                mAggregateStatistics.setAverageSleepScore(0.0f);
            }

            if (highestSleepScoreSleepId > 0) {
                mAggregateStatistics.setBestNight(getCalendar(highestSleepScoreSleepId));
                mAggregateStatistics.setOptimalMattressFirmness((int) highestSleepScoreMattressFirmness);
            }

            if (smallestSleepScoreSleepId > 0) {
                mAggregateStatistics.setWorstNight(getCalendar(smallestSleepScoreSleepId));
            }

            mAggregateStatistics.setOptimalBedtime(optimalBedtimeCalculator.getResult());

            mAggregateStatisticsBehaviourSubject.onNext(mAggregateStatistics);


        } finally {
            if (sleepSessionCursor != null) {
                sleepSessionCursor.close();
            }
        }

    }


    /**
     * TEST METHODS
     */


    /**
     * @param calendar
     * @param days
     */
    public void generateFakeData(Calendar calendar, int days) {

        int endSleepId = getSleepId(calendar);
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        int startSleepId = getSleepId(calendar);

        Random random = new Random();

        try {

            clearSleepDatabase();

            for (int sleepId : generateSleepIdRange(startSleepId, endSleepId)) {
                if (random.nextFloat() > 0.2f||sleepId == endSleepId) {

                    Sleep sleep = Sleep.generateRandom(sleepId, random);
                    writeSleepToDatabase(sleep);
                }
            }

            PreferenceService.instance().setHasRecordedASleep(true);

            mDataChangePublishSubject.onNext(-1);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void writeMattressFirmnessToDatabase(long timeRead, int pressure) {

        SQLiteDatabase database = mSleepSQLiteHelper.getWritableDatabase();

        database.beginTransaction();

        try {
            ContentValues values = new ContentValues();

            values.put(MattressFirmnessContract.MattressFirmness.TIME_READ, timeRead / 1000d);
            values.put(MattressFirmnessContract.MattressFirmness.PRESSURE, pressure);

            long sessionId = database.insertOrThrow(MattressFirmnessContract.MattressFirmness.TABLE_NAME, null, values);

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

    }

    /**
     * @param startDate
     * @param endDate
     * @return
     */
    public LinkedHashMap<Double, Float> readMattressPressures(double startDate, double endDate) {

        // Log.d("SleepService", String.format("startDate: %f endDate: %f", startDate, endDate));

        SQLiteDatabase database = null;
        Cursor mattressPressureCursor = null;

        LinkedHashMap<Double,Float> mattressPressureReadings = new LinkedHashMap<>();

        try {
            database = mSleepSQLiteHelper.getReadableDatabase();

            mattressPressureCursor = database.rawQuery(
                    "select  * from mattress_firmness where time_read >= ? and time_read <= ? order by time_read asc",
                    new String[]{Double.toString(startDate), Double.toString(endDate)});

            mattressPressureCursor.moveToFirst();

            while (!mattressPressureCursor.isAfterLast()) {
                double timeRead = mattressPressureCursor.getDouble(mattressPressureCursor.getColumnIndex(MattressFirmnessContract.MattressFirmness.TIME_READ));
                float pressure = mattressPressureCursor.getFloat(mattressPressureCursor.getColumnIndex(MattressFirmnessContract.MattressFirmness.PRESSURE));
                mattressPressureReadings.put(timeRead,pressure);
                // Log.d("SleepService", String.format("time_read: %f mattress_firmness: %f", timeRead, pressure));
                mattressPressureCursor.moveToNext();
            }


        } finally {
            if (mattressPressureCursor != null) {
                mattressPressureCursor.close();
            }

        }

        return mattressPressureReadings;
    }


}
