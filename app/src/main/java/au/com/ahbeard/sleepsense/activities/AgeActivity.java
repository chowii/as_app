package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.SharedPreferencesStore;
import au.com.ahbeard.sleepsense.utils.GlobalVars;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgeActivity extends BaseActivity {

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
        //add two empty items in the beginning and one in the end for space display in scroller
        list.add("");list.add("");
        for(int i = 5; i < 70; i++)
            list.add(String.valueOf(i));
        list.add("");

        String[] values = list.toArray(new String[0]);

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
        for(int i = 0; i <= listView.getChildCount(); i++) {
            TextView textView = (TextView)listView.getChildAt(i);
            if(i == pos) {
                if(textView != null) {
                    textView.setTextSize(30);
                    textView.setTextColor(ContextCompat.getColor(this, R.color.scroll_view_highlight));
                    selectedValue = textView.getText().toString();
                }
            }
            else {
                if(textView != null) {
                    textView.setTextSize(20);
                    textView.setTextColor(ContextCompat.getColor(this, R.color.scroll_view_blur));
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
        SharedPreferencesStore.PutItem(GlobalVars.SHARED_PREFERENCE_USER_HEIGHT,
                "20", getApplicationContext());
        Intent intent = SleepTargetActivity.getSleepTargetActivity(this);
        startActivity(intent);
        finish();
    }
}
