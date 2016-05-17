package au.com.ahbeard.sleepsense.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.model.beddit.Sleep;
import au.com.ahbeard.sleepsense.model.beddit.SleepStage;
import au.com.ahbeard.sleepsense.model.beddit.TimestampAndFloat;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.utils.StringUtils;
import au.com.ahbeard.sleepsense.widgets.SleepScoreView;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

public class SleepScoreBreakdownActivity extends AppCompatActivity {

    @Bind(R.id.sleep_score_view)
    SleepScoreView mSleepScoreView;

    @Bind(R.id.daily_graph_text_view_sleep_score)
    TextView mSleepScoreTextView;

    @Bind(R.id.daily_graph_layout_sleep_recorded)
    View mSleepRecordedView;

    @Bind(R.id.daily_graph_layout_no_sleep_recorded)
    View mNoSleepRecordedView;

    @Bind(R.id.daily_dashboard_layout_statistics)
    LinearLayout mStatisticsLayout;

    private long mSleepId;
    private Sleep mSleep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sleep_score_breakdown);

        mSleepId = getIntent().getExtras().getInt("sleep_id");

        ButterKnife.bind(this);

        mSleep = SleepService.instance().getSleepFromDatabase(mSleepId);

        if (mSleep == null) {

            mSleepRecordedView.setVisibility(View.GONE);
            mNoSleepRecordedView.setVisibility(View.VISIBLE);

        } else {

            mSleepRecordedView.setVisibility(View.VISIBLE);
            mNoSleepRecordedView.setVisibility(View.GONE);
            mSleepScoreView.setSleepScore(mSleep.getTotalSleepScore());
            mSleepScoreTextView.setText(Integer.toString(Math.round(mSleep.getTotalSleepScore())));

            addStatistic(Color.WHITE,"Time sleeping",StringUtils.timeInSecondsSinceEpochToString(mSleep.getSleepTotalTime()));

            if ( mSleep.getRestingHeartRate() !=null) {
                addStatistic(Color.WHITE,"Resting heart rate",""+mSleep.getRestingHeartRate().intValue()+" bpm");
            }
            addStatistic(Color.WHITE,"Time to fall asleep",StringUtils.timeInSecondsSinceEpochToString(mSleep.getSleepLatency()));
            addStatistic(Color.WHITE,"Sleep efficiency",String.format("%d%%",Math.round(mSleep.getSleepEfficiency()*100)));
            addStatistic(Color.WHITE,"Restless sleep", StringUtils.timeInSecondsSinceEpochToString(mSleep.getRestlessTotalTime()));
            addStatistic(Color.WHITE,"Awake", StringUtils.timeInSecondsSinceEpochToString(mSleep.getWakeTotalTime()));
            addStatistic(Color.WHITE,"Out of bed", StringUtils.timeInSecondsSinceEpochToString(mSleep.getAwayTotalTime()));
            addStatistic(Color.WHITE,"Snoring", StringUtils.timeInSecondsSinceEpochToString(mSleep.getTotalSnoringEpisodeDuration()));


        }

    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private StatisticViewHolder addStatistic(int color, String name, String value) {

        View view = LayoutInflater.from(this).inflate(R.layout.item_statistic, mStatisticsLayout, false);

        StatisticViewHolder viewHolder = new StatisticViewHolder();

        ButterKnife.bind(viewHolder, view);

        viewHolder.nameTextView.setText(name);
        viewHolder.valueTextView.setText(value);

        mStatisticsLayout.addView(view);

        return viewHolder;
    }

    public class StatisticViewHolder {
        @Bind(R.id.statistic_image_view)
        public ImageView imageView;
        @Bind(R.id.statistic_text_view_name)
        public TextView nameTextView;
        @Bind(R.id.statistic_text_view_value)
        public TextView valueTextView;

    }

}
