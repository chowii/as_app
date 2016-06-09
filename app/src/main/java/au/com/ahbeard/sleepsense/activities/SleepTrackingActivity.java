package au.com.ahbeard.sleepsense.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerDevice;
import au.com.ahbeard.sleepsense.services.SleepService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SleepTrackingActivity extends AppCompatActivity {


    private Subscription mClockSubscription;

    @OnClick(R.id.sleep_tracking_button_start_stop)
    void onStopTracking() {

        if (SleepSenseDeviceService.instance().getTrackerDevice().isTracking()) {
            new AlertDialog.Builder(this).setPositiveButton(R.string.sleep_tracking_dialog_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Schedulers.io().createWorker().schedule(new Action0() {
                        @Override
                        public void call() {
                            SleepSenseDeviceService.instance().getTrackerDevice().stopSensorSession();
                        }
                    });
                    finish();
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).setMessage(getString(R.string.sleep_tracking_dialog_message)).create().show();

        }
    }

    @Bind(R.id.sleep_tracking_text_view_clock_time)
    TextView mClockTimeTextView;

    @Bind(R.id.sleep_tracking_text_view_clock_am_pm)
    TextView mClockAmPmTextView;

    @Bind(R.id.sleep_tracking_text_view_sample_count)
    TextView mSampleCountTextView;

    @Bind(R.id.sleep_tracking_button_start_stop)
    Button mStartStopButton;

    @Bind(R.id.sleep_tracking_layout_connecting)
    View mConnectingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sleep_tracking);

        ButterKnife.bind(this);

        mCalendar = Calendar.getInstance();

        if ( SleepSenseDeviceService.instance().getTrackerDevice().isTracking() ) {
            mConnectingLayout.setVisibility(View.GONE);
        } else {
            mConnectingLayout.setVisibility(View.VISIBLE);
        }

        SleepSenseDeviceService.instance().getTrackerDevice()
                .getTrackingStateObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TrackerDevice.TrackerState>() {
                    @Override
                    public void call(TrackerDevice.TrackerState trackerState) {
                        if (TrackerDevice.TrackerState.StartingTracking == trackerState ) {
                            mConnectingLayout.animate().alpha(0.0f).setDuration(300).start();
                        }
                    }
                });

        SleepSenseDeviceService.instance().getTrackerDevice()
                .getPacketCountObservable()
                .sample(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer packetCount) {
                        mSampleCountTextView.setText(Integer.toString(packetCount));
                    }
                });

        SleepSenseDeviceService.instance().getTrackerDevice().startSensorSession();
    }

    private void setButtonState() {

    }

    int mHours;
    int mMinutes;
    int mAMPM;
    Calendar mCalendar;

    @Override
    protected void onStart() {

        super.onStart();

        // Not using a standard date format here, because we will probably have to split it up into multiple
        // fields to get the alignment to work correctly.
        mClockSubscription = Observable.interval(0, 50, TimeUnit.MILLISECONDS).filter(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long elapsedTime) {
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                if (mMinutes != mCalendar.get(Calendar.MINUTE)) {
                    mHours = mCalendar.get(Calendar.HOUR);
                    if (mHours == 0) {
                        mHours = 12;
                    }
                    mMinutes = mCalendar.get(Calendar.MINUTE);
                    mAMPM = mCalendar.get(Calendar.AM_PM);
                    return true;
                } else {
                    return false;
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long elapsedTime) {
                        mClockTimeTextView.setText(String.format("%d:%02d", mHours, mMinutes));
                        mClockAmPmTextView.setText(mAMPM == Calendar.AM ? "AM" : "PM");
                    }
                });

        setButtonState();
    }

    @Override
    protected void onStop() {
        if (mClockSubscription != null) {
            mClockSubscription.unsubscribe();
            mClockSubscription = null;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
