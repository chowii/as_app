package au.com.ahbeard.sleepsense.services;

import android.content.Context;
import android.util.Log;

import com.beddit.analysis.AnalysisException;
import com.beddit.analysis.BatchAnalysis;
import com.beddit.analysis.BatchAnalysisContext;
import com.beddit.analysis.BatchAnalysisResult;
import com.beddit.analysis.CalendarDate;
import com.beddit.analysis.SessionData;
import com.beddit.analysis.TimeValueFragment;
import com.beddit.analysis.TimeValueTrackFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerUtils;
import rx.Observer;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Service to save the sleep data.
 */
public class SleepService {

    private static SleepService sSleepService;

    private final Context mContext;
    private File mSleepDataStorageDirectory;

    public static void initialize(Context context) {
        sSleepService = new SleepService(context);
    }

    public static SleepService instance() {
        return sSleepService;
    }

    public SleepService(Context context) {
        mContext = context;
        mSleepDataStorageDirectory = context.getFilesDir();
    }


    public static String stringFromCalendarDate(CalendarDate calendarDate) {
        return String.format("%04d%02d%02d", calendarDate.getYear(), calendarDate.getMonth(), calendarDate.getDay());
    }

    public void runBatchAnalysis() {

        Schedulers.io().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                CalendarDate analysisCalendarDate = TrackerUtils.getCalendarDate(System.currentTimeMillis());

                RemoteSleepDataService.instance()
                        .saveSleepData("" + System.currentTimeMillis(), new byte[]{1, 2, 3, 4})
                        .subscribe(new Observer<String>() {

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("TAG", "error", e);
                            }

                            @Override
                            public void onNext(String s) {

                            }
                        });

            }
        });

        Schedulers.computation().createWorker().schedule(new Action0() {
            @Override
            public void call() {

                final long MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

                long currentTime = System.currentTimeMillis();

                List<BatchAnalysisResult> previousBatchAnalysisResults = new ArrayList<>();

                long analysisTime = currentTime - 30 * MILLISECONDS_IN_DAY;

                while (analysisTime <= currentTime) {
                    try {

                        CalendarDate analysisCalendarDate = TrackerUtils.getCalendarDate(analysisTime);

                        Log.d("BatchAnalysis", String.format("ANALYZING DATE: %d/%d/%d", analysisCalendarDate.getDay(), analysisCalendarDate.getMonth(), analysisCalendarDate.getYear()));

                        if (hasDataForDate(analysisCalendarDate)) {
                            Log.d("BatchAnalysis", "Analyzing...");
                            BatchAnalysisResult batchAnalysisResult = runBatchAnalysis(previousBatchAnalysisResults, analysisCalendarDate);
                            previousBatchAnalysisResults.add(batchAnalysisResult);
                        } else {
                            Log.d("BatchAnalysis", "No data...");
                        }

                    } catch (IOException | AnalysisException e) {
                        e.printStackTrace();
                    }

                    analysisTime += MILLISECONDS_IN_DAY;

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
        File sessionOutputDirectory = new File(getOutputDirectory(calendarDate), "" + startTime);
        if (!sessionOutputDirectory.exists()) {
            sessionOutputDirectory.mkdirs();
        }

        return sessionOutputDirectory;
    }

    /**
     * @param calendarDate
     * @return
     */
    public File getOutputDirectory(CalendarDate calendarDate) {
        return new File(mSleepDataStorageDirectory,
                "SleepSense_" + stringFromCalendarDate(calendarDate));
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

        File dayDirectory = getOutputDirectory(calendarDate);

        List<SessionData> sessionDataForDay = getSessionDataForDay(dayDirectory);

        BatchAnalysisResult batchAnalysisResult = batchAnalysis.analyzeSessions(
                sessionDataForDay,
                previousBatchResults,
                calendarDate,
                new BatchAnalysisContext(PreferenceService.instance().getSleepTargetTime()  * 3600f));

        TrackerUtils.logBatchAnalysisResult(batchAnalysisResult);

        return batchAnalysisResult;

    }

    public boolean hasDataForDate(CalendarDate calendarDate) {
        return getOutputDirectory(calendarDate).exists();
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
            sessionDatas.add(getSessionData(sessionDirectory));
        }

        return sessionDatas;
    }


    /**
     * Get the sessionData for a single session.
     *
     * @param sessionDirectory
     * @return
     */
    public SessionData getSessionData(File sessionDirectory) throws IOException {

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

        File metadataFile = new File(sessionDirectory, "metadata.dat");

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(metadataFile));

        long startTime = objectInputStream.readLong();
        long endTime = objectInputStream.readLong();

        objectInputStream.close();

        return new SessionData(startTime / 1000D, endTime / 1000D, timeValueFragment);

    }


}
