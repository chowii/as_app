package au.com.ahbeard.sleepsense.fragments.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.HomeActivity;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;

/**
 * Created by sabbib on 28/02/2017.
 */

public class SettingsBaseFragment extends BaseFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_settings_base, container, false);

        showFirstFragment();

        getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            int stackCount = 0;

            @Override
            public void onBackStackChanged() {
                //FIXME: This will work for now but update when we have the Devices onboarding fragments
                int currBackStackCount = getChildFragmentManager().getBackStackEntryCount();
                if (stackCount > currBackStackCount) {
                    stackCount --;
                    showBottomBar();
                } else if (stackCount < currBackStackCount) {
                    stackCount ++;
                } //ignore equal stacks
            }
        });

        return v;
    }

    public void hideBottomBar() {
        if (getActivity() != null) {
            ((HomeActivity) getActivity()).hideBottomBar();
        }
    }

    public void showBottomBar() {
        if (getActivity() != null) {
            ((HomeActivity) getActivity()).showBottomBar();
        }
    }

    public void replaceFragment(Fragment newFrag) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, newFrag)
                .addToBackStack(newFrag.getClass().getName())
                .commit();
    }

    private void showFirstFragment() {
        SettingsListFragment settingsFragment = SettingsFragmentFactory.createSettingsFragment(this);
        getChildFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, settingsFragment)
                .commit();
    }
}
