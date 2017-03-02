package au.com.ahbeard.sleepsense.fragments.settings_wth_factory;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;

/**
 * Created by sabbib on 28/02/2017.
 */

public class ProfileFragment extends SettingsFragment {

    private RecyclerView profileSettings;
    private RecyclerView.Adapter adapter;

    List<SettingsListItem> profileList;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.item_my_profile, container, false);

        createSettings(v);
        preparedData();

        return v;
    }

    private void createSettings(View v){
        profileSettings = (RecyclerView) v.findViewById(R.id.my_profile_txt);
        profileSettings.setHasFixedSize(true);
        profileSettings.setLayoutManager(new LinearLayoutManager(getContext()));

        profileList = new ArrayList<>();

        adapter = new SettingsAdapter(profileList, getContext());
        profileSettings.setAdapter(adapter);
    }

    private void preparedData(){
        profileList.add(new SettingsListItem("Target", "Device Info"));
        profileList.add(new SettingsListItem("ght", "Profile Info"));
        profileList.add(new SettingsListItem("ht", "Assisting you"));
        profileList.add(new SettingsListItem("e", "Sleep Coach"));
        profileList.add(new SettingsListItem("e in bed", "Our secret"));
        profileList.add(new SettingsListItem("KJdfkjshf", "The do's and don'ts"));
    }

}
