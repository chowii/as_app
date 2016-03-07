package au.com.ahbeard.sleepsense.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.PumpTestFragment;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getSupportFragmentManager().beginTransaction().add(R.id.test_layout_fragment, PumpTestFragment.newInstance()).commit();
    }
}
