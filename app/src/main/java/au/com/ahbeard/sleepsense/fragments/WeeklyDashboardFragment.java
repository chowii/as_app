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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.utils.StringUtils;
import au.com.ahbeard.sleepsense.widgets.LabelThingy;
import au.com.ahbeard.sleepsense.widgets.WeeklyGraphView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeeklyDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeeklyDashboardFragment extends Fragment {

    ThreadLocal<SimpleDateFormat> monthOfYear = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return  new SimpleDateFormat("MMM ");
        }
    };

    @Bind(R.id.dashboard_view_pager_graph)
    ViewPager graphViewPager;

    @Bind(R.id.weekly_dashboard_layout_statistics)
    LinearLayout mStatisticsLayout;

    @Bind(R.id.dashboard_labels_graph)
    LabelThingy mDashboardLabels;

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
                calendar.add(Calendar.DAY_OF_MONTH, (position - 1023)*7 -1);

                int startSleepId = SleepService.getSleepId(calendar);

                calendar.add(Calendar.DAY_OF_YEAR, mWidth + 1);

                int endSleepId = SleepService.getSleepId(calendar);

                String[] labels = SleepService.generateLabels(startSleepId, endSleepId,
                        new SimpleDateFormat("EEE", Locale.getDefault()));

                List<Integer> sleepIdList = SleepService.generateSleepIdRange(startSleepId,endSleepId);

                int[] sleepIds = new int[sleepIdList.size()];

                for ( int i=0;i< sleepIdList.size();i++) {
                    sleepIds[i]=sleepIdList.get(i);
                }

                // This needs to properly use the graph fragment.
                WeeklyGraphFragment weeklyGraphFragment = WeeklyGraphFragment.newInstance(sleepIds,SleepService.instance().readSleepScores(startSleepId, endSleepId), labels);
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

            private Map<Integer,String> mLabels = new HashMap<>();

            @Override
            public String getLabel(int position) {

                if (mLabels.containsKey(position)) {
                    return mLabels.get(position);
                } else {
                    Calendar calendar = Calendar.getInstance();

                    // Find the start of the week and subtract one day.
                    calendar.set(Calendar.DAY_OF_WEEK, 2);

                    // Count back the number of pages.
                    calendar.add(Calendar.DAY_OF_MONTH, (position - 1023)*7 -1);

                    int startDay = calendar.get(Calendar.DAY_OF_MONTH);
                    String startMonth = monthOfYear.get().format(calendar.getTime());

                    calendar.add(Calendar.DAY_OF_YEAR, 6);

                    int endDay = calendar.get(Calendar.DAY_OF_MONTH);
                    String endMonth = monthOfYear.get().format(calendar.getTime());

                    String label;

                    if (startMonth.equals(endMonth)) {
                        label = String.format("%s %d - %d",startMonth,startDay,endDay);
                    } else {
                        label = String.format("%s %d - %s %d",startMonth,startDay,endMonth,endDay);
                    }

                    mLabels.put(position,label);

                    return label;
                }
            }
        });

        graphViewPager.addOnPageChangeListener(mDashboardLabels);
        graphViewPager.setCurrentItem(1023);

        setupStatistics();

        return view;
    }

    @Override
    public void onDestroyView() {

        ButterKnife.unbind(this);

        super.onDestroyView();

    }

    private void setupStatistics() {

        addStatistic(Color.GREEN,"Average SleepScore", "85");
        addStatistic(Color.GREEN,"Optimal Bedtime", "9:30 pm");
        addStatistic(Color.GREEN,"Optimal Mattress Firmness", "Medium Plush");
        addStatistic(Color.GREEN,"Best Night", "24 Dec 2015");
        addStatistic(Color.GREEN,"Worst Night", "24 Dec 2015");

    }

    private void addStatistic(int color, String name, String value) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_statistic,mStatisticsLayout,false);

        StatisticViewHolder viewHolder = new StatisticViewHolder();

        ButterKnife.bind(viewHolder,view);

        viewHolder.nameTextView.setText(name);
        viewHolder.valueTextView.setText(value);

        mStatisticsLayout.addView(view);
    }

    public class StatisticViewHolder {
        @Bind(R.id.statistic_image_view)
        ImageView imageView;
        @Bind(R.id.statistic_text_view_name)
        TextView nameTextView;
        @Bind(R.id.statistic_text_view_value)
        TextView valueTextView;
        
    }
}
