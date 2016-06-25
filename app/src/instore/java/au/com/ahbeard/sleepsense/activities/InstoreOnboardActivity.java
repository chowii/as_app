package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceAquisition;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerDevice;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingBluetoothFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingHelpFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingInflateMattressFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingPlacePhoneFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingSearchingFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingState;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;


/**
 * Created by neal on 20/06/2016.
 */
public class InStoreOnBoardActivity extends NewOnBoardActivity implements OnBoardingBluetoothFragment.OnActionListener, OnBoardingSearchingFragment.OnActionListener
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        transitionTo(OnBoardingBluetoothFragment.newInstance());

    }

    public static Intent getOnBoardActivity(Context context) {
        Intent intent = new Intent(context, InStoreOnBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }


    @Override
    public void onBluetoothContinueClicked() {


        mOnBoardingState.requiredPump = true;
        mOnBoardingState.requiredTracker = true;
        mOnBoardingState.requiredBase = true;
        mOnBoardingState.numberOfTrackersRequired = 2;

        requestBackgroundScanningPermissions();

    }

    @Override
    public void onScanningPermissionGranted() {

        // Do the actual searching...
        acquireDevices();

        transitionTo(OnBoardingSearchingFragment.newInstance());

    }


    @Override
    public void onSearchingAction() {

        if (mOnBoardingState.state == OnBoardingState.State.RequiredDevicesFound) {

            BaseDevice baseDevice = null;
            PumpDevice pumpDevice = null;
            TrackerDevice trackerDevice = null;
            TrackerDevice altTrackerDevice = null;

            if ( mOnBoardingState.requiredPump && mOnBoardingState.foundPump ) {
                pumpDevice = mAquiredDevices.getPumpDevices().get(0);
            }

            if ( mOnBoardingState.requiredBase && mOnBoardingState.foundBase  ) {
                baseDevice = mAquiredDevices.getBaseDevices().get(0);
            }

            if ( mOnBoardingState.requiredTracker && mOnBoardingState.foundTracker  ) {
                trackerDevice = mAquiredDevices.getTrackerDevices().get(0);

                if ( mAquiredDevices.getTrackerDevices().size() > 1 ) {
                    altTrackerDevice = mAquiredDevices.getTrackerDevices().get(1);
                }
            }

            SleepSenseDeviceService.instance().setDevices(baseDevice, pumpDevice, trackerDevice, altTrackerDevice);

            Intent intent = new Intent(this, InStoreHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(HomeActivity.EXTRA_SHOW_ON_BOARDING_COMPLETE_DIALOG, true);
            startActivity(intent);
            finish();

        } else if (mOnBoardingState.state == OnBoardingState.State.DevicesMissingAllowRetry) {

            transitionTo(OnBoardingSearchingFragment.newInstance());

            acquireDevices();

        } else if (mOnBoardingState.state == OnBoardingState.State.DevicesMissingShowHelp) {

            transitionTo(OnBoardingHelpFragment.newInstance());

        }


    }
}
