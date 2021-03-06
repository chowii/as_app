package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.Button;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.SharedPreferencesStore;
import au.com.ahbeard.sleepsense.utils.GlobalVars;
import au.com.ahbeard.sleepsense.widgets.SSRulerView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HeightActivity extends AppCompatActivity {

    @Bind(R.id.txt_height)
    TextView txtValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);
        ButterKnife.bind(this);
        final SSRulerView rulerViewMm = (SSRulerView) findViewById(R.id.height_scale);
        rulerViewMm.setStartingPoint(17);
        rulerViewMm.setRulerPointer(250);
        setTextValue(( Math.round((17 * 10f))) + " cm");
        rulerViewMm.setUpdateListener(new SSRulerView.onViewUpdateListener() {
            @Override
            public void onViewUpdate(float result) {
                setTextValue(Math.round((result * 10f)) + " cm");
            }
        });
    }

    private void setTextValue(String value) {
        SpannableString ss1=  new SpannableString(value);
        ss1.setSpan(new RelativeSizeSpan(2f), 0,value.indexOf(" "), 0); // set size
        txtValue.setText(ss1);
    }

    public static Intent getHeightActivity(Context context) {
        Intent intent = new Intent(context, HeightActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @OnClick(R.id.button_height_skip)
    public void skipClicked(Button button) {
        Intent intent = WeightActivity.getWeightActivity(this);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.button_height_continue)
    public void continueClicked(Button button) {
        //TODO: persist user height before below code
        SharedPreferencesStore.PutItem(GlobalVars.SHARED_PREFERENCE_USER_HEIGHT,
                "175", getApplicationContext());
        Intent intent = WeightActivity.getWeightActivity(this);
        startActivity(intent);
        finish();
    }
}
