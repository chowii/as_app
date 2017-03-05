package au.com.ahbeard.sleepsense.fragments.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;
import java.util.List;

/**
 * Created by sabbib on 28/02/2017.
 */

public class SettingsBaseFragment extends BaseFragment {

    private RecyclerView settingsView;
    private RecyclerView.Adapter adapter;

    List<SettingsListItem> settingsList;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_settings_base, container, false);

        showFirstFragment();

        return v;
    }

    public void replaceFragment(Fragment newFrag) {
        getChildFragmentManager().beginTransaction().replace(R.id.container_settings_base, newFrag).addToBackStack("back-rest").commit();
    }

    private void showFirstFragment() {
        SettingsFragment settingsFragment = SettingsFragment.newInstance(this);
        SettingsFragmentFactory.createSettingsFragment(R.layout.fragment_settings);
        getChildFragmentManager().beginTransaction().add(R.id.container_settings_base, settingsFragment).addToBackStack("back-first").commit();

    }
}
