package au.com.ahbeard.sleepsense.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.SleepSenseApplication;
import au.com.ahbeard.sleepsense.bluetooth.BluetoothEvent;
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
import rx.subjects.PublishSubject;

public class SleepTrackingActivity extends BaseActivity {

    private Subscription mClockSubscription;

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

    @Bind(R.id.sleep_tracking_layout_bluetooth_off)
    View mBluetoothOffLayout;

    @Bind(R.id.sleep_tracking_image_view_connecting)
    ImageView mConnectingImageView;

    @Bind(R.id.zeds_container)
    FrameLayout zedsContainer;

    private int spawnerCounter = 0;
    private boolean mIsPaused = false;
    private boolean mIgnoreStateUpdate;

    private PublishSubject<TrackerDevice.TrackerState> mStateSubject;
    private Subscription mTrackerSubscription;

    int mHours;
    int mMinutes;
    int mAMPM;
    Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracking);
        ButterKnife.bind(this);

        mCalendar = Calendar.getInstance();


        mStateSubject = PublishSubject.create();
        mStateSubject
                .compose(this.<TrackerDevice.TrackerState>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TrackerDevice.TrackerState>() {
                    @Override
                    public void call(TrackerDevice.TrackerState trackerState) {
                        if (mIgnoreStateUpdate) return;
                        switch (trackerState) {
                            case Connecting:
                                showConnectingView();
                                break;
                            case Tracking:
                                showClockView();
                                break;
                            case Disconnected:
                            case Error:
                                showErrorView();
                                if (SleepSenseDeviceService.instance().getTrackerDevice() != null)
                                    SleepSenseDeviceService.instance().getTrackerDevice().cleanUp();
                                break;
                            default: break;
                        }
                    }
                });

        connectToTracker();

        prefillZedsPool();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mIsPaused = false;

        // Not using a standard date format here, because we will probably have to split it up into multiple
        // fields to get the alignment to work correctly.
        updateClockText();
        mClockSubscription = Observable.interval(0, 50, TimeUnit.MILLISECONDS)
                .filter(new Func1<Long, Boolean>() {
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
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long elapsedTime) {
                        updateClockText();
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

    @Override
    public void onBackPressed() {
        if (SleepSenseDeviceService.instance().getTrackerDevice().getTrackerState() == TrackerDevice.TrackerState.Tracking) {
            onStopTracking();
        } else {
            finish();
        }
    }

    @OnClick(R.id.sleep_tracking_button_bluetooth_off)
    void turnOnBluetooth() {
        SleepSenseApplication.instance().showBluetoothOffAlertDialog(this);
    }

    @OnClick(R.id.sleep_tracking_button_error)
    void tryAgainButton_onClick() {
        connectToTracker();
    }

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

    private void connectToTracker() {
        BluetoothService.instance().waitForPowerOn()
                .compose(this.<BluetoothEvent>bindToLifecycle())
                .subscribe(new Action1<BluetoothEvent>() {
                    @Override
                    public void call(BluetoothEvent bluetoothEvent) {
                        setupTracker();
                    }
                });

        if (!BluetoothService.instance().isBluetoothEnabled(false)) {
            showTurnOnBluetoothView();
        } else {
            mStateSubject.onNext(TrackerDevice.TrackerState.Connecting);
        }
    }

    private void setupTracker() {
        TrackerDevice trackerDevice = SleepSenseDeviceService.instance().getTrackerDevice();

        if (trackerDevice == null) {
            mStateSubject.onNext(TrackerDevice.TrackerState.Error);
        } else if (trackerDevice.getTrackerState() == TrackerDevice.TrackerState.Tracking) {
            mStateSubject.onNext(TrackerDevice.TrackerState.Tracking);
        } else {
            //Clean up previous subscription to tracker state
            //Link tracker state to state subject
            if (mTrackerSubscription != null) {
                mTrackerSubscription.unsubscribe();
                mTrackerSubscription = null;
            }
            mTrackerSubscription = trackerDevice.getTrackingStateObservable()
                    .compose(this.<TrackerDevice.TrackerState>bindToLifecycle())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<TrackerDevice.TrackerState>() {
                        @Override
                        public void call(TrackerDevice.TrackerState trackerState) {
                            mStateSubject.onNext(trackerState);
                        }
                    });

            turnOnZepSpawner();

            startSleepSession();
        }
    }

    private void startSleepSession() {
        TrackerDevice trackerDevice = SleepSenseDeviceService.instance().getTrackerDevice();
        if (trackerDevice != null)
            trackerDevice.startSensorSession();

        if ( SleepSenseDeviceService.instance().getPumpDevice() != null )
            SleepSenseDeviceService.instance().getPumpDevice().connectToGetFirmness();
    }

    private void showConnectingView() {
        hideClockView();
        hideErrorView();
        hideTurnOnBluetoothView();

        mConnectingLayout.setVisibility(View.VISIBLE);
        RotateAnimation rotateAnimation = new RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mConnectingImageView.startAnimation(rotateAnimation);
    }

    private void hideConnectionView() {
        mConnectingLayout.setVisibility(View.GONE);
    }

    private void showClockView() {
        hideConnectionView();
        hideErrorView();
        hideTurnOnBluetoothView();

        mClockLayout.setVisibility(View.VISIBLE);
    }

    private void hideClockView() {
        mClockLayout.setVisibility(View.GONE);
    }

    private void showErrorView() {
        hideConnectionView();
        hideClockView();
        hideTurnOnBluetoothView();

        mErrorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorView() {
        mErrorLayout.setVisibility(View.GONE);
    }

    private void showTurnOnBluetoothView() {
        hideClockView();
        hideConnectionView();
        hideErrorView();

        mBluetoothOffLayout.setVisibility(View.VISIBLE);
    }

    private void hideTurnOnBluetoothView() {
        mBluetoothOffLayout.setVisibility(View.GONE);
    }

    private void updateClockText() {
        mClockTimeTextView.setText(String.format(Locale.ENGLISH, "%d:%02d", mHours, mMinutes));
        mClockAmPmTextView.setText(mAMPM == Calendar.AM ? "AM" : "PM");
    }

    private Long animationDuration = 5500L;
    private ArrayList<ImageView> mZedsPool = new ArrayList<>();
    private Random r = new Random();
    private Subscription mZedsSubscription;

    private void prefillZedsPool() {
        for (int i = 0; i < 20; i++){ //prefill zed pool
            mZedsPool.add(createZed());
        }
    }

    private void turnOnZepSpawner() {
        if (mZedsSubscription != null) {
            mZedsSubscription.unsubscribe();
            mZedsSubscription = null;
        }

        TrackerDevice trackerDevice = SleepSenseDeviceService.instance().getTrackerDevice();
        if (trackerDevice != null) {
            mZedsSubscription = trackerDevice
                    .getPacketCountObservable()
                    .sample(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer packetCount) {
                            mSampleCountTextView.setText(String.format(Locale.ENGLISH, "%d", packetCount));

                            if (!mIsPaused && spawnerCounter % 2 == 0) {
                                spawnZed();
                            }
                            spawnerCounter++;
                        }
                    });
        }
    }

    private void spawnZed() {
        final ImageView zed = mZedsPool.size() > 0 ? mZedsPool.remove(mZedsPool.size() - 1) : createZed();

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
        mZedsPool.add(zed);
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
