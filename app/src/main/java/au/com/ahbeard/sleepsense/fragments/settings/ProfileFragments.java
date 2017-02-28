package au.com.ahbeard.sleepsense.fragments.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class ProfileFragments extends Fragment {

    private RecyclerView profileSettings;
    private RecyclerView.Adapter adapter;

    List<SettingsList> profileList;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.my_profile_fragment_card, container, false);

        createSettings(v);
        preparedData();

        return v;
    }

    private void createSettings(View v){
        profileSettings = (RecyclerView) v.findViewById(R.id.my_profile_card);
        profileSettings.setHasFixedSize(true);
        profileSettings.setLayoutManager(new LinearLayoutManager(getActivity()));

        profileList = new ArrayList<>();

        adapter = new SettingsAdapter(profileList, getActivity());
        profileSettings.setAdapter(adapter);
    }

    private void preparedData(){
        profileList.add(new SettingsList("Sleep Target", "Device Info"));
        profileList.add(new SettingsList("Weight", "Profile Info"));
        profileList.add(new SettingsList("Height", "Assisting you"));
        profileList.add(new SettingsList("Age", "Sleep Coach"));
        profileList.add(new SettingsList("People in bed", "Our secret"));
        profileList.add(new SettingsList("Gender", "The do's and don'ts"));
    }

}
