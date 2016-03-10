package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.DashboardFragment;
import au.com.ahbeard.sleepsense.fragments.MassageControlFragment;
import au.com.ahbeard.sleepsense.fragments.MoreFragment;
import au.com.ahbeard.sleepsense.fragments.PositionControlFragment;
import au.com.ahbeard.sleepsense.fragments.PumpControlFragment;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.widgets.SimpleTabStrip;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by neal on 3/03/2016.
 */
public class DashboardActivity extends BaseActivity {

    private DashboardPagerStripSimple mDashboardPagerAdapter;

    @OnClick(R.id.dashboard_fab_start_sleep)
    void onStartSleepClicked() {
        Intent intent = new Intent(this,SleepTrackingActivity.class);
        startActivity(intent);
    }

    @Bind(R.id.dashboard_view_pager)
    ViewPager mViewPager;

    @Bind(R.id.dashboard_simple_tab_strip)
    SimpleTabStrip mSimpleTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (PreferenceService.instance().requiresOnBoarding()) {
            Intent intent = new Intent(this,OnBoardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);

        mDashboardPagerAdapter = new DashboardPagerStripSimple(getSupportFragmentManager());
        mViewPager.setAdapter(mDashboardPagerAdapter);
        mSimpleTabStrip.setViewPager(mViewPager);

    }

    class DashboardPagerStripSimple extends FragmentPagerAdapter implements SimpleTabStrip.TabProvider {

        private String[] mTabNames = {
                "Dashboard",
                "Firmness",
                "Position",
                "Massage",
                "More"
        };

        private int[] mTabIconResourceIds = {
                R.drawable.tab_dashboard_unselected,
                R.drawable.tab_firmness_unselected,
                R.drawable.tab_position_unselected,
                R.drawable.tab_massage_unselected,
                R.drawable.tab_more_unselected
        };

        private int[] mSelectedTabIconResourceIds = {
                R.drawable.tab_dashboard_selected,
                R.drawable.tab_firmness_selected,
                R.drawable.tab_position_selected,
                R.drawable.tab_massage_selected,
                R.drawable.tab_more_selected
        };


        private Fragment[] mFragments = {
                DashboardFragment.newInstance("",""),
                PumpControlFragment.newInstance(),
                PositionControlFragment.newInstance("",""),
                MassageControlFragment.newInstance("",""),
                MoreFragment.newInstance("","")
        };

        public DashboardPagerStripSimple(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public String getName(int position) {
            return mTabNames[position];
        }

        @Override
        public int getIconResourceId(int position) {
            return mTabIconResourceIds[position];
        }
        @Override
        public int getSelectedIconResourceId(int position) {
            return mSelectedTabIconResourceIds[position];
        }
    }

}
