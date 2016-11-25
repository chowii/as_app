package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import au.com.ahbeard.sleepsense.R;

import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceAquisition;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.fragments.onboarding.ConnectingFragment;
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

public class ConnectingAdjustableBaseActivity extends BaseActivity implements
        QuestionnaireFragment.OnActionListener {

    protected OnBoardingState mOnBoardingState = new OnBoardingState();
    private OnBoardingFragment mCurrentFragment;
    protected SleepSenseDeviceAquisition mAquiredDevices;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private PublishSubject<OnBoardingState> mOnBoardingEventPublishSubject = PublishSubject.create();

    public static Intent getConnectingAdjustableBaseActivity(Context context) {
        Intent intent = new Intent(context, ConnectingAdjustableBaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting_adjustable_base);
        ButterKnife.bind(this);
        QuestionnaireFragment questionnaireFragment = QuestionnaireFragment.newInstance(
                GlobalVars.ADJUSTABLE_BASE_HEADER,
                GlobalVars.YES_STRING,
                GlobalVars.NO_STRING);
        mCurrentFragment = questionnaireFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_connecting_adjustable_base, questionnaireFragment).commit();
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    //listener for QuestionnaireFragment selection
    @Override
    public void onSelectionClicked(QuestionnaireFragment.SelectedOption selectedOption) {

        if (selectedOption == QuestionnaireFragment.SelectedOption.OPTION_1) {
            //Yes selected
            //verify if bluetooth is enabled
            if (isBluetoothEnabled()) {
                transitionTo(ConnectingFragment.newInstance(GlobalVars.SEARCHING_ADJUSTABLE_BASE));
                subscribeToDeviceFinder();
                findInitialDevices();
            }
            else {
                showBluetoothOffAlertView();
            }
        } else if (selectedOption == QuestionnaireFragment.SelectedOption.OPTION_2) {
            //set up is completed
            Intent intent = SetupCompletedActivity.getSetUpCompletedActivity(this);
            startActivity(intent);
            finish();
        }

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
        if (mAquiredDevices.getBaseDevices().size() > 0) {
            Intent intent = SetupCompletedActivity.getSetUpCompletedActivity(this);
            startActivity(intent);
            finish();
        }
        else {
            //TODO: device not found, retry logic
            acquireDevices();
        }
    }

    protected void transitionTo(ConnectingFragment connectingFragment) {
        mCurrentFragment = connectingFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_connecting_adjustable_base, connectingFragment).commit();
    }

    public void findInitialDevices() {

        SleepSenseDeviceService.instance().newAcquireDevices(2500).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SleepSenseDeviceAquisition>() {

            @Override
            public void onCompleted() {

                if (mAquiredDevices == null) {
                    // EPIC FAIL
                } else {

                    mOnBoardingState.foundBase = mAquiredDevices.getBaseDevices().size() > 0;
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

    public void acquireDevices() {

        SleepSenseDeviceService.instance().newAcquireDevices(5000).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SleepSenseDeviceAquisition>() {

            @Override
            public void onCompleted() {

                if (mAquiredDevices == null) {

                } else {

                    boolean failed = false;

                    if (mOnBoardingState.requiredBase) {
                        if (mAquiredDevices.getBaseDevices().isEmpty()) {
                            failed = true;
                            mOnBoardingState.foundBase = false;
                        } else {
                            mOnBoardingState.foundBase = true;
                        }

                    }

                    AnalyticsService.instance().setUserOwnsBase(mOnBoardingState.requiredBase && mOnBoardingState.foundBase);

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
