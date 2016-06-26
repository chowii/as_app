package au.com.ahbeard.sleepsense.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.model.beddit.Sleep;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.utils.StatisticsUtils;
import au.com.ahbeard.sleepsense.widgets.SleepScoreView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SleepScoreBreakdownFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SleepScoreBreakdownFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SLEEP_ID = "sleepId";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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



    public SleepScoreBreakdownFragment() {
        // Required empty public constructor
    }

    public static SleepScoreBreakdownFragment newInstance(long sleepId) {
        SleepScoreBreakdownFragment fragment = new SleepScoreBreakdownFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SLEEP_ID, sleepId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSleepId = getArguments().getLong(ARG_SLEEP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sleep_score_breakdown, container, false);

        ButterKnife.bind(this,view);

        mStatisticsUtils = new StatisticsUtils(mStatisticsLayout);

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

            mStatisticsUtils.addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_time_sleeping),
                    StatisticsUtils.timeInSecondsSinceEpochToString(textColor, altTextColor, mSleep.getSleepTotalTime()),
                    getText(R.string.sleep_score_breakdown_time_sleeping_txt));

            if (mSleep.getRestingHeartRate() != null) {
                mStatisticsUtils.addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_resting_heart_rate),
                        StatisticsUtils.valueSuffix(textColor, altTextColor, "" + mSleep.getRestingHeartRate().intValue(), " bpm"),getText(R.string.sleep_score_breakdown_resting_heart_rate_txt));
            }

            mStatisticsUtils.addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_time_to_fall_asleep),
                    StatisticsUtils.timeInSecondsSinceEpochToString(textColor, altTextColor, mSleep.getSleepLatency()),
                    getText(R.string.sleep_score_breakdown_time_to_fall_asleep_txt));
            mStatisticsUtils.addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_sleep_efficiency),
                    StatisticsUtils.valueSuffix(textColor, altTextColor, String.format("%d", Math.round(mSleep.getSleepEfficiency() * 100)), " %"),
                    getText(R.string.sleep_score_breakdown_sleep_efficiency_txt));
            mStatisticsUtils.addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_restless_sleep),
                    StatisticsUtils.timeInSecondsSinceEpochToString(textColor, altTextColor, mSleep.getRestlessTotalTime()),
                    getText(R.string.sleep_score_breakdown_restless_sleep_txt));
            mStatisticsUtils.addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_awake),
                    StatisticsUtils.timeInSecondsSinceEpochToString(textColor, altTextColor, mSleep.getWakeTotalTime()),
                    getText(R.string.sleep_score_breakdown_awake_txt));
            mStatisticsUtils.addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_out_of_bed),
                    StatisticsUtils.timeInSecondsSinceEpochToString(textColor, altTextColor, mSleep.getAwayTotalTime()),
                    getText(R.string.sleep_score_breakdown_out_of_bed_txt));

//            mStatisticsUtils.addStatistic(Color.WHITE, getString(R.string.sleep_score_breakdown_snoring),
//                    StatisticsUtils.timeInSecondsSinceEpochToString(textColor, altTextColor, mSleep.getTotalSnoringEpisodeDuration()),
//                    getText(R.string.sleep_score_breakdown_snoring_txt));


        }

        return view;

    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
