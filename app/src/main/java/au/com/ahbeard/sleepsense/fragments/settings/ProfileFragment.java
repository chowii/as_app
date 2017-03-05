package au.com.ahbeard.sleepsense.fragments.settings;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);

        createSettings(v);


        return v;
    }

    public void createSettings(View v){
        profileSettings = (RecyclerView) v.findViewById(R.id.my_profile_txt);
        profileSettings.setHasFixedSize(true);
        profileSettings.setLayoutManager(new LinearLayoutManager(getContext()));

        profileList = new ArrayList<>();
        adapter = new SettingsAdapter(profileList, getContext());
        profileSettings.setAdapter(adapter);
    }


    public void onBackPressed(){

    }

}
