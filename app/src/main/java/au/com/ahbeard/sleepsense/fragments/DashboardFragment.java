package au.com.ahbeard.sleepsense.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.BaseActivity;
import au.com.ahbeard.sleepsense.activities.HelpActivity;
import au.com.ahbeard.sleepsense.activities.HomeActivity;
import au.com.ahbeard.sleepsense.activities.NewOnBoardActivity;
import au.com.ahbeard.sleepsense.activities.PreferenceActivity;
import au.com.ahbeard.sleepsense.activities.SleepTrackingActivity;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import au.com.ahbeard.sleepsense.services.SleepService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    @Bind(R.id.dashboard_tab_host)
    FragmentTabHost mTabHost;

    @Bind(R.id.dashboard_layout_tab_container)
    View tabContainerLayout;

    @Bind(R.id.dashboard_layout_circular_reveal)
    View mCircularRevealLayout;

    @OnClick(R.id.dashboard_fab_start_sleep)
    void onStartSleepClicked() {

        AnalyticsService.instance().logEvent(AnalyticsService.EVENT_DASHBOARD_TOUCH_TRACK_SLEEP);

        Intent intent = new Intent(getActivity(), SleepTrackingActivity.class);
        startActivity(intent);
    }

    @Bind(R.id.dashboard_fab_start_sleep)
    FloatingActionButton mStartSleepFAB;

    @OnClick(R.id.dashboard_image_view_help)
    void onHelpClicked() {
        if ( getActivity() instanceof HomeActivity) {
            startActivity(HelpActivity.getIntent(getActivity(), "Dashboard Help", "http://share.mentallyfriendly.com/sleepsense/#!/faq"));
        } else if (getActivity() instanceof BaseActivity) {
            ((BaseActivity)getActivity()).startOnBoardActivity();
        }
    }


    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

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

        if ( ! (getActivity() instanceof HomeActivity) ) {
            mStartSleepFAB.setVisibility(View.INVISIBLE);
        }

        mTabHost.setup(getContext(), getChildFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("DAILY").setIndicator(getTabIndicator("Daily")), DailyDashboardFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("WEEKLY").setIndicator(getTabIndicator("Weekly")), WeeklyDashboardFragment.class, null);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if ("DAILY".equalsIgnoreCase(tabId)) {
                    AnalyticsService.instance().logEvent(AnalyticsService.EVENT_DASHBOARD_VIEW_DAILY_STATS,
                            AnalyticsService.PROPERTY_ORIGIN, AnalyticsService.VALUE_ORIGIN_TOUCH);
                } else if ("WEEKLY".equalsIgnoreCase(tabId)) {
                    AnalyticsService.instance().logEvent(AnalyticsService.EVENT_DASHBOARD_VIEW_WEEKLY_STATS,
                            AnalyticsService.PROPERTY_ORIGIN, AnalyticsService.VALUE_ORIGIN_TOUCH);
                }
            }
        });

        mCompositeSubscription.add(SleepService.instance().getSleepIdSelectedObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer sleepId) {
                        Log.d("DASHBOARDFRAGMENT", "sleepId selected called..." + sleepId);

                        mTabHost.setCurrentTab(0);
                    }
                }));

        return view;
    }

    public View getTabIndicator(String title) {
        View tabIndicator = LayoutInflater.from(getContext()).inflate(R.layout.item_dashboard_tab, mTabHost, false);
        ((TextView) tabIndicator.findViewById(R.id.dashboard_tab_text_view_title)).setText(title);
        return tabIndicator;
    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.clear();
        ButterKnife.unbind(this);

        super.onDestroyView();

    }

    public void onScroll(int scrollY) {
        tabContainerLayout.setTranslationY(-scrollY / 2);
        mStartSleepFAB.setTranslationY(scrollY / 2);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void circularReveal() {

// get the center for the clipping circle
        float cx = mStartSleepFAB.getX() + mStartSleepFAB.getWidth() / 2;
        float cy = mStartSleepFAB.getY() + mStartSleepFAB.getHeight() / 2;

// get the initial radius for the clipping circle
        float finalRadius = (float) Math.hypot(mCircularRevealLayout.getWidth(), mCircularRevealLayout.getHeight());

// create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(mCircularRevealLayout, (int) cx, (int) cy, 0, finalRadius);

// make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mCircularRevealLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Intent intent = new Intent(getActivity(), SleepTrackingActivity.class);
                startActivity(intent);
            }
        });


// start the animation
        anim.start();

    }

}
