package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;

/**
 * Created by neal on 4/05/2016.
 */
public class ClearDevicesActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SleepSenseDeviceService.instance().clearDevices();

        Intent intent = new Intent(this, OnBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }
}
