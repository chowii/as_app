package au.com.ahbeard.sleepsense.fragments.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.ahbeard.sleepsense.BuildConfig;
import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;
import au.com.ahbeard.sleepsense.fragments.DailyDashboardFragment;
import au.com.ahbeard.sleepsense.fragments.MassageControlFragment;
import au.com.ahbeard.sleepsense.fragments.WeeklyDashboardFragment;

import java.util.List;

/**
 * Created by sabbib on 28/02/2017.
 */

public class SettingsBaseFragment extends BaseFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_settings_base, container, false);

        showFirstFragment();

        return v;
    }

    public void replaceFragment(Fragment newFrag) {
        getChildFragmentManager().beginTransaction().replace(R.id.container_settings_base, newFrag).addToBackStack("child-fragments").commit();
    }

    private void showFirstFragment() {
        SettingsListFragment settingsFragment = SettingsFragmentFactory.createSettingsFragment(this);
        if(BuildConfig.DEBUG)
            getChildFragmentManager().beginTransaction().add(R.id.container_settings_base, new MassageControlFragment()).commit();
        getChildFragmentManager().beginTransaction().add(R.id.container_settings_base, settingsFragment).addToBackStack("first-fragment").commit();

    }
}
