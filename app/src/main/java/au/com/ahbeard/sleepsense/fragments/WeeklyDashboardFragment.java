package au.com.ahbeard.sleepsense.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.model.AggregateStatistics;
import au.com.ahbeard.sleepsense.model.Firmness;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.utils.StatisticsUtils;
import au.com.ahbeard.sleepsense.widgets.LabelThingy;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

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

    ThreadLocal<SimpleDateFormat> fancyDate = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("dd MMM yyyy");
        }
    };

    ThreadLocal<SimpleDateFormat> optimalBeditme = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("hh:mm a");
        }
    };

    @Bind(R.id.dashboard_view_pager_graph)
    ViewPager mGraphViewPager;

    @Bind(R.id.weekly_dashboard_layout_statistics)
    LinearLayout mStatisticsLayout;

    @Bind(R.id.dashboard_labels_graph)
    LabelThingy mDashboardLabels;

    @Bind(R.id.weekly_dashboard_scroll_view)
    ScrollView mScrollView;

    @Bind(R.id.dashboard_text_view_sleep_tip_text)
    TextView mSleepTipText;

    CompositeDisposable mCompositeSubscription = new CompositeDisposable();

    private StatisticsUtils.StatisticViewHolder mAverageSleepScore;
    private StatisticsUtils.StatisticViewHolder mOptimalBedtime;
    private StatisticsUtils.StatisticViewHolder mOptimalMattressFirmness;
    private StatisticsUtils.StatisticViewHolder mBestNight;
    private StatisticsUtils.StatisticViewHolder mWorstNight;

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

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(1024);

        mGraphViewPager.setAdapter(adapter);

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

        mGraphViewPager.addOnPageChangeListener(mDashboardLabels);
        mGraphViewPager.setCurrentItem(1024-1);

        StatisticsUtils statisticsUtils = new StatisticsUtils(mStatisticsLayout);

        mAverageSleepScore = statisticsUtils.addStatistic(
                StatisticsUtils.getCircleDrawable(getContext(), StatisticsUtils.StatisticCircleColor.GREEN),
                "Average Sleep Score", null);
        mOptimalBedtime = statisticsUtils.addStatistic(
                StatisticsUtils.getCircleDrawable(getContext(), StatisticsUtils.StatisticCircleColor.DARK_BLUE),
                "Optimal Bedtime", null);
        mOptimalMattressFirmness = statisticsUtils.addStatistic(
                StatisticsUtils.getCircleDrawable(getContext(), StatisticsUtils.StatisticCircleColor.LIGHT_BLUE),
                "Optimal Mattress Firmness", null);
        mBestNight =statisticsUtils.addStatistic(
                StatisticsUtils.getCircleDrawable(getContext(), StatisticsUtils.StatisticCircleColor.GREEN),
                "Best Night", null);
        mWorstNight = statisticsUtils.addStatistic(
                StatisticsUtils.getCircleDrawable(getContext(), StatisticsUtils.StatisticCircleColor.RED),
                "Worst Night", null);

        mCompositeSubscription.add(SleepService.instance().getChangeObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer sleepId) {
                mGraphViewPager.getAdapter().notifyDataSetChanged();
            }
        }));

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if ( mScrollView != null ) {
                    int scrollY = mScrollView.getScrollY();
                    if ( getParentFragment() != null ) {
                        if ( getParentFragment() instanceof DashboardFragment ) {
                            ((DashboardFragment)getParentFragment()).onScroll(scrollY);
                        }
                    }
                }
            }
        });

        String[] sleepTips = getResources().getStringArray(R.array.sleep_tips);
        mSleepTipText.setText(sleepTips[new Random().nextInt(sleepTips.length)]);

        mCompositeSubscription.add(SleepService.instance().getAggregateStatisticsObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<AggregateStatistics>() {
            @Override
            public void accept(final AggregateStatistics aggregateStatistics) {

                // Average sleep score
                mAverageSleepScore.valueTextView.setText(Integer.toString(aggregateStatistics.getAverageSleepScore().intValue()));

                // Best night
                if ( aggregateStatistics.getBestNight()!=null) {
                    mBestNight.valueTextView.setText(format(fancyDate.get(),aggregateStatistics.getBestNight()));
                    mBestNight.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AnalyticsService.instance().logDashboardViewDailyStats(AnalyticsService.VALUE_ORIGIN_BEST_NIGHT);
                            SleepService.instance().notifySleepIdSelected(SleepService.getSleepId(aggregateStatistics.getBestNight()));

                        }
                    });
                }

                // Worst night
                if ( aggregateStatistics.getWorstNight()!=null) {
                    mWorstNight.valueTextView.setText(format(fancyDate.get(),aggregateStatistics.getWorstNight()));
                    mWorstNight.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AnalyticsService.instance().logDashboardViewDailyStats(AnalyticsService.VALUE_ORIGIN_WORST_NIGHT);
                            SleepService.instance().notifySleepIdSelected(SleepService.getSleepId(aggregateStatistics.getWorstNight()));

                        }
                    });
                }

                // Optimal bedtime.
                if ( aggregateStatistics.getOptimalBedtime() != null ) {
                    mOptimalBedtime.valueTextView.setText(format(optimalBeditme.get(),aggregateStatistics.getOptimalBedtime()));
                }

                // Mattress firmness.
                if ( aggregateStatistics.getOptimalMattressFirmness() !=null ) {
                    mOptimalMattressFirmness.valueTextView.setText(Firmness.getFirmnessForPressure(aggregateStatistics.getOptimalMattressFirmness()).getLabel());
                }
            }
        }));

        return view;
    }

    @Override
    public void onDestroyView() {

        mCompositeSubscription.clear();

        ButterKnife.unbind(this);

        super.onDestroyView();

    }

    public String format(SimpleDateFormat simpleDateFormat, Calendar calendar) {
        if ( calendar == null ) {
            return null;
        } else {
            simpleDateFormat.setCalendar(calendar);
            return simpleDateFormat.format(calendar.getTime());
        }
    }


    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private int mNumberOfPagesBack;

        public MyFragmentPagerAdapter(int numberOfPagesBack) {
            super(getChildFragmentManager());
            mNumberOfPagesBack = numberOfPagesBack;
        }

        @Override
        public Fragment getItem(int position) {
            return WeeklyGraphFragment.newInstance(position - mNumberOfPagesBack + 1);
        }

        @Override
        public int getCount() {
            return mNumberOfPagesBack;
        }



    }
}
