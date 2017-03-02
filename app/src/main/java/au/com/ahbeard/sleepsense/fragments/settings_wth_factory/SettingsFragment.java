package au.com.ahbeard.sleepsense.fragments.settings_wth_factory;

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

/**
 * Created by sabbib on 1/03/2017.
 */

class SettingsFragmentFactory {

    static SettingsListFragment createSettingsFragment(final Activity activity) {
        SettingsListFragment frag = new SettingsListFragment();

        ArrayList<SettingsListItem> settingsList = new ArrayList<>();
        settingsList.add(new SettingsListItem("My Devices", "Device Info"));
        settingsList.add(new SettingsListItem("My Profile", "Profile Info"));
        settingsList.add(new SettingsListItem("Support", "Assisting you"));
        settingsList.add(new SettingsListItem("Six Week Sleep Challenge", "Sleep Coach"));
        settingsList.add(new SettingsListItem("Privacy Policy", "Our secret"));
        settingsList.add(new SettingsListItem("Terms of Service", "The do's and don'ts"));

        frag.configure(R.layout.item_settings, settingsList, new SettingsListFragment.SettingsAdapterOnItemClickListener() {
            @Override
            public void onItemClick(String buttonTitle) {
                switch(s) {

                    case "My Devices":
                        Toast.makeText(getActivity(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new DeviceFragments()).commit();
                        break;
                    case "My Profile":
                        Toast.makeText(getActivity(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
                        mBaseFragment.replaceFragment(new ProfileFragment());
                        break;
                    case "Support":
                        Toast.makeText(getActivity(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new SupportFragment()).commit();
                        break;
                    case "Six Week Sleep Challenge":
                        Toast.makeText(getActivity(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new SWSCFragments()).commit();
                        break;
                    case "Privacy Policy":
                        Toast.makeText(getActivity(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.startActivity(HelpActivity.getIntent(act.getApplicationContext(),"Privacy Policy","http://www.ahbeard.com.au/privacypolicy"));
//                act.getSupportFragmentManager().beginTransaction().replace(R.id.settings_txt, new PrivacyFragment()).commit();
                        break;
                    case "Terms of Service":
                        Toast.makeText(getActivity(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new ToSFragments()).commit();
                        break;

                }
            }
        });

        return frag;
    }
}

class SettingsListFragment extends BaseFragment {
    protected SettingsBaseFragment mBaseFragment;

    protected RecyclerView settingsView;
    protected SettingsAdapter adapter;

    private int layoutId;
    private SettingsAdapterOnItemClickListener clickListener;

    List<SettingsListItem> settingsList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(layoutId, container, false);

        createSettings(v);

        return v;
    }

    private void createSettings(View v){
        settingsView = (RecyclerView) v.findViewById(R.id.settings_txt);
        settingsView.setHasFixedSize(true);
        settingsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        settingsList = new ArrayList<>();

        adapter = new SettingsAdapter(settingsList, getActivity());
        settingsView.setAdapter(adapter);


        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.v(SettingsAdapter.class.getSimpleName(),"You clicked " + adapter.settingsItems.get(position).getHead());
                AppCompatActivity act = (AppCompatActivity) v.getContext();

                /***
                 * Still need to modularize this section
                 * Decouple switch
                 */


                String s = adapter.settingsItems.get(adapter.position).getHead().toString();
                clickListener.onItemClick(s);
            }
        });

    }

    public void configure(int layoutId, List<SettingsListItem> items, SettingsAdapterOnItemClickListener itemClicked) {
        this.layoutId = layoutId;
        settingsList = items;
        clickListener = itemClicked;
    }

    //abstract protected void preparedData();
    //abstract protected int getLayoutResId();
    //abstract protected void onItemClicked(String buttonTitle);

    interface SettingsAdapterOnItemClickListener{
        void onItemClick(String buttonTitle);
    }
}
