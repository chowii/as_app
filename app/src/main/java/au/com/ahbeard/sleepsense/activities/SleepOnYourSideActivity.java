package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import au.com.ahbeard.sleepsense.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SleepOnYourSideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_on_your_side);
        ButterKnife.bind(this);
    }

    public static Intent getSleepOnYourSideActivity(Context context) {
        Intent intent = new Intent(context, SleepOnYourSideActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @OnClick(R.id.button_sleep_on_your_side_continue)
    void option2Clicked() {
        //TODO: load tracker movement activity here
        Intent intent = ConnectingAdjustableBaseActivity.getConnectingAdjustableBaseActivity(this);
        startActivity(intent);
        finish();
    }
}
