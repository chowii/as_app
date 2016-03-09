package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.DashboardFragment;
import au.com.ahbeard.sleepsense.fragments.MassageControlFragment;
import au.com.ahbeard.sleepsense.fragments.MoreFragment;
import au.com.ahbeard.sleepsense.fragments.PositionControlFragment;
import au.com.ahbeard.sleepsense.fragments.PumpControlFragment;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by neal on 3/03/2016.
 */
public class DashboardActivity extends BaseActivity {

    private DashboardPagerAdapter mDashboardPagerAdapter;

    @Bind(R.id.dashboard_view_pager)
    ViewPager mViewPager;

    @Bind({R.id.dashboard_tab_1,R.id.dashboard_tab_2,R.id.dashboard_tab_3,R.id.dashboard_tab_4,R.id.dashboard_tab_5})
    List<ViewGroup> mTabs;

    @OnClick({R.id.dashboard_tab_1,R.id.dashboard_tab_2,R.id.dashboard_tab_3,R.id.dashboard_tab_4,R.id.dashboard_tab_5})
    void tabClicked(ViewGroup viewGroup) {
        mViewPager.setCurrentItem(mTabs.indexOf(viewGroup));
    }

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

        mDashboardPagerAdapter = new DashboardPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mDashboardPagerAdapter);

    }

    class DashboardPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] mFragments = {
                DashboardFragment.newInstance("",""),
                PumpControlFragment.newInstance(),
                PositionControlFragment.newInstance("",""),
                MassageControlFragment.newInstance("",""),
                MoreFragment.newInstance("","")
        };

        public DashboardPagerAdapter(FragmentManager fm) {
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
    }

}
