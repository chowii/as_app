package au.com.ahbeard.sleepsense.activities;

import android.os.Bundle;
import android.util.Log;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.fragments.OnBoardConnectionsFragment;
import au.com.ahbeard.sleepsense.fragments.OnBoardInitialFragment;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class OnBoardActivity extends BaseActivity {

    @OnClick(R.id.on_board_image_view_phone)
    void click() {
        SleepSenseDeviceService.instance()
                .acquireDevices(2500)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<String>() {
                            @Override
                            public void onCompleted() {
                                Log.e("OnBoardActivity","completed acquiring devices..");
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
                                Log.e("OnBoardActivity","error acquiring devices..",e);
                            }

                            @Override
                            public void onNext(String s) {
                            }
                        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestBackgroundScanningPermissions();

        setContentView(R.layout.activity_onboard);

        ButterKnife.bind(this);

        getSupportFragmentManager().beginTransaction().add(R.id.on_board_layout_container,
                OnBoardInitialFragment.newInstance()).commit();


    }

}
