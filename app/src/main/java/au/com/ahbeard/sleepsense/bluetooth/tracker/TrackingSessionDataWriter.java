package au.com.ahbeard.sleepsense.bluetooth.tracker;

import com.beddit.analysis.TimeValueFragment;
import com.beddit.analysis.TimeValueTrackFragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.services.log.SSLog;
import rx.Observer;

/**
 * Created by neal on 20/04/2016.
 */
public class TrackingSessionDataWriter implements Observer<TimeValueFragment> {

    private long mStartTime;
    private long mEndTime;

    private Map<String, OutputStream> mTrackOutputStreams = new HashMap<>();

    private File mSessionDirectory;

    public TrackingSessionDataWriter() {
        mStartTime = System.currentTimeMillis();
        mSessionDirectory = SleepService.instance().getSessionOutputDirectory(mStartTime);
    }

    /**
     *
     */
    @Override
    public void onCompleted() {

        mEndTime = System.currentTimeMillis();

        try {

            for (String trackName : mTrackOutputStreams.keySet()) {

                OutputStream trackOutputStream = mTrackOutputStreams.get(trackName);

                if (trackOutputStream != null) {
                    trackOutputStream.close();
                }

            }

            writeSessionMetadata();

            SleepService.instance().writeSessionDataToDatabase(mSessionDirectory);
            SleepService.instance().runBatchAnalysis();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param e
     */
    @Override
    public void onError(Throwable e) {

        mEndTime = System.currentTimeMillis();

        try {
            writeError(e);
//            FirebaseCrash.log("Error thrown during sleep session capture...");
//            FirebaseCrash.report(e);
            SSLog.e("Error during sleep session capture: %s", e);
        } catch (IOException ioe) {
            SSLog.e("Error writing session data: %s",ioe);
        }

        try {

            for (String trackName : mTrackOutputStreams.keySet()) {

                OutputStream trackOutputStream = mTrackOutputStreams.get(trackName);

                if (trackOutputStream != null) {
                    trackOutputStream.close();
                }

                writeSessionMetadata();

                // Also attempt to run the batch analysis.
                SleepService.instance().writeSessionDataToDatabase(mSessionDirectory);
                SleepService.instance().runBatchAnalysis();

            }

        } catch (java.io.IOException ioe) {
            SSLog.e("Error closing session: %s",ioe);
        }

    }

    /**
     * @param timeValueFragment
     */
    @Override
    public void onNext(TimeValueFragment timeValueFragment) {

        TrackerUtils.logTimeValueFragment(timeValueFragment);

        for (String trackName : timeValueFragment.getNames()) {

            TimeValueTrackFragment timeValueTrackFragment = timeValueFragment.getTrackFragment(trackName);

            try {

                OutputStream trackOutputStream = mTrackOutputStreams.get(trackName);

                if (trackOutputStream == null) {

                    File trackOutputFile = SleepService.instance()
                            .getTrackOutputFile(mStartTime, trackName, timeValueTrackFragment.getItemType());
                    trackOutputStream = new BufferedOutputStream(new FileOutputStream(trackOutputFile),512);
                    mTrackOutputStreams.put(trackName, trackOutputStream);
                }

                trackOutputStream.write(timeValueTrackFragment.getData());

            } catch (java.io.IOException ioe) {
                ioe.printStackTrace();
            }
        }


    }

    /**
     * @throws IOException
     */
    public void writeSessionMetadata() throws IOException {

        ObjectOutputStream fileOutputStream = new ObjectOutputStream(
                new FileOutputStream(new File(mSessionDirectory, "metadata.dat")));

        fileOutputStream.writeLong(mStartTime);
        fileOutputStream.writeLong(mEndTime);

        fileOutputStream.close();

    }

    /**
     * @throws IOException
     */
    public void writeError(Throwable throwable) throws IOException {

        PrintWriter errorWriter = new PrintWriter(new FileWriter(
                new File(mSessionDirectory, "error.log"), true));

        throwable.printStackTrace(errorWriter);

        errorWriter.close();

    }


}
