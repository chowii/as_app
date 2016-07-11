package au.com.ahbeard.sleepsense.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.BluetoothService;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerDevice;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
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

public class SleepTrackingActivity extends BaseActivity {

    private Subscription mClockSubscription;

    @OnClick(R.id.sleep_tracking_button_start_stop)
    void onStopTracking() {
        TrackerDevice trackerDevice = SleepSenseDeviceService.instance().getTrackerDevice();

        if (trackerDevice != null && trackerDevice.isTracking()) {
            new AlertDialog.Builder(this).setPositiveButton(R.string.sleep_tracking_dialog_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mIgnoreStateUpdate = true;
                    AnalyticsService.instance().logSleepScreenStopTracking();
                    Schedulers.computation().createWorker().schedule(new Action0() {
                        @Override
                        public void call() {
                            if (SleepSenseDeviceService.instance().getTrackerDevice() != null)
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

    @Bind(R.id.sleep_tracking_layout_tracking)
    View mClockLayout;

    @Bind(R.id.sleep_tracking_layout_connecting)
    View mConnectingLayout;

    @Bind(R.id.sleep_tracking_layout_error)
    View mErrorLayout;

    @Bind(R.id.sleep_tracking_image_view_connecting)
    ImageView mConnectingImageView;

    @OnClick(R.id.sleep_tracking_button_error)
    void tryAgainButton_onClick() {
        //Relaunch activity for now
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private int spawnerCounter = 0;
    private boolean mIsPaused = false;
    private boolean mIgnoreStateUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sleep_tracking);

        ButterKnife.bind(this);

        mCalendar = Calendar.getInstance();

        TrackerDevice trackerDevice = SleepSenseDeviceService.instance().getTrackerDevice();

        if (!BluetoothService.instance().isBluetoothEnabled() || trackerDevice == null) {
            showErrorView();
        } else if (trackerDevice.getTrackerState() == TrackerDevice.TrackerState.Tracking) {
            showClockView();
        } else {
            showConnectingView();
        }

        if (trackerDevice != null) {
            trackerDevice.getTrackingStateObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<TrackerDevice.TrackerState>() {
                        @Override
                        public void call(TrackerDevice.TrackerState trackerState) {
                            if (mIgnoreStateUpdate) return;
                            switch (trackerState) {
                                case Connecting:
                                    hideClockView();
                                    hideErrorView();
                                    showConnectingView();
                                case Tracking:
                                    hideConnectionView();
                                    hideErrorView();
                                    showClockView();
                                    break;
                                case Disconnected:
                                case Error:
                                    hideConnectionView();
                                    hideClockView();
                                    showErrorView();
                                    break;
                                default: break;
                            }
                        }
                    });

            trackerDevice
                    .getPacketCountObservable()
                    .sample(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer packetCount) {
                            mSampleCountTextView.setText(Integer.toString(packetCount));

                            if (!mIsPaused && spawnerCounter % 2 == 0) {
                                spawnZed();
                            }
                            spawnerCounter++;
                        }
                    });

            startSleepSession();
        }



        for (int i = 0; i < 20; i++){ //prefill zed pool
            zedsPool.add(createZed());
        }
    }

    void startSleepSession() {
        TrackerDevice trackerDevice = SleepSenseDeviceService.instance().getTrackerDevice();
        if (trackerDevice != null)
            trackerDevice.startSensorSession();

        if ( SleepSenseDeviceService.instance().getPumpDevice() != null )
            SleepSenseDeviceService.instance().getPumpDevice().connectToGetFirmness();
    }

    void showConnectingView() {
        mConnectingLayout.setVisibility(View.VISIBLE);
        RotateAnimation rotateAnimation = new RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mConnectingImageView.startAnimation(rotateAnimation);
    }

    void hideConnectionView() {
        mConnectingLayout.setVisibility(View.GONE);
    }

    void showClockView() {
        mClockLayout.setVisibility(View.VISIBLE);
    }

    void hideClockView() {
        mClockLayout.setVisibility(View.GONE);
    }

    void showErrorView() {
        mErrorLayout.setVisibility(View.VISIBLE);
    }

    void hideErrorView() {
        mErrorLayout.setVisibility(View.GONE);
    }

    int mHours;
    int mMinutes;
    int mAMPM;
    Calendar mCalendar;

    @Override
    protected void onStart() {
        super.onStart();

        mIsPaused = false;

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
    }

    @Override
    protected void onStop() {
        if (mClockSubscription != null) {
            mClockSubscription.unsubscribe();
            mClockSubscription = null;
        }
        super.onStop();

        mIsPaused = true;
    }

    @Bind(R.id.zeds_container)
    FrameLayout zedsContainer;
    Long animationDuration = 5500L;
    ArrayList<ImageView> zedsPool = new ArrayList<>();
    Random r = new Random();

    private void spawnZed() {
        final ImageView zed = zedsPool.size() > 0 ? zedsPool.remove(zedsPool.size() - 1) : createZed();

        final int width = zedsContainer.getWidth();
        final int height = zedsContainer.getHeight();

        float spawnWidth = dpToPx(50);
        float goalWidth = dpToPx(70);

        int spawnMargin = Math.round(spawnWidth / 2);
        float fromXDelta = spawnMargin - r.nextInt(Math.round(spawnWidth));
        float toXDelta = r.nextInt(Math.round(goalWidth)) * (fromXDelta > 0 ? 1 : -1);
        float toYDelta = dpToPx(-100);

        float angle = Math.round(Math.toDegrees(-Math.atan(-toYDelta / (toXDelta - fromXDelta))));

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) zed.getLayoutParams();
        layoutParams.leftMargin = (width - zed.getWidth()) / 2 + Math.round(fromXDelta);
        layoutParams.topMargin = height - zed.getHeight();
        zed.setLayoutParams(layoutParams);
        zed.setVisibility(View.VISIBLE);

        AnimationSet animationSet = new AnimationSet(false);

        TranslateAnimation moveAnim = new TranslateAnimation(fromXDelta, toXDelta, 0, toYDelta);
        moveAnim.setInterpolator(new DecelerateInterpolator());
        moveAnim.setDuration(animationDuration);
        moveAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                recycleZed(zed);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationSet.addAnimation(moveAnim);

        ScaleAnimation scaleAnim = new ScaleAnimation(0, 1.5f, 0, 1.5f);
        scaleAnim.setInterpolator(new DecelerateInterpolator());
        scaleAnim.setDuration(animationDuration);
        animationSet.addAnimation(scaleAnim);

        AlphaAnimation alphaAnim = new AlphaAnimation(1, 0);
        alphaAnim.setDuration(animationDuration);
        animationSet.addAnimation(alphaAnim);

//        RotateAnimation rotateAnimation = new RotateAnimation(0, angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnimation.setDuration(animationDuration);
//        animationSet.addAnimation(rotateAnimation);

        zed.startAnimation(animationSet);
    }

    private void recycleZed(ImageView zed) {
        zed.setVisibility(View.INVISIBLE);
        zedsPool.add(zed);
    }

    private ImageView createZed() {
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.zed_icon));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setVisibility(View.INVISIBLE);
        zedsContainer.addView(imageView);
        return imageView;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
