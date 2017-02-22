package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceAquisition;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.fragments.onboarding.ConnectingFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.KnowEachOtherFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingState;
import au.com.ahbeard.sleepsense.fragments.onboarding.QuestionnaireFragment;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import au.com.ahbeard.sleepsense.utils.GlobalVars;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class ConnectingTrackerActivity extends BaseActivity implements
        QuestionnaireFragment.OnActionListener,
        KnowEachOtherFragment.OnActionListener {

    protected OnBoardingState mOnBoardingState = new OnBoardingState();
    private OnBoardingFragment mCurrentFragment;
    protected SleepSenseDeviceAquisition mAquiredDevices;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private PublishSubject<OnBoardingState> mOnBoardingEventPublishSubject = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting_tracker);
        ButterKnife.bind(this);
        QuestionnaireFragment questionnaireFragment = QuestionnaireFragment.newInstance(
                GlobalVars.SLEEP_TRACKING_QUESTION,
                GlobalVars.YES_STRING,
                GlobalVars.NO_STRING);
        mCurrentFragment = questionnaireFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_connecting_tracker, questionnaireFragment).commit();
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (mCurrentFragment != null && !mCurrentFragment.onBackPressed()) {
            new AlertDialog.Builder(this).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    AnalyticsService.instance().logCloseSetup();
                    finish();
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            }).setMessage(getString(R.string.on_boarding_close_warning)).create().show();
        }

    }

    //listener for QuestionnaireFragment selection
    @Override
    public void onSelectionClicked(QuestionnaireFragment.SelectedOption selectedOption) {

        if (selectedOption == QuestionnaireFragment.SelectedOption.OPTION_1) {
            //Yes selected
            //verify if bluetooth is enabled
            if (isBluetoothEnabled()) {
                transitionTo(ConnectingFragment.newInstance(GlobalVars.SEARCHING_SLEEP_TRACKERS));
                subscribeToDeviceFinder();
                findInitialDevices();
            } else {
                showBluetoothOffAlertView();
            }
        } else if (selectedOption == QuestionnaireFragment.SelectedOption.OPTION_2) {
            //user did not purchase tracker
            Intent intent = ConnectingAdjustableBaseActivity.getConnectingAdjustableBaseActivity(this);
            startActivity(intent);
            finish();
        }

    }

    //listener for KnowEachOtherFragment.OnActionListener
    @Override
    public void onContinueClicked(KnowEachOtherFragment.Part part) {
        if (part == KnowEachOtherFragment.Part.PART1) {
            KnowEachOtherFragment knowEachOtherFragment = KnowEachOtherFragment.newInstance(
                    GlobalVars.TRACKER_PART2_TITLE,
                    GlobalVars.TRACKER_PART2_OTHER,
                    GlobalVars.TRACKER_PART2_BUTTON,
                    KnowEachOtherFragment.Part.PART2);
            transitionTo(knowEachOtherFragment);
        } else if (part == KnowEachOtherFragment.Part.PART2) {
            //take user to questionnaires
            Intent intent = HeightActivity.getHeightActivity(this);
            startActivity(intent);
            finish();
        }
    }

    public static Intent getConnectingTrackerActivity(Context context) {
        Intent intent = new Intent(context, ConnectingTrackerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private void subscribeToDeviceFinder() {
        mCompositeSubscription.add(getOnBoardingObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<OnBoardingState>() {
            @Override
            public void call(OnBoardingState onBoardingState) {
                if (onBoardingState.state == OnBoardingState.State.ChoosingDevices) {
                    parseDeviceFinderResult();
                }
            }
        }));
    }

    public void parseDeviceFinderResult() {
        //verify if we have found any tracking devices around
//        if (mAquiredDevices.getTrackerDevices().size() > 0) {
        KnowEachOtherFragment knowEachOtherFragment = KnowEachOtherFragment.newInstance(
                GlobalVars.TRACKER_PART1_TITLE,
                GlobalVars.TRACKER_PART1_OTHER,
                GlobalVars.TRACKER_PART1_BUTTON,
                KnowEachOtherFragment.Part.PART1);
        transitionTo(knowEachOtherFragment);
//        }
//        else {
//            //TODO: device not found, retry logic
//        }
    }

    protected void transitionTo(ConnectingFragment connectingFragment) {
        mCurrentFragment = connectingFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_connecting_tracker, connectingFragment).commit();
    }

    protected void transitionTo(KnowEachOtherFragment knowEachOtherFragment) {
        mCurrentFragment = knowEachOtherFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_connecting_tracker, knowEachOtherFragment).commit();
    }

    public void findInitialDevices() {

        SleepSenseDeviceService.instance().scanDevices().subscribe(new Observer<SleepSenseDeviceAquisition>() {
            @Override
            public void onComplete() {

            @Override
            public void onCompleted() {

                if (mAquiredDevices == null) {
                    // EPIC FAIL
                } else {

                    mOnBoardingState.foundBase = mAquiredDevices.getBaseDevices().size() > 0;
                    mOnBoardingState.foundPump = mAquiredDevices.getPumpDevices().size() > 0;
                    mOnBoardingState.foundTracker = mAquiredDevices.getTrackerDevices().size() > 0;
                    mOnBoardingState.state = OnBoardingState.State.ChoosingDevices;

                    mOnBoardingEventPublishSubject.onNext(mOnBoardingState);

                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(SleepSenseDeviceAquisition sleepSenseDeviceAquisition) {
                mAquiredDevices = sleepSenseDeviceAquisition;
            }

        });


    }

    public void acquireDevices() {

        SleepSenseDeviceService.instance().scanDevices().subscribe(new Observer<SleepSenseDeviceAquisition>() {
            @Override
            public void onSubscribe(Disposable d) {

            @Override
            public void onCompleted() {

                if (mAquiredDevices == null) {

                } else {

                    boolean failed = false;

                    if (mOnBoardingState.requiredTracker) {
                        if (mAquiredDevices.getTrackerDevices().size() < mOnBoardingState.numberOfTrackersRequired) {
                            failed = true;
                            mOnBoardingState.foundTracker = false;
                        } else {
                            // Get the closest tracker.

                            mOnBoardingState.foundTracker = true;
                        }
                    }

                    AnalyticsService.instance().setUserOwnsTracker(mOnBoardingState.requiredTracker && mOnBoardingState.foundTracker);

                    if (failed) {
                        mOnBoardingState.failedAttempts += 1;

                        if (mOnBoardingState.failedAttempts > 1) {
                            mOnBoardingState.state = OnBoardingState.State.DevicesMissingShowHelp;
                        } else {
                            mOnBoardingState.state = OnBoardingState.State.DevicesMissingAllowRetry;
                        }

                    } else {
                        mOnBoardingState.state = OnBoardingState.State.RequiredDevicesFound;
                    }

                    mOnBoardingEventPublishSubject.onNext(mOnBoardingState);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(SleepSenseDeviceAquisition sleepSenseDeviceAquisition) {
                mAquiredDevices = sleepSenseDeviceAquisition;
            }

        });

    }

    public Observable<OnBoardingState> getOnBoardingObservable() {
        return mOnBoardingEventPublishSubject;
    }


}