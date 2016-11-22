package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceAquisition;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpCommand;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpEvent;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerDevice;
import au.com.ahbeard.sleepsense.fragments.onboarding.ConnectingFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingChooseSideFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingFirmnessControlsFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingHelpFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingInflateMattressFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingItemsFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingLieOnBedFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingMassageControlsFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingPlacePhoneFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingPositionControlsFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingSearchingFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingState;
import au.com.ahbeard.sleepsense.fragments.onboarding.QuestionnaireFragment;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.services.SharedPreferencesStore;
import au.com.ahbeard.sleepsense.utils.GlobalVars;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class ConnectingPumpActivity  extends BaseActivity implements
        QuestionnaireFragment.OnActionListener,
        OnBoardingItemsFragment.OnActionListener,
        OnBoardingChooseSideFragment.OnActionListener,
        OnBoardingPlacePhoneFragment.OnActionListener,
        OnBoardingSearchingFragment.OnActionListener,
        OnBoardingInflateMattressFragment.OnActionListener,
        OnBoardingFirmnessControlsFragment.OnActionListener,
        OnBoardingPositionControlsFragment.OnActionListener,
        OnBoardingMassageControlsFragment.OnActionListener,
        OnBoardingHelpFragment.OnActionListener,
        OnBoardingLieOnBedFragment.OnActionListener {

    protected OnBoardingState mOnBoardingState = new OnBoardingState();
    private OnBoardingFragment mCurrentFragment;
    protected SleepSenseDeviceAquisition mAquiredDevices;
    private GlobalVars.MattressType mattressType;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private PublishSubject<OnBoardingState> mOnBoardingEventPublishSubject = PublishSubject.create();

    public void parseDeviceFinderResult() {
//        Intent intent = new Intent(this, ConnectingTrackerActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();

        //verify if we have found any pump devices around
//        if(mAquiredDevices.getPumpDevices().size() > 0) {
            //Load pump questionnaire if selected mattress type is single, else take user to tracking device
            if (mattressType.equals(GlobalVars.MattressType.SINGLE)) {
                QuestionnaireFragment questionnaireFragment = QuestionnaireFragment.newInstance(
                        GlobalVars.MATTRESS_PUMP_SIDE_QUESTION,
                        GlobalVars.LEFT_STRING,
                        GlobalVars.RIGHT_STRING);
                transitionTo(questionnaireFragment);
            } else {
                Intent intent = ConnectingTrackerActivity.getConnectingTrackerActivity(this);
                startActivity(intent);
                finish();
            }
//        }
//        else {
//            //TODO: handle failure connecting devices
//        }

//        Intent intent = ConnectingTrackerActivity.getConnectingTrackerActivity(this);
//        startActivity(intent);
//        finish();
    }

    //listener for QuestionnaireFragment selection
    @Override
    public void onSelectionClicked(QuestionnaireFragment.SelectedOption selectedOption) {
        //TODO: process the selected side in case of single mattress
        if(selectedOption == QuestionnaireFragment.SelectedOption.OPTION_1) {
            //left side selected
            SharedPreferencesStore.PutItem(GlobalVars.SHARED_PREFERENCE_CONNECTING_PUMP_SIDE,
                    "LEFT", getApplicationContext());
        }
        else if(selectedOption == QuestionnaireFragment.SelectedOption.OPTION_2) {
            //right side selected
            SharedPreferencesStore.PutItem(GlobalVars.SHARED_PREFERENCE_CONNECTING_PUMP_SIDE,
                    "RIGHT", getApplicationContext());
        }
        Intent intent = ConnectingTrackerActivity.getConnectingTrackerActivity(this);
        startActivity(intent);
        finish();
    }

    public static Intent getConnectingPumpActivity(Context context) {
        Intent intent = new Intent(context, ConnectingPumpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting_pump);
        ButterKnife.bind(this);

        //TODO: why do we need location access?
//        requestBackgroundScanningPermissions();

        transitionTo(ConnectingFragment.newInstance(GlobalVars.SEARCHING_PUMP_DEVICE));
        mattressType = (GlobalVars.MattressType)getIntent().getSerializableExtra(GlobalVars.SELECTED_MATTRESS_TYPE);
        subscribeToDeviceFinder();
        findInitialDevices();
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    @Override
    public void onScanningPermissionGranted() {

        subscribeToDeviceFinder();
        findInitialDevices();
//        transitionTo(OnBoardingItemsFragment.newInstance(true, mOnBoardingState.foundPump, mOnBoardingState.foundTracker));
       // transitionTo(ConnectingFragment.newInstance());
    }

    private void subscribeToDeviceFinder() {
        mCompositeSubscription.add(getOnBoardingObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<OnBoardingState>() {
            @Override
            public void call(OnBoardingState onBoardingState) {
                if (onBoardingState.state == OnBoardingState.State.ChoosingDevices) {

                    parseDeviceFinderResult();

//                    mHasPump = true;
//                    mHasBase = onBoardingState.foundBase;
//                    mHasTracker = onBoardingState.foundTracker;
//
//                    mBaseImageView.setSelected(onBoardingState.foundBase);
//                    mBaseItemImageView.setAlpha(mBaseImageView.isSelected()?1.0f:0.0f);
//
//                    mMattressImageView.setSelected(true);
//                    mMattressItemImageView.setAlpha(mMattressImageView.isSelected()?1.0f:0.0f);
//
//                    mTrackerImageView.setSelected(onBoardingState.foundTracker);
//                    mTrackerItemImageView.setAlpha(mTrackerImageView.isSelected()?1.0f:0.0f);
//
//                    mHeaderTextView.setText("Success!");
//                    mDescriptionTextView.setText("This is what I found. Tap on the item if it's missing and I'll double check");
//
//                    mItemsLayout.animate().alpha(1.0f).start();
                }
            }
        }));
    }

    protected void transitionTo(ConnectingFragment connectingFragment) {
        mCurrentFragment = connectingFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_connecting_pump, connectingFragment).commit();
    }

    protected void transitionTo(QuestionnaireFragment questionnaireFragment) {
        mCurrentFragment = questionnaireFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_connecting_pump, questionnaireFragment).commit();
    }

    @Override
    public void onItemsContinueClicked(boolean hasPump, boolean hasTracker, boolean hasBase) {

        AnalyticsService.instance().logItemsSelected(hasPump, hasTracker, hasBase);

        mOnBoardingState.requiredPump = hasPump;
        mOnBoardingState.requiredTracker = hasTracker;
        mOnBoardingState.requiredBase = hasBase;

//        if (!hasPump) {
//            mOnBoardingState.numberOfTrackersRequired = 1;
//        }

//        transitionTo(OnBoardingChooseSideFragment.newInstance());

    }

    @Override
    public void onChooseSideContinueClicked(String sideOfBed) {
        if (sideOfBed == null) { return; }

//        PreferenceService.instance().setSideOfBed(sideOfBed);
        SharedPreferencesStore.PutItem(GlobalVars.SHARED_PREFERENCE_CONNECTING_PUMP_SIDE,
                sideOfBed, getApplicationContext());
        AnalyticsService.instance().setUserSideOfBed(sideOfBed.toLowerCase());

        mOnBoardingState.sideOfBed = sideOfBed;

//        if (mOnBoardingState.requiredTracker) {
//            transitionTo(OnBoardingPlacePhoneFragment.newInstance(sideOfBed));
//        } else {
//            onPlacePhoneContinueClicked();
//        }

    }

    @Override
    public void onPlacePhoneContinueClicked() {

        // Do the actual searching...
//        transitionTo(OnBoardingSearchingFragment.newInstance());

        acquireDevices();

    }

    @Override
    public void onSearchingAction() {

        if (mOnBoardingState.state == OnBoardingState.State.RequiredDevicesFound) {

            BaseDevice baseDevice = null;
            PumpDevice pumpDevice = null;
            TrackerDevice trackerDevice = null;
            TrackerDevice altTrackerDevice = null;

            if (mOnBoardingState.requiredPump && mOnBoardingState.foundPump) {
                pumpDevice = mAquiredDevices.getPumpDevices().get(0);
            }

            if (mOnBoardingState.requiredBase && mOnBoardingState.foundBase) {
                baseDevice = mAquiredDevices.getBaseDevices().get(0);
            }

            if (mOnBoardingState.requiredTracker && mOnBoardingState.foundTracker) {
                trackerDevice = mAquiredDevices.getTrackerDevices().get(0);

                if (mAquiredDevices.getTrackerDevices().size() > 1) {
                    altTrackerDevice = mAquiredDevices.getTrackerDevices().get(1);
                }
            }

            SleepSenseDeviceService.instance().setDevices(baseDevice, pumpDevice, trackerDevice, altTrackerDevice);

            if (mOnBoardingState.requiredPump && mOnBoardingState.foundPump) {
//                transitionTo(OnBoardingInflateMattressFragment.newInstance());

                inflateMattress();
            } else {
                // Skip to base controls.
                onFirmnessControlsAction();
            }


        } else if (mOnBoardingState.state == OnBoardingState.State.DevicesMissingAllowRetry) {

//            transitionTo(OnBoardingSearchingFragment.newInstance());

            acquireDevices();

        } else if (mOnBoardingState.state == OnBoardingState.State.DevicesMissingShowHelp) {

//            transitionTo(OnBoardingHelpFragment.newInstance("http://sleepsense.com.au/app-faq#cant-connect-to-devices"));

        }
    }

    @Override
    public void onInflateContinueClicked() {
//        if (mOnBoardingState.state == OnBoardingState.State.InflationError) {
//            transitionTo(OnBoardingHelpFragment.newInstance("http://sleepsense.com.au/app-faq#sleepsense-mattress-setup"));
//        } else {
//            transitionTo(OnBoardingLieOnBedFragment.newInstance(PreferenceService.instance().getSideOfBed()));
//        }

    }

    @Override
    public void onLieOnBedContinueClicked() {
//        transitionTo(OnBoardingFirmnessControlsFragment.newInstance());
    }

    @Override
    public void onFirmnessControlsAction() {
        if (mOnBoardingState.requiredBase) {
//            transitionTo(OnBoardingPositionControlsFragment.newInstance());
        } else {
            onMassageControlsAction();
        }
    }

    @Override
    public void onPositionControlsAction() {
        if (mOnBoardingState.requiredBase) {
//            transitionTo(OnBoardingMassageControlsFragment.newInstance());
        }
    }

    @Override
    public void onMassageControlsAction() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(HomeActivity.EXTRA_SHOW_ON_BOARDING_COMPLETE_DIALOG, true);
        startActivity(intent);
        finish();
    }

    // HELP FLOW
    @Override
    public void onRetryButtonClicked() {

        AnalyticsService.instance().logSetupErrorResolvingTryAgain();

        if (mOnBoardingState.state == OnBoardingState.State.InflationError) {
//            transitionTo(OnBoardingInflateMattressFragment.newInstance());
            inflateMattress();
        } else {
            onPlacePhoneContinueClicked();
        }
    }

    @Override
    public void onCallButtonClicked() {
        AnalyticsService.instance().logSetupErrorResolvingCallUs();

        Uri numberUri = Uri.parse("tel:1300001150");
        Intent callIntent = new Intent(Intent.ACTION_DIAL, numberUri);
        startActivity(callIntent);
    }

    public void findInitialDevices() {

        SleepSenseDeviceService.instance().newAcquireDevices(2500).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SleepSenseDeviceAquisition>() {

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

                    if (mOnBoardingState.requiredPump) {
                        if (mAquiredDevices.getPumpDevices().isEmpty()) {
                            failed = true;
                            mOnBoardingState.foundPump = false;
                        } else {
                            mOnBoardingState.foundPump = true;
                        }
                    }

                    AnalyticsService.instance().setUserOwnsMattress(mOnBoardingState.requiredPump && mOnBoardingState.foundPump);

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

    public void inflateMattress() {

        if (SleepSenseDeviceService.instance().getPumpDevice() != null) {

            PumpDevice pumpDevice = SleepSenseDeviceService.instance().getPumpDevice();

            pumpDevice.getChangeObservable().onBackpressureBuffer().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Device>() {
                @Override
                public void call(Device device) {
                    if (mOnBoardingState.state == OnBoardingState.State.RequiredDevicesFound && device.isDisconnected() && device.getLastConnectionStatus() != 0) {
                        // We had an error connecting to the pump.
                        mOnBoardingState.state = OnBoardingState.State.InflationError;
                        mOnBoardingEventPublishSubject.onNext(mOnBoardingState);
                    }
                }
            });

            pumpDevice.getPumpEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<PumpEvent>() {

                long mStartedInflatingAt;

                @Override
                public void call(PumpEvent pumpEvent) {

                    if (pumpEvent.isAdjustingOrCheckingPressure() && mOnBoardingState.state ==
                            OnBoardingState.State.RequiredDevicesFound) {
                        mStartedInflatingAt = System.currentTimeMillis();
                        mOnBoardingState.state = OnBoardingState.State.Inflating;
                        mOnBoardingEventPublishSubject.onNext(mOnBoardingState);
                    } else if (!pumpEvent.isAdjustingOrCheckingPressure() && mOnBoardingState.state ==
                            OnBoardingState.State.Inflating) {
                        mOnBoardingState.state = OnBoardingState.State.InflationComplete;
                        mOnBoardingEventPublishSubject.onNext(mOnBoardingState);
                    } else if (pumpEvent.isAdjustingOrCheckingPressure() && mOnBoardingState.state ==
                            OnBoardingState.State.Inflating && (System.currentTimeMillis() - mStartedInflatingAt) > 30000) {
                        mOnBoardingState.state = OnBoardingState.State.InflationError;
                        mOnBoardingEventPublishSubject.onNext(mOnBoardingState);
                    }

                }
            });

            pumpDevice.sendCommand(PumpCommand.setPressure(PreferenceService.instance().getSideOfBed(), 20));
        }
    }

    public Observable<OnBoardingState> getOnBoardingObservable() {
        return mOnBoardingEventPublishSubject;
    }

}