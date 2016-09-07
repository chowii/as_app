package au.com.ahbeard.sleepsense.bluetooth.tracker;

import com.beddit.analysis.AnalysisException;
import com.beddit.analysis.InputSpec;
import com.beddit.analysis.SampledFragment;
import com.beddit.analysis.SampledTrackFragment;
import com.beddit.analysis.StreamingAnalysis;
import com.beddit.analysis.TimeValueFragment;
import com.beddit.sensor.SensorException;
import com.beddit.sensor.SensorSession;
import com.beddit.sensor.SessionAccounting;
import com.beddit.synchronization.SampledTrackDescriptor;
import com.beddit.synchronization.SynchronizationException;
import com.beddit.synchronization.Synchronizer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.ahbeard.sleepsense.services.log.SSLog;
import rx.Observer;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Listener for a SensorSession. There is one of these created per sensor session.
 * <p>
 * Created by Neal Maloney on 14/04/2016.
 */
public class TrackingSessionAnalyser implements SensorSession.Listener {

    // From what I understand from reading the iOS code, this is the only frame length supported?
    public static final double FRAME_LENGTH = 0.2;

    public static final String TAG = "TrackingSessionAnalyser";

    private final TrackerDevice mTrackerDevice;

    private long mPreviousTimestamp;

    private Synchronizer mSynchronizer;
    private StreamingAnalysis mStreamingAnalysis;

    private long mAnalysisStartTime = 0;
    private long mAnalysisEndTime = 0;

    //
    // Create a publish subject to stream the sensor data to, so we can process it in order and off the
    // receiving thread.
    //
    public PublishSubject<SensorData> mSensorDataObservable = PublishSubject.create();
    public PublishSubject<TimeValueFragment> mTimeValueTrackFragmentPublishSubject = PublishSubject.create();

    public TrackingSessionAnalyser(TrackerDevice trackerDevice) {

        mTrackerDevice = trackerDevice;

        // Subscribe to the raw sensor data on a computation thread.
        mSensorDataObservable.onBackpressureBuffer().observeOn(Schedulers.computation()).subscribe(new Observer<SensorData>() {
            @Override
            public void onCompleted() {
                endSensorSession();
            }

            @Override
            public void onError(Throwable e) {
                errorSensorSession(e);
            }

            @Override
            public void onNext(SensorData sensorData) {
                processSensorData(sensorData);
            }

        });

        mTimeValueTrackFragmentPublishSubject.onBackpressureBuffer()
                .observeOn(Schedulers.computation())
                .subscribe(new TrackingSessionDataWriter());

        mSensorDataObservable.onBackpressureBuffer()
                .observeOn(Schedulers.computation())
                .subscribe(new Observer<SensorData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SensorData sensorData) {
                        mTrackerDevice.logPacket();
                    }
                });

    }

    /**
     *
     * @param sensorSession the sensor session in question
     */
    @Override
    public void onSensorSessionOpened(final SensorSession sensorSession) {

        SSLog.d("onSensorSessionOpened sensor details: " + sensorSession.getSensorDetails());

        mTrackerDevice.setTrackerState(TrackerDevice.TrackerState.Connected);

        startSensorSession(sensorSession);

    }

    /**
     *
     * @param sensorSession sensor session in question
     * @param data ready parsed data for a single track
     * @param trackName name of the track data belongs to
     * @param sampleIndex the index of the first sample in the data
     */
    @Override
    public void onSensorSessionReceivedData(SensorSession sensorSession, byte[] data, String trackName, int sampleIndex) {
        // Use the observable to publish the data to the computation thread.
        mSensorDataObservable.onNext(new SensorData(data, trackName, sampleIndex, System.currentTimeMillis() - mAnalysisStartTime));
    }

    /**
     *
     * @param sensorSession the sensor session in question
     * @param accounting accounting information gathered during the session
     * @param error null or error session finished with
     */
    @Override
    public void onSensorSessionFinished(SensorSession sensorSession, SessionAccounting accounting, SensorException error) {

        mTrackerDevice.setTrackerState(TrackerDevice.TrackerState.Disconnected);

        if (error == null) {
            SSLog.d("onSensorSessionFinished: " + accounting.totalNumberOfPaddedSamples + " : " + accounting.totalNumberOfPaddingEvents);

            // Once again, shift this over to a computation thread.
            mSensorDataObservable.onCompleted();
        } else {
            mTrackerDevice.setTrackerState(TrackerDevice.TrackerState.Error);

            SSLog.e("onSensorSessionFinished: " + accounting.totalNumberOfPaddedSamples + " : " + accounting.totalNumberOfPaddingEvents);
            SSLog.e("error: " + error.getMessage());
            // Once again, shift this over to a computation thread.
            mSensorDataObservable.onError(error);
        }

    }

    /**
     *
     * @param sensorSession the sensor session in question
     * @param trackName name of the track that was padded
     * @param numberOfSamples number of samples added
     */
    @Override
    public void onSensorSessionPaddedTrack(SensorSession sensorSession, String trackName, int numberOfSamples) {
        // Still unsure if we're supposed to do anything here.
        SSLog.d("onSensorPaddedTrack: " + trackName + " : " + numberOfSamples);
    }


    /**
     * Initialize our sensor session.
     *
     * @param sensorSession
     */
    private void startSensorSession(final SensorSession sensorSession) {

        mAnalysisStartTime = System.currentTimeMillis();

        SSLog.d("startSensorSessionCalled...");

        // Create the InputSpec.
        InputSpec inputSpec = new InputSpec(FRAME_LENGTH);

        for (String trackName : sensorSession.getSensorDetails().getTrackNames()) {
            // Add the channel to the InputSpec.
            inputSpec.addChannel(trackName, (int) (sensorSession.getSensorDetails().sampleRate * FRAME_LENGTH));
        }

        // Create a list of descriptors for the Synchronizer.
        List<SampledTrackDescriptor> trackDescriptors = new ArrayList<>();

        for (String trackName : sensorSession.getSensorDetails().getTrackNames()) {
            // Add the channel to the input spec.
            trackDescriptors.add(new SampledTrackDescriptor(trackName,
                    sensorSession.getSensorDetails().deviceVersion,
                    sensorSession.getSensorDetails().sampleType,
                    (int) (sensorSession.getSensorDetails().sampleRate * FRAME_LENGTH)));
        }

        mSynchronizer = new Synchronizer(trackDescriptors, (float) FRAME_LENGTH, "force", 8192);

        try {

            mStreamingAnalysis = new StreamingAnalysis(inputSpec);

            SSLog.d("Streaming Start");

            Schedulers.computation().createWorker().schedule(new Action0() {
                @Override
                public void call() {
                    sensorSession.startStreaming();
                    mTrackerDevice.setTrackerState(TrackerDevice.TrackerState.StartingTracking);
                }
            },2500, TimeUnit.MILLISECONDS);


        } catch (AnalysisException e) {
            sensorSession.close();
        }
    }

    /**
     * Process the sensor data received from the Beddit device.
     *
     * @param sensorData
     */
    private void processSensorData(SensorData sensorData) {

        if ( mTrackerDevice.getTrackerState() != TrackerDevice.TrackerState.Tracking ) {
            mTrackerDevice.setTrackerState(TrackerDevice.TrackerState.Tracking);
        }

        // Append the data to the synchronizer and get the fragment if we have one.
        // the timestamps must be strictly incremental, but it is possible to get 2 packets
        // at the same instant from the Beddit code.  With backpressure it's also possible we might get more.
        if (sensorData.timestamp <= mPreviousTimestamp) {
            sensorData.timestamp = mPreviousTimestamp + 1;
        }

        mPreviousTimestamp = sensorData.timestamp;

        com.beddit.synchronization.SampledFragment synchronizedSampleFragment = null;
        try {
            synchronizedSampleFragment = mSynchronizer.appendData(sensorData.data, sensorData.trackName, sensorData.timestamp);
        } catch (SynchronizationException e) {
            e.printStackTrace();
        }

        if (synchronizedSampleFragment != null) {

            try {

                // Copy the fragment from the synchronizer's SampledFragment class to the JNI SampledFragment class
                SampledFragment sampledFragment = new SampledFragment(synchronizedSampleFragment.getFrameCount());

                for (int i = 0; i < synchronizedSampleFragment.getTrackNames().size(); i++) {

                    String synchronizedTrackName = (String) synchronizedSampleFragment.getTrackNames().get(i);

                    SampledTrackDescriptor sampledTrackDescriptor = synchronizedSampleFragment.getTrackDescriptor(synchronizedTrackName);

                    SampledTrackFragment sampledTrackFragment = new SampledTrackFragment(
                            synchronizedSampleFragment.getTrackData(synchronizedTrackName),
                            sampledTrackDescriptor.getSamplesPerFrame(),
                            sampledTrackDescriptor.getSampleType());

                    sampledFragment.addTrackFragment(synchronizedTrackName, sampledTrackFragment);

                }

                mTimeValueTrackFragmentPublishSubject.onNext(mStreamingAnalysis.analyze(sampledFragment));

            } catch (AnalysisException e) {
                SSLog.e("exception analysing raw stream data: %s", e.getMessage());
            }

        }
    }


    /**
     *
     */
    private void endSensorSession() {
        try {

            if (mStreamingAnalysis != null) {
                SSLog.d("Finalizing analysis");

                TimeValueFragment timeValueFragment = mStreamingAnalysis.finalizeAnalysis();

                SSLog.d("Analysis finalized");

                if (timeValueFragment != null) {
                    mTimeValueTrackFragmentPublishSubject.onNext(timeValueFragment);
                }

                mTimeValueTrackFragmentPublishSubject.onCompleted();
            }

        } catch (AnalysisException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void errorSensorSession(Throwable e) {
        if (mTimeValueTrackFragmentPublishSubject != null) {
            mTimeValueTrackFragmentPublishSubject.onError(e);
        }
    }

    //
    // Structure to hold the sensor data to publish via the observable.
    //
    private static class SensorData implements Serializable {

        byte[] data;
        String trackName;
        int sampleIndex;
        long timestamp;

        public SensorData(byte[] data, String trackName, int sampleIndex, long timestamp) {
            this.data = data;
            this.trackName = trackName;
            this.sampleIndex = sampleIndex;
            this.timestamp = timestamp;
        }

    }


}
