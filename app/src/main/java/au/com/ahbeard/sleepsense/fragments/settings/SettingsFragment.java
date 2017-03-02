package au.com.ahbeard.sleepsense.fragments.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;
import au.com.ahbeard.sleepsense.fragments.settings.SettingsAdapter;
import au.com.ahbeard.sleepsense.fragments.settings.SettingsBaseFragment;
import au.com.ahbeard.sleepsense.fragments.settings.SettingsListItem;

import static au.com.ahbeard.sleepsense.fragments.settings.SettingsFragment.frag;

/**
 * Created by sabbib on 1/03/2017.
 */

class SettingsFragmentFactory {

//    static SettingsFragment createMyProfileFragment() {
//        ...
//    }

    static SettingsFragment createSettingsFragment(final int layout) {
//        SettingsFragment frag = new SettingsFragment();


        ArrayList<SettingsListItem> settingsList = new ArrayList<>();
        settingsList.add(new SettingsListItem("My Devices", "Device Info"));
        settingsList.add(new SettingsListItem("My Profile", "Profile Info"));
        settingsList.add(new SettingsListItem("Support", "Assisting you"));
        settingsList.add(new SettingsListItem("Six Week Sleep Challenge", "Sleep Coach"));
        settingsList.add(new SettingsListItem("Privacy Policy", "Our secret"));
        settingsList.add(new SettingsListItem("Terms of Service", "The do's and don'ts"));

        frag.configure(layout, settingsList, new SettingsFragment.SettingsAdapterOnItemClickListener() {
            @Override
            public void onItemClick(String s, int position) {
                switch(s) {

                    case "My Devices":
//                        Toast.makeText(getActivity(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new DeviceFragments()).commit();
                        break;
                    case "My Profile":
//                        Toast.makeText(getActivity(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        mBaseFragment.replaceFragment(new ProfileFragment());
                        break;
                    case "Support":
//                        Toast.makeText(getActivity(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new SupportFragment()).commit();
                        break;
                    case "Six Week Sleep Challenge":
//                        Toast.makeText(getActivity(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new SWSCFragments()).commit();
                        break;
                    case "Privacy Policy":
//                        Toast.makeText(getActivity(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.startActivity(HelpActivity.getIntent(act.getApplicationContext(),"Privacy Policy","http://www.ahbeard.com.au/privacypolicy"));
//                act.getSupportFragmentManager().beginTransaction().replace(R.id.settings_txt, new PrivacyFragment()).commit();
                        break;
                    case "Terms of Service":
//                        Toast.makeText(, "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new ToSFragments()).commit();
                        break;

                }
            }
        });

        return frag;
    }
}

public class SettingsFragment extends BaseFragment {

    protected SettingsBaseFragment mBaseFragment;

    protected RecyclerView settingsView;
    protected SettingsAdapter adapter;

    private int layoutId;
    private SettingsAdapterOnItemClickListener clickListener;

    List<SettingsListItem> settingsList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {;

        View v = inflater.inflate(layoutId, container, false);

        createSettings(v);

        return v;
    }

    static SettingsFragment frag;

    public static SettingsFragment newInstance(SettingsBaseFragment baseFragment){
        frag = new SettingsFragment();
        frag.mBaseFragment = baseFragment;
        return frag;
    }

    public static SettingsFragment getInstance(){ return frag;}

    private void createSettings(View v){
        settingsView = (RecyclerView) v.findViewById(R.id.settings_txt);
        settingsView.setHasFixedSize(true);
        settingsView.setLayoutManager(new LinearLayoutManager(getActivity()));



        adapter = new SettingsAdapter(settingsList, getActivity());
        settingsView.setAdapter(adapter);


        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = adapter.getPosition();
                String s = adapter.getSettingsItem().get(position).getHead().toString();
                clickListener.onItemClick(s, adapter.position);
            }
        });

    }

    public void configure(int layoutId, List<SettingsListItem> items, SettingsAdapterOnItemClickListener itemClicked) {
        this.layoutId = layoutId;
        settingsList = items;
        clickListener = itemClicked;
    }


    interface SettingsAdapterOnItemClickListener{
        void onItemClick(String buttonTitle, int position);
    }
}
