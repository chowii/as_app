package au.com.ahbeard.sleepsense.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceAquisition;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpCommand;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpEvent;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerDevice;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingBluetoothFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingChooseSideFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingFirmnessControlsFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingHelpFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingInflateMattressFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingItemsFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingLieOnBedFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingMassageControlsFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingPlacePhoneFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingPositionControlsFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingSearchingFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingState;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class NewOnBoardActivity extends BaseActivity implements
        OnBoardingBluetoothFragment.OnActionListener,
        OnBoardingItemsFragment.OnActionListener,
        OnBoardingChooseSideFragment.OnActionListener,
        OnBoardingPlacePhoneFragment.OnActionListener,
        OnBoardingSearchingFragment.OnActionListener,
        OnBoardingInflateMattressFragment.OnActionListener,
        OnBoardingFirmnessControlsFragment.OnActionListener,
        OnBoardingPositionControlsFragment.OnActionListener,
        OnBoardingMassageControlsFragment.OnActionListener,
        OnBoardingHelpFragment.OnActionListener,
        OnBoardingLieOnBedFragment.OnActionListener
{

    private OnBoardingState mOnBoardingState = new OnBoardingState();

    private SleepSenseDeviceAquisition mAquiredDevices;

    public void successContinue() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    private PublishSubject<OnBoardingState> mOnBoardingEventPublishSubject = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_onboard);

        ButterKnife.bind(this);

        getSupportFragmentManager().beginTransaction().add(R.id.new_onboard_layout_container,
                OnBoardingBluetoothFragment.newInstance()).commit();

    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    @Override
    public void onBluetoothContinueClicked() {

        requestBackgroundScanningPermissions();

    }

    @Override
    public void onScanningPermissionGranted() {

        findInitialDevices();

        // Need to do the permissions check here... after the permissions check is complete, then we do our other shit.
        getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                OnBoardingItemsFragment.newInstance(true, mOnBoardingState.foundPump, mOnBoardingState.foundTracker)).commit();
    }

    @Override
    public void onItemsContinueClicked(boolean hasPump, boolean hasTracker, boolean hasBase) {

        mOnBoardingState.requiredPump = hasPump;
        mOnBoardingState.requiredTracker = hasTracker;
        mOnBoardingState.requiredBase = hasBase;

        if ( ! hasPump ) {
            mOnBoardingState.numberOfTrackersRequired = 1;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                OnBoardingChooseSideFragment.newInstance()).commit();

    }



    @Override
    public void onChooseSideContinueClicked(String sideOfBed) {

        PreferenceService.instance().setSideOfBed(sideOfBed);

        mOnBoardingState.sideOfBed = sideOfBed;

        if ( mOnBoardingState.requiredTracker ) {
            getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                    OnBoardingPlacePhoneFragment.newInstance(sideOfBed)).commit();
        } else {
            onPlacePhoneContinueClicked();
        }


    }

    @Override
    public void onPlacePhoneContinueClicked() {

        // Do the actual searching...
        getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                OnBoardingSearchingFragment.newInstance()).commit();

        acquireDevices();

    }

    @Override
    public void onSearchingAction() {

        if (mOnBoardingState.state == OnBoardingState.State.RequiredDevicesFound) {

            if ( mOnBoardingState.foundPump ) {
                getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                        OnBoardingInflateMattressFragment.newInstance()).commit();

                inflateMattress();
            } else {
                // Skip to base controls.
                onFirmnessControlsAction();
            }


        } else if (mOnBoardingState.state == OnBoardingState.State.DevicesMissingAllowRetry) {

            getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                    OnBoardingSearchingFragment.newInstance()).commit();

            acquireDevices();

        } else if (mOnBoardingState.state == OnBoardingState.State.DevicesMissingShowHelp) {

            getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                    OnBoardingHelpFragment.newInstance()).commit();

        }
    }

    @Override
    public void onInflateContinueClicked() {
        getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                OnBoardingLieOnBedFragment.newInstance(PreferenceService.instance().getSideOfBed())).commit();
    }

    @Override
    public void onLieOnBedContinueClicked() {
        getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                OnBoardingFirmnessControlsFragment.newInstance()).commit();
    }

    @Override
    public void onFirmnessControlsAction() {
        if ( mOnBoardingState.requiredBase ) {
            getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                    OnBoardingPositionControlsFragment.newInstance()).commit();
        } else {
            onMassageControlsAction();
        }
    }

    @Override
    public void onPositionControlsAction() {
        if ( mOnBoardingState.requiredBase ) {
            getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                    OnBoardingMassageControlsFragment.newInstance()).commit();
        }
    }

    @Override
    public void onMassageControlsAction() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(HomeActivity.EXTRA_SHOW_ON_BOARDING_COMPLETE_DIALOG,true);
        startActivity(intent);
        finish();
    }

    // HELP FLOW
    @Override
    public void onRetryButtonClicked() {
        onPlacePhoneContinueClicked();
    }

    @Override
    public void onCallButtonClicked() {

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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Add the buttons
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        }).setMessage("Close Sleepsense before setting up your sleep hardware?").create().show();

    }

    public void acquireDevices() {

        SleepSenseDeviceService.instance().newAcquireDevices(5000).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SleepSenseDeviceAquisition>() {

            @Override
            public void onCompleted() {

                if (mAquiredDevices == null) {

                } else {

                    boolean failed = false;

                    BaseDevice baseDevice = null;
                    PumpDevice pumpDevice = null;
                    TrackerDevice trackerDevice = null;

                    if (mOnBoardingState.requiredBase) {
                        if (mAquiredDevices.getBaseDevices().isEmpty()) {
                            failed = true;
                            mOnBoardingState.foundBase = false;
                        } else {
                            baseDevice = mAquiredDevices.getBaseDevices().get(0);
                        }
                    }

                    if (mOnBoardingState.requiredPump) {
                        if (mAquiredDevices.getPumpDevices().isEmpty()) {
                            failed = true;
                            mOnBoardingState.foundPump = false;
                        } else {
                            pumpDevice = mAquiredDevices.getPumpDevices().get(0);
                        }
                    }

                    if (mOnBoardingState.requiredTracker) {
                        if (mAquiredDevices.getTrackerDevices().size() < mOnBoardingState.numberOfTrackersRequired) {
                            failed = true;
                            mOnBoardingState.foundTracker = false;
                        } else {
                            // Get the closest tracker.
                            trackerDevice = mAquiredDevices.getTrackerDevices().get(0);
                        }
                    }

                    if (failed) {
                        mOnBoardingState.failedAttempts += 1;

                        if (mOnBoardingState.failedAttempts > 1) {
                            mOnBoardingState.state = OnBoardingState.State.DevicesMissingShowHelp;
                        } else {
                            mOnBoardingState.state = OnBoardingState.State.DevicesMissingAllowRetry;
                        }

                    } else {
                        mOnBoardingState.state = OnBoardingState.State.RequiredDevicesFound;
                        SleepSenseDeviceService.instance().setDevices(baseDevice, pumpDevice, trackerDevice);
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

        SleepSenseDeviceService.instance().getPumpDevice().getChangeObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Device>() {
            @Override
            public void call(Device device) {
                if ( mOnBoardingState.state == OnBoardingState.State.RequiredDevicesFound && device.isDisconnected() && device.getLastConnectionStatus() != 0 ) {
                    // We had an error connecting to the pump.
                    mOnBoardingState.state = OnBoardingState.State.InflationError;
                    mOnBoardingEventPublishSubject.onNext(mOnBoardingState);
                }
            }
        });

        SleepSenseDeviceService.instance().getPumpDevice().getPumpEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<PumpEvent>() {

            long mStartedInflatingAt;

            @Override
            public void call(PumpEvent pumpEvent) {

                if ( pumpEvent.isAdjustingOrCheckingPressure() && mOnBoardingState.state == OnBoardingState.State.RequiredDevicesFound ) {
                    mStartedInflatingAt = System.currentTimeMillis();
                    mOnBoardingState.state = OnBoardingState.State.Inflating;
                    mOnBoardingEventPublishSubject.onNext(mOnBoardingState);
                } else if ( ! pumpEvent.isAdjustingOrCheckingPressure() && mOnBoardingState.state == OnBoardingState.State.Inflating ) {
                    mOnBoardingState.state = OnBoardingState.State.InflationComplete;
                    mOnBoardingEventPublishSubject.onNext(mOnBoardingState);
                } else if ( pumpEvent.isAdjustingOrCheckingPressure() && mOnBoardingState.state == OnBoardingState.State.Inflating && ( System.currentTimeMillis() - mStartedInflatingAt ) > 30000 ){
                    mOnBoardingState.state = OnBoardingState.State.InflationError;
                    mOnBoardingEventPublishSubject.onNext(mOnBoardingState);
                }

            }
        });

        SleepSenseDeviceService.instance().getPumpDevice().sendCommand(PumpCommand.setPressure(PreferenceService.instance().getSideOfBed(),20));

    }

    public Observable<OnBoardingState> getOnBoardingObservable() {
        return mOnBoardingEventPublishSubject;
    }


}
