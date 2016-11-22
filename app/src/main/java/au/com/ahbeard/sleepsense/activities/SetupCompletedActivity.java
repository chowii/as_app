package au.com.ahbeard.sleepsense.activities;

import au.com.ahbeard.sleepsense.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SetupCompletedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_completed);
        ButterKnife.bind(this);
    }

    public static Intent getSetUpCompletedActivity(Context context) {
        Intent intent = new Intent(context, SetupCompletedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }


    @OnClick(R.id.button_setup_completed_continue)
    void continueButtonClicked() {
        Toast.makeText(this, "Set up is completed. Congratulations!", Toast.LENGTH_SHORT).show();
    }
}
