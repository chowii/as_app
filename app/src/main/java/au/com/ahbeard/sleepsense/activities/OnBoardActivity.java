package au.com.ahbeard.sleepsense.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.fragments.OnBoardConnectionsFragment;
import au.com.ahbeard.sleepsense.fragments.OnBoardInitialFragment;
import au.com.ahbeard.sleepsense.services.LogService;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class OnBoardActivity extends BaseActivity {

    @Bind(R.id.on_board_image_view_phone)
    ImageView mPhoneImageView;

    public void acquireDevices() {
        SleepSenseDeviceService.instance()
                .acquireDevices(2500)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<String>() {
                            @Override
                            public void onCompleted() {
                                LogService.i("OnBoardActivity", "completed acquiring devices..");

                                int numberOfDevices = SleepSenseDeviceService.instance().countDevices();

                                if (numberOfDevices == 0) {
                                    getSupportFragmentManager().beginTransaction().replace(R.id.on_board_layout_container,
                                            OnBoardConnectionsFragment.newInstance()).commit();
                                } else if (numberOfDevices == 1) {
                                    getSupportFragmentManager().beginTransaction().replace(R.id.on_board_layout_container,
                                            OnBoardConnectionsFragment.newInstance()).commit();
                                } else {
                                    getSupportFragmentManager().beginTransaction().replace(R.id.on_board_layout_container,
                                            OnBoardConnectionsFragment.newInstance()).commit();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogService.i("OnBoardActivity", "error acquiring devices..", e);
                            }

                            @Override
                            public void onNext(String s) {
                            }
                        });


    }

    public void successContinue() {
        finish();
    }

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestBackgroundScanningPermissions();

        setContentView(R.layout.activity_onboard);

        ButterKnife.bind(this);

        getSupportFragmentManager().beginTransaction().add(R.id.on_board_layout_container,
                OnBoardInitialFragment.newInstance()).commit();

        mCompositeSubscription.add(SleepSenseDeviceService.instance()
                .getEventObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SleepSenseDeviceService.SleepSenseDeviceServiceEvent>() {
            @Override
            public void call(SleepSenseDeviceService.SleepSenseDeviceServiceEvent sleepSenseDeviceServiceEvent) {
                if (sleepSenseDeviceServiceEvent == SleepSenseDeviceService.SleepSenseDeviceServiceEvent.StartedSearchingForDevices) {
                    mPhoneImageView.animate().rotationBy(360).setDuration(2000).start();
                } else if (sleepSenseDeviceServiceEvent == SleepSenseDeviceService.SleepSenseDeviceServiceEvent.StartedSearchingForDevices) {

                }
            }
        }));

    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }
}
