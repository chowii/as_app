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
import au.com.ahbeard.sleepsense.fragments.DashboardFragment;
import au.com.ahbeard.sleepsense.fragments.DashboardNoSleepsFragment;
import au.com.ahbeard.sleepsense.fragments.WeeklyDashboardFragment;
import au.com.ahbeard.sleepsense.fragments.DebugFragment;
import au.com.ahbeard.sleepsense.fragments.FirmnessControlFragment;
import au.com.ahbeard.sleepsense.fragments.MassageControlFragment;
import au.com.ahbeard.sleepsense.fragments.MoreFragment;
import au.com.ahbeard.sleepsense.fragments.PositionControlFragment;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.widgets.SimpleTabStrip;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by neal on 3/03/2016.
 */
public class HomeActivity extends BaseActivity {

    private final WeeklyDashboardFragment mHomeFragment = WeeklyDashboardFragment.newInstance();

    private final Fragment mDebugFragment = DebugFragment.newInstance();

    private HomeFragmentPagerAdapter mDashboardPagerAdapter;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Bind(R.id.dashboard_view_pager)
    ViewPager mViewPager;

    @Bind(R.id.dashboard_simple_tab_strip)
    SimpleTabStrip mSimpleTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (PreferenceService.instance().requiresOnBoarding()) {
            Intent intent = new Intent(this, NewOnBoardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        setupTabs();

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

//        SleepSenseDeviceService.instance().getEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SleepSenseDeviceService.SleepSenseDeviceServiceEvent>() {
//            @Override
//            public void call(SleepSenseDeviceService.SleepSenseDeviceServiceEvent event) {
//                if ( event == SleepSenseDeviceService.SleepSenseDeviceServiceEvent.DeviceListChanged ) {
//                    setupTabs();
//                }
//            }
//        });

    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    private void setupTabs() {

        mDashboardPagerAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager());

        if (SleepSenseDeviceService.instance().hasTrackerDevice() ) {
            if ( PreferenceService.instance().getHasRecordedASleep() ) {
                mDashboardPagerAdapter.addTab("Dashboard",R.drawable.tab_dashboard_unselected,R.drawable.tab_dashboard_selected, DashboardFragment.newInstance());
            } else {
                mDashboardPagerAdapter.addTab("Dashboard",R.drawable.tab_dashboard_unselected,R.drawable.tab_dashboard_selected, DashboardNoSleepsFragment.newInstance());
            }
        }

        if (SleepSenseDeviceService.instance().hasPumpDevice() ) {
            mDashboardPagerAdapter.addTab("Firmness",R.drawable.tab_firmness_unselected,R.drawable.tab_firmness_selected, FirmnessControlFragment.newInstance());
        }

        if (SleepSenseDeviceService.instance().hasBaseDevice() ) {
            mDashboardPagerAdapter.addTab("Position",R.drawable.tab_position_unselected,R.drawable.tab_position_selected, PositionControlFragment.newInstance());
            mDashboardPagerAdapter.addTab("Massage",R.drawable.tab_massage_unselected,R.drawable.tab_massage_selected, MassageControlFragment.newInstance());
        }

        mDashboardPagerAdapter.addTab("More",R.drawable.tab_more_unselected,R.drawable.tab_more_selected, MoreFragment.newInstance());

        // mDashboardPagerAdapter.addTab("Debug",R.drawable.debug_icon_normal,R.drawable.debug_icon_selected, mDebugFragment);

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
        Intent intent= new Intent(this,ClearDevicesActivity.class);
        startActivity(intent);
        finish();
    }

    public void doOnboarding() {
        Intent intent= new Intent(this,NewOnBoardActivity.class);
        startActivity(intent);
        finish();
    }

}
