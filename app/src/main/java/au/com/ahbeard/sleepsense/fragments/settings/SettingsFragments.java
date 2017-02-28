package au.com.ahbeard.sleepsense.fragments.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.settings.SettingsAdapter;
import au.com.ahbeard.sleepsense.fragments.settings.SettingsList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabbib on 28/02/2017.
 */

public class SettingsFragments extends Fragment {

    private RecyclerView settingsView;
    private RecyclerView.Adapter adapter;

    List<SettingsList> settingsList;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.settings_fragment_card, container, false);

        createSettings(v);
        preparedData();

        return v;
    }

    private void createSettings(View v){
        settingsView = (RecyclerView) v.findViewById(R.id.rView);
        settingsView.setHasFixedSize(true);
        settingsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        settingsList = new ArrayList<>();

        adapter = new SettingsAdapter(settingsList, getActivity());
        settingsView.setAdapter(adapter);


    }

    private void preparedData(){
        settingsList.add(new SettingsList("My Devices", "Device Info"));
        settingsList.add(new SettingsList("My Profile", "Profile Info"));
        settingsList.add(new SettingsList("Support", "Assisting you"));
        settingsList.add(new SettingsList("Six Week Sleep Challenge", "Sleep Coach"));
        settingsList.add(new SettingsList("Privacy Policy", "Our secret"));
        settingsList.add(new SettingsList("Terms of Service", "The do's and don'ts"));
    }

}
