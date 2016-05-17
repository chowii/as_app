package au.com.ahbeard.sleepsense.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.model.beddit.Sleep;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.utils.StringUtils;
import au.com.ahbeard.sleepsense.widgets.SleepScoreView;
import butterknife.Bind;
import butterknife.ButterKnife;

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

            int textColor = getResources().getColor(R.color.detailedSleepScoreTextColor);
            int altTextColor = getResources().getColor(R.color.detailedSleepScoreAltTextColor);

            addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_time_sleeping), StringUtils.timeInSecondsSinceEpochToString(textColor,altTextColor,mSleep.getSleepTotalTime()));

            if (mSleep.getRestingHeartRate() != null) {
                addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_resting_heart_rate), StringUtils.valueSuffix(textColor,altTextColor,"" + mSleep.getRestingHeartRate().intValue()," bpm"));
            }

            addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_time_to_fall_asleep),
                    StringUtils.timeInSecondsSinceEpochToString(textColor,altTextColor,mSleep.getSleepLatency()));
            addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_sleep_efficiency),
                    StringUtils.valueSuffix(textColor,altTextColor,String.format("%d", Math.round(mSleep.getSleepEfficiency() * 100))," %"),
                    getText(R.string.lorem_ipsum));
            addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_restless_sleep),
                    StringUtils.timeInSecondsSinceEpochToString(textColor,altTextColor,mSleep.getRestlessTotalTime()));
            addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_awake),
                    StringUtils.timeInSecondsSinceEpochToString(textColor,altTextColor,mSleep.getWakeTotalTime()));
            addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_out_of_bed),
                    StringUtils.timeInSecondsSinceEpochToString(textColor,altTextColor,mSleep.getAwayTotalTime()));
            addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_snoring),
                    StringUtils.timeInSecondsSinceEpochToString(textColor,altTextColor,mSleep.getTotalSnoringEpisodeDuration()));


        }

    }

    /**
     *
     */
    @Override
    protected void onStart() {

        super.onStart();

    }

    /**
     *
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     *
     * @param color
     * @param name
     * @param value
     * @return
     */
    private StatisticViewHolder addStatistic(int color, CharSequence name, CharSequence value ) {
        return addStatistic(color,name,value,null);
    }

    /**
     *
     * @param color
     * @param name
     * @param value
     * @param expandedText
     *
     * @return
     */
    private StatisticViewHolder addStatistic(int color, CharSequence name, CharSequence value, CharSequence expandedText ) {

        View view = LayoutInflater.from(this).inflate(R.layout.item_statistic, mStatisticsLayout, false);

        final StatisticViewHolder viewHolder = new StatisticViewHolder();

        ButterKnife.bind(viewHolder, view);

        viewHolder.nameTextView.setText(name);
        viewHolder.valueTextView.setText(value);

        if ( expandedText != null ) {
            viewHolder.expandCollapseImageView.setVisibility(View.VISIBLE);
            viewHolder.expandedTextView.setText(expandedText);
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.expandedLayout.getVisibility()==View.VISIBLE) {
                        viewHolder.expandedLayout.setVisibility(View.GONE);
                    } else {
                        viewHolder.expandedLayout.setVisibility(View.VISIBLE);
                    }
                }
            });

        }


        mStatisticsLayout.addView(view);

        return viewHolder;
    }

    /**
     *
     */
    public class StatisticViewHolder {

        @Bind(R.id.statistic_layout)
        public View layout;
        @Bind(R.id.statistic_image_view)
        public ImageView imageView;
        @Bind(R.id.statistic_text_view_name)
        public TextView nameTextView;
        @Bind(R.id.statistic_text_view_value)
        public TextView valueTextView;
        @Bind(R.id.statistic_image_view_expand_collapse)
        public ImageView expandCollapseImageView;
        @Bind(R.id.statistic_layout_expanded)
        public View expandedLayout;
        @Bind(R.id.statistic_text_view_expanded_text)
        public TextView expandedTextView;

    }

}
