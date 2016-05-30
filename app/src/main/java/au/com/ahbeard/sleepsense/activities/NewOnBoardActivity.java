package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.os.Bundle;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceAquisition;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerDevice;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingBluetoothFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingChooseSideFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingHelpFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingInflateMattressFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingItemsFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingPlacePhoneFragment;
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
        OnBoardingSearchingFragment.OnActionListener {

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

        requestBackgroundScanningPermissions();

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

        findInitialDevices();

        mCompositeSubscription.add(getOnBoardingObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<OnBoardingState>() {
            @Override
            public void call(OnBoardingState onBoardingState) {
                if (onBoardingState.state == OnBoardingState.State.Locating) {
                    // Need to do the permissions check here... after the permissions check is complete, then we do our other shit.
                    getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                            OnBoardingItemsFragment.newInstance(onBoardingState.foundBase, onBoardingState.foundPump, onBoardingState.foundTracker)).commit();
                }
            }
        }));

    }

    @Override
    public void onItemsContinueClicked(boolean hasPump, boolean hasTracker, boolean hasBase) {

        mOnBoardingState.requiredBase = hasBase;
        mOnBoardingState.requiredTracker = hasTracker;
        mOnBoardingState.requiredPump = hasPump;

        getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                OnBoardingChooseSideFragment.newInstance()).commit();

    }

    @Override
    public void onChooseSideContinueClicked(String sideOfBed) {

        PreferenceService.instance().setSideOfBed(sideOfBed);

        mOnBoardingState.sideOfBed = sideOfBed;

        getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                OnBoardingPlacePhoneFragment.newInstance(sideOfBed)).commit();

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

            getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                    OnBoardingInflateMattressFragment.newInstance()).commit();

            inflateMattress();

        } else if (mOnBoardingState.state == OnBoardingState.State.DevicesMissingAllowRetry) {

            getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                    OnBoardingSearchingFragment.newInstance()).commit();

            acquireDevices();

        } else if (mOnBoardingState.state == OnBoardingState.State.DevicesMissingShowHelp) {

            getSupportFragmentManager().beginTransaction().replace(R.id.new_onboard_layout_container,
                    OnBoardingHelpFragment.newInstance()).commit();

        }
    }


    public void findInitialDevices() {

        SleepSenseDeviceService.instance().newAcquireDevices(5000).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SleepSenseDeviceAquisition>() {

            @Override
            public void onCompleted() {

                if (mAquiredDevices == null) {
                    // EPIC FAIL
                } else {

                    mOnBoardingState.foundBase = mAquiredDevices.getBaseDevices().size() > 0;
                    mOnBoardingState.foundPump = mAquiredDevices.getPumpDevices().size() > 0;
                    mOnBoardingState.foundTracker = mAquiredDevices.getTrackerDevices().size() > 1;
                    mOnBoardingState.state = OnBoardingState.State.Locating;

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

        SleepSenseDeviceService.instance().newAcquireDevices(5000).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SleepSenseDeviceAquisition>() {

            @Override
            public void onCompleted() {

                if (mAquiredDevices == null) {
                    // EPIC FAIL
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
                        if (mAquiredDevices.getTrackerDevices().size() < 2) {
                            failed = true;
                            mOnBoardingState.foundTracker = false;
                        } else {
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

    }

    public Observable<OnBoardingState> getOnBoardingObservable() {
        return mOnBoardingEventPublishSubject;
    }


}
