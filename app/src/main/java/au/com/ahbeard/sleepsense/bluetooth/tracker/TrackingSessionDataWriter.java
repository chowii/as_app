package au.com.ahbeard.sleepsense.bluetooth.tracker;

import com.beddit.analysis.CalendarDate;
import com.beddit.analysis.TimeValueFragment;
import com.beddit.analysis.TimeValueTrackFragment;

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

    public TrackingSessionDataWriter(CalendarDate calendarDate) {
        mStartTime = System.currentTimeMillis();
        mCalendarDate = calendarDate;
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

        ObjectOutputStream fileOutputStream = new ObjectOutputStream(new FileOutputStream(new File(SleepService.instance().getSessionOutputDirectory(mCalendarDate, mStartTime), "metadata.dat")));

        fileOutputStream.writeLong(mStartTime);
        fileOutputStream.writeLong(mEndTime);

        fileOutputStream.close();

    }

    /**
     * @throws IOException
     */
    public void writeError(Throwable throwable) throws IOException {

        PrintWriter errorWriter = new PrintWriter(new FileWriter(new File(SleepService.instance().getSessionOutputDirectory(mCalendarDate, mStartTime), "error.log"),true));

        throwable.printStackTrace(errorWriter);

        errorWriter.close();

    }


}
