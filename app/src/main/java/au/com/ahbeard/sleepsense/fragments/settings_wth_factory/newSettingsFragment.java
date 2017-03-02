package au.com.ahbeard.sleepsense.fragments.settings_wth_factory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;

/**
 * Created by sabbib on 1/03/2017.
 */

public class newSettingsFragment extends BaseFragment implements newSettingsFragmentFactory {

    List<SettingsListItem> settingsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_settings, container, false);

        makeSetting(v);0

        return v;
    }

    private void preparedSettingsOption() {
        settingsList.add(new SettingsListItem("My Devices", "Device Info"));
        settingsList.add(new SettingsListItem("My Profile", "Profile Info"));
        settingsList.add(new SettingsListItem("Support", "Assisting you"));
        settingsList.add(new SettingsListItem("Six Week Sleep Challenge", "Sleep Coach"));
        settingsList.add(new SettingsListItem("Privacy Policy", "Our secret"));
        settingsList.add(new SettingsListItem("Terms of Service", "The do's and don'ts"));
    }


    @Override
    public void makeSetting(View v) {
        createSettingOption(v);
        preparedSettingsOption();
    }

    private void createSettingOption(View v){
        RecyclerView settingsView;
        SettingsAdapter adapter;



        settingsView = (RecyclerView) v.findViewById(R.id.settings_txt);
        settingsView.setHasFixedSize(true);
        settingsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        settingsList = new ArrayList<>();

        adapter = new SettingsAdapter(settingsList, getActivity());
        settingsView.setAdapter(adapter);

    }
}
