package au.com.ahbeard.sleepsense.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.Conversion;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerUtils;
import au.com.ahbeard.sleepsense.model.beddit.Actigram;
import au.com.ahbeard.sleepsense.model.beddit.Sleep;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.utils.ConversionUtils;
import au.com.ahbeard.sleepsense.utils.StatisticsUtils;
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

    private StatisticsUtils mStatisticsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sleep_score_breakdown);

        mSleepId = getIntent().getExtras().getInt("sleep_id");

        ButterKnife.bind(this);

        mStatisticsUtils = new StatisticsUtils(mStatisticsLayout);

        mSleep = SleepService.instance().getSleepFromDatabase(mSleepId);

        for (Actigram actigram:mSleep.getActigrams()) {
            Log.d("ACTIGRAM",String.format("time: %f actigrams: %f",actigram.getTimestamp(),actigram.getActigram()));
        }



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

            mStatisticsUtils.addStatistic(null, getString(R.string.sleep_score_breakdown_time_sleeping),
                    StatisticsUtils.timeInSecondsSinceEpochToString(textColor, altTextColor, mSleep.getSleepTotalTime()),
                    getText(R.string.sleep_score_breakdown_time_sleeping_txt));

            if (mSleep.getRestingHeartRate() != null) {
                mStatisticsUtils.addStatistic(null, getString(R.string.sleep_score_breakdown_resting_heart_rate),
                        StatisticsUtils.valueSuffix(textColor, altTextColor, "" + mSleep.getRestingHeartRate().intValue(), " bpm"),getText(R.string.sleep_score_breakdown_resting_heart_rate_txt));
            }

            mStatisticsUtils.addStatistic(null, getString(R.string.sleep_score_breakdown_time_to_fall_asleep),
                    StatisticsUtils.timeInSecondsSinceEpochToString(textColor, altTextColor, mSleep.getSleepLatency()),
                    getText(R.string.sleep_score_breakdown_time_to_fall_asleep_txt));
            mStatisticsUtils.addStatistic(null, getString(R.string.sleep_score_breakdown_sleep_efficiency),
                    StatisticsUtils.valueSuffix(textColor, altTextColor, String.format("%d", Math.round(mSleep.getSleepEfficiency() * 100)), " %"),
                    getText(R.string.sleep_score_breakdown_sleep_efficiency_txt));
            mStatisticsUtils.addStatistic(null, getString(R.string.sleep_score_breakdown_restless_sleep),
                    StatisticsUtils.timeInSecondsSinceEpochToString(textColor, altTextColor, mSleep.getRestlessTotalTime()),
                    getText(R.string.sleep_score_breakdown_restless_sleep_txt));
            mStatisticsUtils.addStatistic(null, getString(R.string.sleep_score_breakdown_awake),
                    StatisticsUtils.timeInSecondsSinceEpochToString(textColor, altTextColor, mSleep.getWakeTotalTime()),
                    getText(R.string.sleep_score_breakdown_awake_txt));
            mStatisticsUtils.addStatistic(null, getString(R.string.sleep_score_breakdown_out_of_bed),
                    StatisticsUtils.timeInSecondsSinceEpochToString(textColor, altTextColor, mSleep.getAwayTotalTime()),
                    getText(R.string.sleep_score_breakdown_out_of_bed_txt));
//            mStatisticsUtils.addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_snoring),
//                    StatisticsUtils.timeInSecondsSinceEpochToString(textColor, altTextColor, mSleep.getTotalSnoringEpisodeDuration()),
//                    getText(R.string.sleep_score_breakdown_snoring_txt));


        }

    }


}
