package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.fragments.BaseTestFragment;
import au.com.ahbeard.sleepsense.fragments.DashboardFragment;
import au.com.ahbeard.sleepsense.fragments.DebugFragment;
import au.com.ahbeard.sleepsense.fragments.FirmnessControlFragment;
import au.com.ahbeard.sleepsense.fragments.MassageControlFragment;
import au.com.ahbeard.sleepsense.fragments.MoreFragment;
import au.com.ahbeard.sleepsense.fragments.PositionControlFragment;
import au.com.ahbeard.sleepsense.fragments.PumpTestFragment;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.widgets.SimpleTabStrip;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by neal on 3/03/2016.
 */
public class DashboardActivity extends BaseActivity {

    private final DashboardFragment mHomeFragment = DashboardFragment.newInstance("", "");
    private final FirmnessControlFragment mFirmnessControlFragment = FirmnessControlFragment.newInstance();
    private final PositionControlFragment mPositionControlFragment = PositionControlFragment.newInstance();
    private final MassageControlFragment mMassageControlFragment = MassageControlFragment.newInstance();
    private final MoreFragment mMoreFragment = MoreFragment.newInstance("", "");

    private final Fragment mDebugFragment = DebugFragment.newInstance();
    private final Fragment mBaseTestFragment = BaseTestFragment.newInstance();
    private final Fragment mPumpTestFragment = PumpTestFragment.newInstance();

    private HomeFragmentPagerAdapter mDashboardPagerAdapter;

    @OnClick(R.id.dashboard_fab_start_sleep)
    void onStartSleepClicked() {
        Intent intent = new Intent(this, SleepTrackingActivity.class);
        startActivity(intent);
    }

    @Bind(R.id.dashboard_fab_start_sleep)
    FloatingActionButton mStartSleepFloatingActionButton;

    @Bind(R.id.dashboard_view_pager)
    ViewPager mViewPager;

    @Bind(R.id.dashboard_simple_tab_strip)
    SimpleTabStrip mSimpleTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (PreferenceService.instance().requiresOnBoarding()) {
            Intent intent = new Intent(this, OnBoardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);


        setupTabs();

        mViewPager.setOffscreenPageLimit(5);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mStartSleepFloatingActionButton.setVisibility(mDashboardPagerAdapter.getItem(position) == mHomeFragment ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        SleepSenseDeviceService.instance().getEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SleepSenseDeviceService.SleepSenseDeviceServiceEvent>() {
            @Override
            public void call(SleepSenseDeviceService.SleepSenseDeviceServiceEvent event) {
                if ( event == SleepSenseDeviceService.SleepSenseDeviceServiceEvent.DeviceListChanged ) {
                    setupTabs();
                }
            }
        });

        mStartSleepFloatingActionButton.setVisibility(mDashboardPagerAdapter.getItem(0) == mHomeFragment ? View.VISIBLE : View.GONE);
    }

    private void setupTabs() {

        mDashboardPagerAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager());

        if (SleepSenseDeviceService.instance().hasTrackerDevice() ) {
            mDashboardPagerAdapter.addTab("Dashboard",R.drawable.tab_dashboard_unselected,R.drawable.tab_dashboard_selected, mHomeFragment);
        }

        if (SleepSenseDeviceService.instance().hasPumpDevice() ) {
            mDashboardPagerAdapter.addTab("Firmness",R.drawable.tab_firmness_unselected,R.drawable.tab_firmness_selected, mFirmnessControlFragment);
        }

        if (SleepSenseDeviceService.instance().hasBaseDevice() ) {
            mDashboardPagerAdapter.addTab("Position",R.drawable.tab_position_unselected,R.drawable.tab_position_selected, mPositionControlFragment);
            mDashboardPagerAdapter.addTab("Massage",R.drawable.tab_massage_unselected,R.drawable.tab_massage_selected, mMassageControlFragment);
        }

//        mDashboardPagerAdapter.addTab("More",R.drawable.tab_more_unselected,R.drawable.tab_more_selected, mMoreFragment);
        mDashboardPagerAdapter.addTab("Debug",R.drawable.debug_icon_normal,R.drawable.debug_icon_selected, mDebugFragment);
//        mDashboardPagerAdapter.addTab("Base",R.drawable.tab_more_unselected,R.drawable.tab_more_selected, mBaseTestFragment);
//        mDashboardPagerAdapter.addTab("Pump",R.drawable.tab_more_unselected,R.drawable.tab_more_selected, mPumpTestFragment);

        mViewPager.setAdapter(mDashboardPagerAdapter);

        mSimpleTabStrip.setViewPager(mViewPager);

    }

    class HomeFragmentPagerAdapter extends FragmentStatePagerAdapter implements SimpleTabStrip.TabProvider {

        private List<String> mTabNames = new ArrayList<>();
        private List<Integer> mTabIconResourceIds = new ArrayList<>();
        private List<Integer> mSelectedTabIconResourceIds = new ArrayList<>();
        private List<Fragment> mFragments = new ArrayList<>();

        public HomeFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addTab(String name, int iconResource, int selectedIconResource, Fragment fragment ) {
            mTabNames.add(name);
            mTabIconResourceIds.add(iconResource);
            mSelectedTabIconResourceIds.add(selectedIconResource);
            mFragments.add(fragment);
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

        @Override
        public int getSelectedIconResourceId(int position) {
            return mSelectedTabIconResourceIds.get(position);
        }
    }

    public void clearDevices() {
        // This is a hack until we make sure that the fragments can deal with null devices.
        setupTabs();
        SleepSenseDeviceService.instance().clearDevices();
        Intent intent= new Intent(this,DashboardActivity.class);
        startActivity(intent);
        finish();
    }

}
