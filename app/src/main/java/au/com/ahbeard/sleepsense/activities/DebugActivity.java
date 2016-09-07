package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.DebugEmailService;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.services.log.SSLog;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class DebugActivity extends AppCompatActivity {

    @OnClick(R.id.debug_button_generate_fake_data)
    void generateFakeData() {
        Schedulers.computation().createWorker().schedule(new Action0() {
            @Override
            public void call() {
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
        Intent emailIntent = DebugEmailService.getDebugEmailIntent(this);
        startActivity(emailIntent);
    }

}
