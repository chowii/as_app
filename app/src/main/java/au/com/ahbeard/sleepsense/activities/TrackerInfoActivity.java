package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import au.com.ahbeard.sleepsense.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrackerInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_info);
        ButterKnife.bind(this);
    }

    public static Intent getTrackerInfoActivity(Context context) {
        Intent intent = new Intent(context, TrackerInfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }


    @OnClick(R.id.button_tracker_info_continue)
    void option2Clicked() {
        Intent intent = SleepOnYourSideActivity.getSleepOnYourSideActivity(this);
        startActivity(intent);
        finish();
    }
}
