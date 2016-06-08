package au.com.ahbeard.sleepsense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.SleepScoreBreakdownActivity;
import au.com.ahbeard.sleepsense.model.beddit.Sleep;
import au.com.ahbeard.sleepsense.model.beddit.TimestampAndFloat;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.widgets.DailyGraphView;
import au.com.ahbeard.sleepsense.widgets.SleepScoreView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by neal on 12/03/2016.
 */
public class DailyGraphFragment extends Fragment {

    @Bind(R.id.graph_view)
    DailyGraphView mGraphView;

    @Bind(R.id.sleep_score_view)
    SleepScoreView mSleepScoreView;
    private int mSleepId;

    @OnClick(R.id.sleep_score_view)
    void openSleepScoreBreakdown() {
        if ( mSleep != null ) {
            Intent intent = new Intent(getActivity(),SleepScoreBreakdownActivity.class);
            intent.putExtra("sleep_id",mSleepId);
            getActivity().startActivity(intent);
        }
//        SleepService.instance().generateFakeData(Calendar.getInstance(),7);
    }

    @Bind(R.id.daily_graph_text_view_sleep_score)
    TextView mSleepScoreTextView;

    @Bind(R.id.daily_graph_layout_sleep_recorded)
    View mSleepRecordedView;

    @Bind(R.id.daily_graph_layout_no_sleep_recorded)
    View mNoSleepRecordedView;

    private int mDaysBeforeToday;

    private Sleep mSleep;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDaysBeforeToday = getArguments().getInt("days_before_today");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_graph, container, false);

        ButterKnife.bind(this, view);

        updateData();

        mCompositeSubscription.add(SleepService.instance().getChangeObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer sleepId) {
                updateData();
            }
        }));

        return view;
    }

    private void updateData() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, mDaysBeforeToday);

        mSleepId = SleepService.getSleepId(calendar);
        mSleep = SleepService.instance().getSleepFromDatabase(mSleepId);

        if (mSleep == null) {

            mSleepRecordedView.setVisibility(View.GONE);
            mNoSleepRecordedView.setVisibility(View.VISIBLE);

        } else {

            mSleepRecordedView.setVisibility(View.VISIBLE);
            mNoSleepRecordedView.setVisibility(View.GONE);
            mSleepScoreView.setSleepScore(mSleep.getTotalSleepScore());
            mSleepScoreTextView.setText(Integer.toString(Math.round(mSleep.getTotalSleepScore())));

            List<TimestampAndFloat> sleepCycles = new ArrayList<>();
            sleepCycles.addAll(mSleep.getSleepCycles());

            mGraphView.setValues(sleepCycles, 0.0f, 4.0f);

        }
    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.clear();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    public static DailyGraphFragment newInstance(int daysBeforeToday) {
        DailyGraphFragment fragment = new DailyGraphFragment();
        Bundle args = new Bundle();
        args.putInt("days_before_today", daysBeforeToday);
        fragment.setArguments(args);
        return fragment;
    }


}
