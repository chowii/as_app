package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingFragment;
import au.com.ahbeard.sleepsense.utils.GlobalVars;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgeActivity extends BaseActivity {

    private OnBoardingFragment mCurrentFragment;
    private HashMap scrollValuesMap = new HashMap();
    ListView listView;
    String selectedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age);
        ButterKnife.bind(this);

        listView = (ListView) findViewById(R.id.age_list);
        //TODO: replace by actual values (ages)
        //{
        List<String> list = new ArrayList();
        for(int i = 1; i < 70; i++)
            list.add(String.valueOf(i));

        String[] values = list.toArray(new String[0]);
        values[0] = "";
        values[values.length-1] = "";

        //}

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.vertical_scroll_selector_text_layout, R.id.scrollview_text_id, values);


        listView.setAdapter(adapter);
        ScrollView scrollView = (ScrollView) this.findViewById(R.id.age_scroller);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                getViewByPosition(2, listView);
            }
        });
    }

    public static Intent getAgeActivity(Context context) {
        Intent intent = new Intent(context, AgeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public void getViewByPosition(int pos, ListView listView) {
//        View myView;
//        final int firstListItemPosition = listView.getFirstVisiblePosition();
//        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
//
//        if (pos < firstListItemPosition || pos > lastListItemPosition) {
//            myView = listView.getAdapter().getView(pos, null, listView);
//        } else {
//            final int childIndex = pos - firstListItemPosition;
//            myView = listView.getChildAt(childIndex);
//        }
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

    @OnClick(R.id.button_age_skip)
    void option1Clicked() {
        Intent intent = SleepTargetActivity.getSleepTargetActivity(this);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.button_age_continue)
    void option2Clicked() {
        //TODO: save "selectedValue"
        Intent intent = SleepTargetActivity.getSleepTargetActivity(this);
        startActivity(intent);
        finish();
    }
}
