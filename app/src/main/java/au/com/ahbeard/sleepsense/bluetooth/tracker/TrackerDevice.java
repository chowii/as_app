package au.com.ahbeard.sleepsense.bluetooth.tracker;

import android.content.Context;
import android.os.PowerManager;

import com.beddit.sensor.Sensor;
import com.beddit.sensor.SensorException;
import com.beddit.sensor.SensorManager;
import com.beddit.sensor.SensorSession;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothService;
import au.com.ahbeard.sleepsense.bluetooth.Device;

import au.com.ahbeard.sleepsense.services.log.SSLog;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by neal on 4/03/2016.
 */
public class TrackerDevice extends Device {

    private Sensor mSensor;
    private SensorSession mSensorSession;

    private PublishSubject<TrackerState> mTrackerStateSubject = PublishSubject.create();
    private PublishSubject<Integer> mPacketCountSubject = PublishSubject.create();

    private int mPacketCount = 0;
    private PowerManager.WakeLock mWakeLock;

    private TrackerState mTrackerState;

    public TrackerState getTrackerState() {
        return mTrackerState;
    }

    public enum TrackerState {
        Error,
        Disconnected,
        Connecting,
        Connected,
        StartingTracking,
        Tracking,
        Disconnecting
    }

    public Sensor getSensor() {

        if (mSensor == null && isLinked()) {
            mSensor = new Sensor(getAddress(), "Beddit", true);
        }

        return mSensor;

    }

    public void startSensorSession() {

        if ( mSensorSession != null ) {
            return;
        }

        if (BluetoothService.instance().isBluetoothDisabled()) {
            return;
        }

        PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);

        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "SleepTrackingWakeLock");
        mWakeLock.acquire();

        try {
            // Hack to load the library here.  It was the ONLY way I could get JNI to load the included
            // .so library for the Beddit jar file. The .so file probably needs to be contained in the
            // JAR file to get linked properly.
            Runtime runtime = Runtime.getRuntime();
            runtime.loadLibrary("mobile-analysis-jni");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mSensorSession = SensorManager.getInstance().createSession(getSensor());
            mSensorSession.setListener(new TrackingSessionAnalyser(this));
            SSLog.d("Sensor session created...");
            mSensorSession.open();
            SSLog.d("Sensor session open called...");
            setTrackerState(TrackerState.StartingTracking);
        } catch (SensorException e) {
            e.printStackTrace();
        }
    }

    public void stopSensorSession() {
        setTrackerState(TrackerState.Disconnecting);
        SSLog.d("Sensor session stop.");
        cleanUp();
    }

    public void cleanUp() {
        SSLog.d("Sensor session cleaning up.");
        if (mSensorSession != null) {
            mSensorSession.close();
            mSensorSession = null;
        }

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mWakeLock = null;
    }

    public boolean isTracking() {
        return mTrackerState == TrackerState.Tracking;
    }

    public Observable<TrackerState> getTrackingStateObservable() {
        return mTrackerStateSubject.hide();
    }

    public Observable<Integer> getPacketCountObservable() {
        return mPacketCountSubject.hide();
    }

    public void logPacket() {
        mPacketCountSubject.onNext(++mPacketCount);
    }

    public void setTrackerState(TrackerState trackerState) {
        mTrackerState = trackerState;
        mTrackerStateSubject.onNext(mTrackerState);
    }
}
