package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.ui.onboarding.MainOnboardingActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (PreferenceService.instance().requiresOnBoarding()) {
//            goToOnboarding();
//        } else {
            goToDashboard();
//        }
//        goToDashboard();
    }

    private void goToDashboard() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void goToOnboarding() {
        Intent intent = new Intent(this, MainOnboardingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
