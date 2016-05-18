package au.com.ahbeard.sleepsense.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.SleepService;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    @Bind(R.id.dashboard_tab_host)
    FragmentTabHost mTabHost;

    private Float[] mValues;
    private Calendar mCalendar;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ButterKnife.bind(this, view);

        mTabHost.setup(getContext(),getChildFragmentManager(),android.R.id.tabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("DAILY").setIndicator(getTabIndicator("Daily")),DailyDashboardFragment.class,null);
        mTabHost.addTab(mTabHost.newTabSpec("WEEKLY").setIndicator(getTabIndicator("Weekly")),WeeklyDashboardFragment.class,null);

        return view;
    }

    public View getTabIndicator(String title) {
        View tabIndicator = LayoutInflater.from(getContext()).inflate(R.layout.item_dashboard_tab,mTabHost,false);
        ((TextView)tabIndicator.findViewById(R.id.dashboard_tab_text_view_title)).setText(title);
        return tabIndicator;
    }

    @Override
    public void onDestroyView() {

        ButterKnife.unbind(this);

        super.onDestroyView();

    }


}
