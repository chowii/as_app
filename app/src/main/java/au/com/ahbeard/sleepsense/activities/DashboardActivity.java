package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.os.Bundle;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.PreferenceService;

/**
 * Created by neal on 3/03/2016.
 */
public class DashboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (PreferenceService.instance().requiresOnBoarding()) {
            Intent intent = new Intent(this,OnBoardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        setContentView(R.layout.activity_dashboard);

    }

}
