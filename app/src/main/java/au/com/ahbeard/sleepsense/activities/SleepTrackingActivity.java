package au.com.ahbeard.sleepsense.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import au.com.ahbeard.sleepsense.R;
import butterknife.ButterKnife;

public class SleepTrackingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracking);
        ButterKnife.bind(this);
    }
}
