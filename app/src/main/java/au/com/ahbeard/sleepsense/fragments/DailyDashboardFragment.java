package au.com.ahbeard.sleepsense.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.model.beddit.Sleep;
import au.com.ahbeard.sleepsense.model.beddit.SleepStage;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.utils.StringUtils;
import au.com.ahbeard.sleepsense.widgets.LabelThingy;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyDashboardFragment extends Fragment {

    @Bind(R.id.dashboard_view_pager_graph)
    ViewPager mGraphViewPager;

    @Bind(R.id.dashboard_labels_graph)
    LabelThingy mGraphLabels;

    @Bind(R.id.daily_dashboard_layout_statistics)
    LinearLayout mStatisticsLayout;

    ThreadLocal<SimpleDateFormat> dayOfWeek = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("cccc ");
        }
    };

    ThreadLocal<SimpleDateFormat> monthOfYear = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(" MMMM");
        }
    };

    private Sleep mSleep;

    private StatisticViewHolder mTotalTimeSlept;
    private StatisticViewHolder mTimesOutOfBed;
    private StatisticViewHolder mMattressFirmness;


    public DailyDashboardFragment() {
        // Required empty public constructor
    }

    public static DailyDashboardFragment newInstance() {
        DailyDashboardFragment fragment = new DailyDashboardFragment();
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
        View view = inflater.inflate(R.layout.fragment_daily_dashboard, container, false);

        ButterKnife.bind(this, view);

        setupStatistics();


        mGraphViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            private int mNumberOfPagesBack = 1024;

            @Override
            public Fragment getItem(int position) {

                Calendar calendar = Calendar.getInstance();

                // Count back the number of pages.
                calendar.add(Calendar.DAY_OF_YEAR, position - 1023);

                return DailyGraphFragment.newInstance(SleepService.getSleepId(calendar));

            }

            @Override
            public int getCount() {
                return mNumberOfPagesBack;
            }

        });

        mGraphLabels.setLabelProvider(new LabelThingy.LabelProvider() {

            private Map<Integer, String> mLabels = new HashMap<>();

            @Override
            public String getLabel(int position) {

                if (mLabels.containsKey(position)) {
                    return mLabels.get(position);
                } else {
                    Calendar calendar = Calendar.getInstance();

                    calendar.add(Calendar.DAY_OF_YEAR, position - 1023);

                    String label = dayOfWeek.get().format(calendar.getTime()) +
                            StringUtils.ordinal(calendar.get(Calendar.DAY_OF_MONTH)) +
                            monthOfYear.get().format(calendar.getTime());

                    mLabels.put(position, label);


                    return label;
                }
            }
        });


        mGraphViewPager.addOnPageChangeListener(mGraphLabels);

        mGraphViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                final Calendar calendar = Calendar.getInstance();

                // Count back the number of pages.
                calendar.add(Calendar.DAY_OF_YEAR, position - 1023);

                Schedulers.io().createWorker().schedule(new Action0() {
                    @Override
                    public void call() {
                        final Sleep sleep = SleepService.instance().getSleepFromDatabase(SleepService.getSleepId(calendar));

                        AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                            @Override
                            public void call() {

                                if (sleep != null) {
                                    if (sleep.getSleepTotalTime() != null) {
                                        int sleepHours = (int)(sleep.getSleepTotalTime()/(60*60));
                                        int sleepMinutes = (int)(sleep.getSleepTotalTime()/(60))%60;
                                        mTotalTimeSlept.valueTextView.setText(sleepHours+"h " + sleepMinutes+" min");
                                    } else {
                                        mTotalTimeSlept.valueTextView.setText("");
                                    }

                                    if (sleep.getSleepStages()!=null) {
                                        int numberOfTimesOutOfBed = 0;
                                        for ( SleepStage sleepStage: sleep.getSleepStages() ) {
                                            if ( sleepStage.getStage() == SleepStage.Stage.Away ) {
                                                numberOfTimesOutOfBed+=1;
                                            }
                                        }

                                        mTimesOutOfBed.valueTextView.setText(Integer.toString(numberOfTimesOutOfBed));
                                    } else {
                                        mTimesOutOfBed.valueTextView.setText("");
                                    }

                                }

                            }
                        });

                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mGraphViewPager.setCurrentItem(1023);

        return view;
    }

    @Override
    public void onDestroyView() {

        ButterKnife.unbind(this);

        mTimesOutOfBed = null;
        mMattressFirmness = null;
        mTotalTimeSlept = null;

        super.onDestroyView();

    }

    private void setupStatistics() {

        mTotalTimeSlept = addStatistic(Color.GREEN, "Total Time Slept", "");
        mTimesOutOfBed = addStatistic(Color.GREEN, "Times out of Bed", "");
        mMattressFirmness = addStatistic(Color.GREEN, "Mattress Firmness", "");

    }

    private StatisticViewHolder addStatistic(int color, String name, String value) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_statistic, mStatisticsLayout, false);

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
