package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.onboarding.ImageQuestionnaireFragment;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingFragment;
import au.com.ahbeard.sleepsense.utils.GlobalVars;

public class SleepScenarioActivity extends BaseActivity
        implements ImageQuestionnaireFragment.OnActionListener{

    private OnBoardingFragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_scenario);
        ImageQuestionnaireFragment imageQuestionnaireFragment = ImageQuestionnaireFragment.newInstance(
                GlobalVars.SLEEP_SCENARIO_HEADER,
                GlobalVars.SLEEP_SCENARIO_ALONE,
                GlobalVars.SLEEP_SCENARIO_WITH_PARTNER);
        mCurrentFragment = imageQuestionnaireFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_sleep_scenario, imageQuestionnaireFragment).commit();
    }

    @Override
    public void onSelectionClicked(GlobalVars.ButtonActioned buttonActioned) {
        if(buttonActioned == GlobalVars.ButtonActioned.SKIP) {
//            Intent intent = TrackerInfoActivity.getTrackerInfoActivity(this);
//            startActivity(intent);
//            finish();
        }
        else if(buttonActioned == GlobalVars.ButtonActioned.CONTINUE) {
            //TODO: save selection before continue
//            Intent intent = TrackerInfoActivity.getTrackerInfoActivity(this);
//            startActivity(intent);
//            finish();
        }
    }

    public static Intent getSleepScenarioActivity(Context context) {
        Intent intent = new Intent(context, SleepScenarioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

}