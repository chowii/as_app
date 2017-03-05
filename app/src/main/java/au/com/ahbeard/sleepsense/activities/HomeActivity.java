package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.fragments.DashboardFragment;
import au.com.ahbeard.sleepsense.fragments.DashboardNoSleepsFragment;
import au.com.ahbeard.sleepsense.fragments.FirmnessControlFragment;
import au.com.ahbeard.sleepsense.fragments.MassageControlFragment;
import au.com.ahbeard.sleepsense.fragments.MoreFragment;
import au.com.ahbeard.sleepsense.fragments.PositionControlFragment;

import au.com.ahbeard.sleepsense.fragments.settings.SettingsBaseFragment;

import au.com.ahbeard.sleepsense.services.AnalyticsService;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.services.SleepService;
import au.com.ahbeard.sleepsense.widgets.SimpleTabStrip;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by neal on 3/03/2016.
 */
public class HomeActivity extends BaseActivity {

    public static final String EXTRA_SHOW_ON_BOARDING_COMPLETE_DIALOG = "showOnBoardingCompleteDialog";

    private HomeFragmentPagerAdapter mDashboardPagerAdapter;

    private CompositeDisposable mCompositeSubscription = new CompositeDisposable();

    @Bind(R.id.dashboard_view_pager)
    ViewPager mViewPager;

    @Bind(R.id.on_board_complete_dialog_layout)
    View mOnBoardingCompleteDialogLayout;
    private boolean mHasRecordedASleep;

    @OnClick(R.id.on_board_complete_dialog_button)
    void onClickBoardingComplete() {
        mOnBoardingCompleteDialogLayout.setVisibility(View.GONE);
    }

    @Bind(R.id.dashboard_simple_tab_strip)
    SimpleTabStrip mSimpleTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        if ( getIntent().getBooleanExtra(EXTRA_SHOW_ON_BOARDING_COMPLETE_DIALOG, false)) {
            mOnBoardingCompleteDialogLayout.setAlpha(0.0f);
            mOnBoardingCompleteDialogLayout.setVisibility(View.VISIBLE);
            mOnBoardingCompleteDialogLayout.animate().setStartDelay(1000).alpha(1.0f).start();
        }

        setupTabs();

        // So we keep all the tabs intact.
        mViewPager.setOffscreenPageLimit(5);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mCompositeSubscription.add(SleepService.instance().getChangeObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer sleepId) {
                if ( mHasRecordedASleep != PreferenceService.instance().getHasRecordedASleep()) {
                    setupTabs();
                }
            }
        }));

    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    private void setupTabs() {

        mDashboardPagerAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager());

        if (SleepSenseDeviceService.instance().hasTrackerDevice() ) {
            mHasRecordedASleep = PreferenceService.instance().getHasRecordedASleep();
            if (mHasRecordedASleep) {
                mDashboardPagerAdapter.addTab("Dashboard", R.drawable.tab_dashboard_unselected, R.drawable.tab_dashboard_selected, DashboardFragment.newInstance(), new Runnable() {
                    @Override
                    public void run() {
                        AnalyticsService.instance().logDashboardViewTracking();
                    }
                });
            } else {
                mDashboardPagerAdapter.addTab("Dashboard",R.drawable.tab_dashboard_unselected,R.drawable.tab_dashboard_selected, DashboardNoSleepsFragment.newInstance(), new Runnable() {
                    @Override
                    public void run() {
                        AnalyticsService.instance().logDashboardViewTracking();
                    }
                });
            }
        }

        if (SleepSenseDeviceService.instance().hasPumpDevice() ) {
            mDashboardPagerAdapter.addTab("Firmness",R.drawable.tab_firmness_unselected,R.drawable.tab_firmness_selected, FirmnessControlFragment.newInstance(), new Runnable() {
                @Override
                public void run() {
                    AnalyticsService.instance().logDashboardViewMattress();
                }
            });
        }

        if (SleepSenseDeviceService.instance().hasBaseDevice() ) {
            mDashboardPagerAdapter.addTab("Position",R.drawable.tab_position_unselected,R.drawable.tab_position_selected, PositionControlFragment.newInstance(), new Runnable() {
                @Override
                public void run() {
                    AnalyticsService.instance().logDashboardViewPosition();
                }
            });
            mDashboardPagerAdapter.addTab("Massage",R.drawable.tab_massage_unselected,R.drawable.tab_massage_selected, MassageControlFragment.newInstance(), new Runnable() {
                @Override
                public void run() {
                    AnalyticsService.instance().logDashboardViewMassage();
                }
            });
        }

//        mDashboardPagerAdapter.addTab("More",R.drawable.tab_more_unselected,R.drawable.tab_more_selected, MoreFragment.newInstance(), new Runnable() {
//            @Override
//            public void run() {
//                AnalyticsService.instance().logDashboardViewSettings();
//            }
//        });

        /***
         * chowii block
         */
        mDashboardPagerAdapter.addTab("Settings",R.drawable.tab_more_unselected,R.drawable.tab_more_selected, new SettingsBaseFragment(), new Runnable() {
            @Override
            public void run() {
                AnalyticsService.instance().logDashboardViewSettings();
            }
        });

        mViewPager.setAdapter(mDashboardPagerAdapter);

        mSimpleTabStrip.setViewPager(mViewPager);


    }


    class HomeFragmentPagerAdapter extends FragmentStatePagerAdapter implements SimpleTabStrip.TabProvider {

        private List<String> mTabNames = new ArrayList<>();
        private List<Integer> mTabIconResourceIds = new ArrayList<>();
        private List<Integer> mSelectedTabIconResourceIds = new ArrayList<>();
        private List<Fragment> mFragments = new ArrayList<>();
        private List<Runnable> mOnClickActions = new ArrayList<>();

        public HomeFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addTab(String name, int iconResource, int selectedIconResource, Fragment fragment, Runnable onClickAction ) {
            mTabNames.add(name);
            mTabIconResourceIds.add(iconResource);
            mSelectedTabIconResourceIds.add(selectedIconResource);
            mFragments.add(fragment);
            mOnClickActions.add(onClickAction);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public String getName(int position) {
            return mTabNames.get(position);
        }

        @Override
        public int getIconResourceId(int position) {
            return mTabIconResourceIds.get(position);
        }

        public Runnable getOnClickAction(int position) {
            return mOnClickActions.get(position);
        }

        @Override
        public int getSelectedIconResourceId(int position) {
            return mSelectedTabIconResourceIds.get(position);
        }
    }

    public void doOnboarding() {
        Intent intent= new Intent(this,NewOnBoardActivity.class);
        startActivity(intent);
        finish();
    }

}
