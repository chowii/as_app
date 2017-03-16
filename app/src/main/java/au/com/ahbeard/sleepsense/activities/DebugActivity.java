package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Calendar;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.DebugEmailService;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.services.log.SSLog;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.schedulers.Schedulers;

public class DebugActivity extends AppCompatActivity {

    @OnClick(R.id.debug_button_generate_fake_data)
    void generateFakeData() {
        Schedulers.computation().createWorker().schedule(new Runnable() {
            @Override
            public void run() {
                SleepService.instance().generateFakeData(Calendar.getInstance(),60);
            }
        });
    }

    @OnClick(R.id.debug_button_re_run_batch_analysis)
    void runBatchAnalysis() {
        SleepService.instance().runBatchAnalysis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.debug_button_email_sleep_data)
    void uploadSleepData() {
        SSLog.d("Uploading sleepsense data");
        DebugEmailService.getDebugEmailIntent(this, new DebugEmailService.OnIntentCreatedCallback() {
            @Override
            public void onIntentCreated(Intent intent) {
                if (intent != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getParent() ,"Failed to upload sleep data. Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
