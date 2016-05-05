package au.com.ahbeard.sleepsense.bluetooth.tracker;

import android.os.Environment;
import android.util.Log;

import com.beddit.analysis.CalendarDate;
import com.beddit.analysis.TimeValueFragment;
import com.beddit.analysis.TimeValueTrackFragment;
import com.nostra13.universalimageloader.utils.StorageUtils;

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
import rx.Observer;

/**
 * Created by neal on 20/04/2016.
 */
public class TrackingSessionDataWriter implements Observer<TimeValueFragment> {

    private CalendarDate mCalendarDate;

    private long mStartTime;
    private long mEndTime;

    private Map<String, OutputStream> mTrackOutputStreams = new HashMap<>();

    private File mSessionDirectory;

    public TrackingSessionDataWriter(CalendarDate calendarDate) {
        mStartTime = System.currentTimeMillis();
        mCalendarDate = calendarDate;
        mSessionDirectory = SleepService.instance().getSessionOutputDirectory(mCalendarDate, mStartTime);
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
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        try {

            for (String trackName : mTrackOutputStreams.keySet()) {

                OutputStream trackOutputStream = mTrackOutputStreams.get(trackName);

                if (trackOutputStream != null) {
                    trackOutputStream.close();
                }

                writeSessionMetadata();
            }

        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
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

                    File trackOutputFile = SleepService.instance().getTrackOutputFile(mCalendarDate, mStartTime, trackName, timeValueTrackFragment.getItemType());
                    trackOutputStream = new FileOutputStream(trackOutputFile);
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
        Log.d("EXTERNAL",""+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        ObjectOutputStream fileOutputStream = new ObjectOutputStream(new FileOutputStream(new File(mSessionDirectory, "metadata.dat")));

        fileOutputStream.writeLong(mStartTime);
        fileOutputStream.writeLong(mEndTime);

        fileOutputStream.close();

    }

    /**
     * @throws IOException
     */
    public void writeError(Throwable throwable) throws IOException {

        PrintWriter errorWriter = new PrintWriter(new FileWriter(new File(mSessionDirectory, "error.log"),true));

        throwable.printStackTrace(errorWriter);

        errorWriter.close();

    }


}
