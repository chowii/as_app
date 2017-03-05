package au.com.ahbeard.sleepsense.fragments.settings;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.log.SSLog;

/**
 * Created by sabbib on 6/03/2017.
 */
class SettingsFragmentFactory {

    static SettingsListFragment createMyProfileFragment(SettingsBaseFragment baseFragment){
        List<SettingsListItem> settingsList = new ArrayList<>();

        settingsList.add(new SettingsListItem("Sleep Target"));
        settingsList.add(new SettingsListItem("Weight"));
        settingsList.add(new SettingsListItem("Height"));
        settingsList.add(new SettingsListItem("Age"));
        settingsList.add(new SettingsListItem("Gender"));

        SettingsListFragment frag = new SettingsListFragment();

        frag.configure(baseFragment, R.string.settings_profile_title, R.layout.fragment_settings, settingsList, new SettingsListFragment.SettingsAdapterOnItemClickListener() {
            @Override
            public void onItemClick(String buttonTitle, int position) {
                SSLog.e("profile clicked " + buttonTitle + " at " + position);

            }
        });
        return frag;
    }


    static SettingsListFragment createSettingsFragment(final SettingsBaseFragment baseFragment) {

        ArrayList<SettingsListItem> settingsList = new ArrayList<>();

        settingsList.add(new SettingsListItem("My Devices"));
        settingsList.add(new SettingsListItem("My Profile"));
        settingsList.add(new SettingsListItem("Support"));
        settingsList.add(new SettingsListItem("Six Week Sleep Challenge"));
        settingsList.add(new SettingsListItem("Privacy Policy"));
        settingsList.add(new SettingsListItem("Terms of Service"));

        final SettingsListFragment frag = new SettingsListFragment();

        frag.configure(baseFragment, R.string.settings_more_title, R.layout.fragment_settings, settingsList, new SettingsListFragment.SettingsAdapterOnItemClickListener() {
            @Override
            public void onItemClick(String s, int position) {
                switch(s) {

                    case "My Devices":
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new DeviceFragments()).commit();
                        break;
                    case "My Profile":
                        SettingsListFragment myProfileFrag = createMyProfileFragment(baseFragment);
                        baseFragment.replaceFragment(myProfileFrag);
                        break;
                    case "Support":
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new SupportFragment()).commit();
                        break;
                    case "Six Week Sleep Challenge":
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new SWSCFragments()).commit();
                        break;
                    case "Privacy Policy":
                    case "Terms of Service":
                        baseFragment.replaceFragment(createWebViewFragment(s));
                        break;
                }
            }
        });

        return frag;
    }

    public static AssistFragment createWebViewFragment(String buttonTitle) {
        AssistFragment frag = new AssistFragment();

        switch(buttonTitle) {
            case "Privacy Policy":
                frag.configure("http://www.ahbeard.com.au/privacypolicy");
                break;
            case "Terms of Service":
                frag.configure("https://sleepsense.com.au/terms-of-service");
                break;
        }

        return frag;
    }



}
