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
import au.com.ahbeard.sleepsense.widgets.ScaleView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeightActivity extends AppCompatActivity {

    @Bind(R.id.txt_weight)
    TextView txtValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);
        ButterKnife.bind(this);
        final ScaleView rulerViewMm = (ScaleView) findViewById(R.id.weight_scale);
        rulerViewMm.setRotation(90);
        rulerViewMm.setStartingPoint(7);
        rulerViewMm.setRulerPointer(200);
        setTextValue((Math.round((7 * 10f))) + " kg");
        rulerViewMm.setUpdateListener(new ScaleView.onViewUpdateListener() {
            @Override
            public void onViewUpdate(float result) {
                setTextValue(Math.round((result * 10f)) + " kg");
            }
        });

    }

    public static Intent getWeightActivity(Context context) {
        Intent intent = new Intent(context, WeightActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private void setTextValue(String value) {
        SpannableString ss1=  new SpannableString(value);
        ss1.setSpan(new RelativeSizeSpan(2f), 0,value.indexOf(" "), 0); // set size
        txtValue.setText(ss1);
    }

    @OnClick(R.id.button_weight_skip)
    public void skipClicked(Button button) {
        Intent intent = GenderActivity.getGenderActivity(this);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.button_weight_continue)
    public void continueClicked(Button button) {
        //TODO: persist user height before below code
        SharedPreferencesStore.PutItem(GlobalVars.SHARED_PREFERENCE_USER_WEIGHT,
                "75", getApplicationContext());
        Intent intent = GenderActivity.getGenderActivity(this);
        startActivity(intent);
        finish();
    }
}
