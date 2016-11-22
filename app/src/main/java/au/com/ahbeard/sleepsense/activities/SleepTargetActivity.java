package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingFragment;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SleepTargetActivity extends BaseActivity {

    private OnBoardingFragment mCurrentFragment;
    private HashMap scrollValuesMap = new HashMap();
    ListView listView;
    String selectedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_target);
        ButterKnife.bind(this);

        listView = (ListView) findViewById(R.id. sleep_target_list);
        //TODO: replace by actual values (ages)
        //{
        List<String> list = new ArrayList();
        //add two empty items in the beginning and one in the end for space display in scroller
        list.add("");list.add("");
        for(int i = 2; i < 12; i++)
            list.add(String.valueOf(i) + " hours");
        list.add("");

        String[] values = list.toArray(new String[0]);
        //}

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.vertical_scroll_selector_text_layout, R.id.scrollview_text_id, values);


        listView.setAdapter(adapter);
        ScrollView scrollView = (ScrollView) this.findViewById(R.id. sleep_target_scroller);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                getViewByPosition(2, listView);
            }
        });
    }

    public static Intent getSleepTargetActivity(Context context) {
        Intent intent = new Intent(context, SleepTargetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public void getViewByPosition(int pos, ListView listView) {
        for(int i = 0; i <= listView.getChildCount(); i++) {
            TextView textView = (TextView)listView.getChildAt(i);
            if(i == pos) {
                if(textView != null) {
                    textView.setTextSize(30);
                    textView.setTextColor(Color.parseColor("#FFFFFFFF"));
                    selectedValue = textView.getText().toString();
                }
            }
            else {
                if(textView != null) {
                    textView.setTextSize(20);
                    textView.setTextColor(Color.parseColor("#40FFFFFF"));
                }
            }
        }
    }

    @OnClick(R.id.button_sleep_target_skip)
    void option1Clicked() {
        Intent intent = SleepScenarioActivity.getSleepScenarioActivity(this);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.button_sleep_target_continue)
    void option2Clicked() {
        //TODO: save "selectedValue"
        Intent intent = SleepScenarioActivity.getSleepScenarioActivity(this);
        startActivity(intent);
        finish();
    }
}
