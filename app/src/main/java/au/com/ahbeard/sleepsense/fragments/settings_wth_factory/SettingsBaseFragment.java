package au.com.ahbeard.sleepsense.fragments.settings_wth_factory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;

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
        getChildFragmentManager().beginTransaction().replace(R.id.container_settings_base, newFrag).commit();
    }

    private void showFirstFragment() {
        SettingsFragment frag = SettingsFragment.newInstance(this);
        getChildFragmentManager().beginTransaction().add(R.id.container_settings_base, frag).commit();
    }
}
