package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceAquisition;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.fragments.onboarding.ConnectingFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.ConnectionFailedFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.HelpWebViewFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingState;
import au.com.ahbeard.sleepsense.fragments.onboarding.QuestionnaireFragment;
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
        ConnectionFailedFragment.OnActionListener {

    protected OnBoardingState mOnBoardingState = new OnBoardingState();
    private OnBoardingFragment mCurrentFragment;
    protected SleepSenseDeviceAquisition mAquiredDevices;
    private GlobalVars.MattressType mattressType;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private PublishSubject<OnBoardingState> mOnBoardingEventPublishSubject = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting_pump);
        ButterKnife.bind(this);

        transitionTo(ConnectingFragment.newInstance(GlobalVars.SEARCHING_PUMP_DEVICE));
        mattressType = (GlobalVars.MattressType)getIntent().getSerializableExtra(GlobalVars.SELECTED_MATTRESS_TYPE);
        subscribeToDeviceFinder();
        findPumpDevice();
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    @Override
    public void onScanningPermissionGranted() {
        subscribeToDeviceFinder();
        findPumpDevice();
    }

    @Override
    public void onBackPressed() {
        Intent intent = MattressSizeActivity.getMattressSizeActivity(this);
        startActivity(intent);
        this.finish();
    }

    //listener for ConnectionFailedFragment action
    //{
    @Override
    public void helpMeClicked() {
        removeHelpBottomFragment();
    }

    @Override
    public void tryAgainClicked() {
        removeHelpBottomFragment();
        findPumpDevice();
    }

    @Override
    public void setupLaterClicked() {
        removeHelpBottomFragment();
        loadTrackerActivity();
    }

    @Override
    public void fragmentBackButtonPressed() {
        removeHelpBottomFragment();
        onBackPressed();
    }
    //}

    //listener for QuestionnaireFragment selection
    //{
    @Override
    public void onSelectionClicked(QuestionnaireFragment.SelectedOption selectedOption) {
        //process the selected side in case of single mattress
        if(selectedOption == QuestionnaireFragment.SelectedOption.OPTION_1) {
            //left side selected
            SharedPreferencesStore.PutItem(GlobalVars.SHARED_PREFERENCE_CONNECTING_PUMP_SIDE,
                    GlobalVars.SleepSide.LEFT.name(), getApplicationContext());
        }
        else if(selectedOption == QuestionnaireFragment.SelectedOption.OPTION_2) {
            //right side selected
            SharedPreferencesStore.PutItem(GlobalVars.SHARED_PREFERENCE_CONNECTING_PUMP_SIDE,
                    GlobalVars.SleepSide.RIGHT.name(), getApplicationContext());
        }
        loadTrackerActivity();
    }
    //}

    public static Intent getConnectingPumpActivity(Context context) {
        Intent intent = new Intent(context, ConnectingPumpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private void loadTrackerActivity(){
        Intent intent = ConnectingTrackerActivity.getConnectingTrackerActivity(this);
        startActivity(intent);
        finish();
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

    //triggered by device finder observer
    public void parseDeviceFinderResult() {
        //verify if we have found any pump devices around
        if(mAquiredDevices.getPumpDevices().size() > 0) {
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
        }
        else {
            //handle failure connecting devices
            showHelpBottomFragment();
        }
    }

    private void showHelpBottomFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(GlobalVars.FRAGMENT_DIALOG_ID);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        BottomSheetDialogFragment newFragment = ConnectionFailedFragment.newInstance();
        newFragment.show(ft,GlobalVars.FRAGMENT_DIALOG_ID);
    }

    private void removeHelpBottomFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        BottomSheetDialogFragment fragment = ConnectionFailedFragment.newInstance();
        ft.remove(fragment);
        ft.commit();
        getSupportFragmentManager().popBackStack();
    }

    protected void transitionTo(ConnectingFragment connectingFragment) {
        mCurrentFragment = connectingFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_connecting_pump, connectingFragment).commit();
    }

    protected void transitionTo(QuestionnaireFragment questionnaireFragment) {
        mCurrentFragment = questionnaireFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_connecting_pump, questionnaireFragment).commit();
    }

    public void findPumpDevice() {

        SleepSenseDeviceService.instance().scanDevices().subscribe(new Observer<SleepSenseDeviceAquisition>() {
            @Override
            public void onCompleted() {

                if (mAquiredDevices == null) {
                    // EPIC FAIL
                    showHelpBottomFragment();
                } else {

                    mOnBoardingState.foundPump = mAquiredDevices.getPumpDevices().size() > 0;
                    mOnBoardingState.state = OnBoardingState.State.ChoosingDevices;
                    mOnBoardingEventPublishSubject.onNext(mOnBoardingState);
                }
            }

            @Override
            public void onError(Throwable e) {
                showHelpBottomFragment();
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