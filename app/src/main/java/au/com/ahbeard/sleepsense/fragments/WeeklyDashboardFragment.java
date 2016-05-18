package au.com.ahbeard.sleepsense.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.model.AggregateStatistics;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.utils.StatisticsUtils;
import au.com.ahbeard.sleepsense.widgets.LabelThingy;
import au.com.ahbeard.sleepsense.widgets.WeeklyGraphView;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeeklyDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeeklyDashboardFragment extends Fragment {

    ThreadLocal<SimpleDateFormat> monthOfYear = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MMM ");
        }
    };

    @Bind(R.id.dashboard_view_pager_graph)
    ViewPager graphViewPager;

    @Bind(R.id.weekly_dashboard_layout_statistics)
    LinearLayout mStatisticsLayout;

    @Bind(R.id.dashboard_labels_graph)
    LabelThingy mDashboardLabels;

    CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    private StatisticsUtils.StatisticViewHolder mAverageSleepScore;
    private StatisticsUtils.StatisticViewHolder mOptimalBedtime;
    private StatisticsUtils.StatisticViewHolder mOptimalMattressFirmness;

    public WeeklyDashboardFragment() {
        // Required empty public constructor
    }

    public static WeeklyDashboardFragment newInstance() {
        WeeklyDashboardFragment fragment = new WeeklyDashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weekly_dashboard, container, false);

        ButterKnife.bind(this, view);

        graphViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            private int mWidth = 7;
            private int mNumberOfPagesBack = 1024;

            @Override
            public Fragment getItem(int position) {

                Calendar calendar = Calendar.getInstance();

                // Find the start of the week and subtract one day.
                calendar.set(Calendar.DAY_OF_WEEK, 1);

                // Count back the number of pages.
                calendar.add(Calendar.DAY_OF_MONTH, (position - 1023) * 7 - 1);

                int startSleepId = SleepService.getSleepId(calendar);

                calendar.add(Calendar.DAY_OF_YEAR, mWidth + 1);

                int endSleepId = SleepService.getSleepId(calendar);

                String[] labels = SleepService.generateLabels(startSleepId, endSleepId,
                        new SimpleDateFormat("EEE", Locale.getDefault()));

                List<Integer> sleepIdList = SleepService.generateSleepIdRange(startSleepId, endSleepId);

                int[] sleepIds = new int[sleepIdList.size()];

                for (int i = 0; i < sleepIdList.size(); i++) {
                    sleepIds[i] = sleepIdList.get(i);
                }

                // This needs to properly use the graph fragment.
                WeeklyGraphFragment weeklyGraphFragment = WeeklyGraphFragment.newInstance(sleepIds, SleepService.instance().readSleepScores(startSleepId, endSleepId), labels);
                weeklyGraphFragment.setOnClickListener(new WeeklyGraphView.OnClickListener() {
                    @Override
                    public void onValueClicked(Object identifier) {

                    }
                });
                return weeklyGraphFragment;
            }

            @Override
            public int getCount() {
                return mNumberOfPagesBack;
            }

        });

        mDashboardLabels.setLabelProvider(new LabelThingy.LabelProvider() {

            private Map<Integer, String> mLabels = new HashMap<>();

            @Override
            public String getLabel(int position) {

                if (mLabels.containsKey(position)) {
                    return mLabels.get(position);
                } else {
                    Calendar calendar = Calendar.getInstance();

                    // Find the start of the week and subtract one day.
                    calendar.set(Calendar.DAY_OF_WEEK, 2);

                    // Count back the number of pages.
                    calendar.add(Calendar.DAY_OF_MONTH, (position - 1023) * 7 - 1);

                    int startDay = calendar.get(Calendar.DAY_OF_MONTH);
                    String startMonth = monthOfYear.get().format(calendar.getTime());

                    calendar.add(Calendar.DAY_OF_YEAR, 6);

                    int endDay = calendar.get(Calendar.DAY_OF_MONTH);
                    String endMonth = monthOfYear.get().format(calendar.getTime());

                    String label;

                    if (startMonth.equals(endMonth)) {
                        label = String.format("%s %d - %d", startMonth, startDay, endDay);
                    } else {
                        label = String.format("%s %d - %s %d", startMonth, startDay, endMonth, endDay);
                    }

                    mLabels.put(position, label);

                    return label;
                }
            }
        });

        graphViewPager.addOnPageChangeListener(mDashboardLabels);
        graphViewPager.setCurrentItem(1023);

        StatisticsUtils statisticsUtils = new StatisticsUtils(mStatisticsLayout);

        mAverageSleepScore = statisticsUtils.addStatistic(Color.GREEN, "Average SleepScore", "85");
        mOptimalBedtime = statisticsUtils.addStatistic(Color.GREEN, "Optimal Bedtime", "9:30 pm");
        mOptimalMattressFirmness = statisticsUtils.addStatistic(Color.GREEN, "Optimal Mattress Firmness", "Medium Plush");
        statisticsUtils.addStatistic(Color.GREEN, "Best Night", "24 Dec 2015");
        statisticsUtils.addStatistic(Color.GREEN, "Worst Night", "24 Dec 2015");



        mCompositeSubscription.add(SleepService.instance()
                .getAggregateStatisticsObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AggregateStatistics>() {
                    @Override
                    public void call(AggregateStatistics aggregateStatistics) {


                    }
                }));

        setupStatistics();

        return view;
    }

    @Override
    public void onDestroyView() {

        ButterKnife.unbind(this);

        super.onDestroyView();

    }

    private void setupStatistics() {

        // Move this off the main thread.
        SleepService.instance().getAggregateStatistics();

        //mCompositeSubscription.s



    }

}
