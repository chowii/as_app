package au.com.ahbeard.sleepsense.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;
import au.com.ahbeard.sleepsense.services.log.SSLog;

import static au.com.ahbeard.sleepsense.fragments.settings.SettingsFragment.frag;

/**
 * Created by sabbib on 1/03/2017.
 */

class SettingsFragmentFactory {

    static SettingsFragment createMyProfileFragment(final int lay){
        List<SettingsListItem> settingsList = new ArrayList<>();

        settingsList.add(new SettingsListItem("Sleep Target"));
        settingsList.add(new SettingsListItem("Weight"));
        settingsList.add(new SettingsListItem("Height"));
        settingsList.add(new SettingsListItem("Age"));
        settingsList.add(new SettingsListItem("Gender"));



        frag.configure(R.string.settings_profile_title, lay, settingsList, new SettingsFragment.SettingsAdapterOnItemClickListener() {
            @Override
            public void onItemClick(String buttonTitle, int position) {
                SSLog.e("profile clicked " + buttonTitle + " at " + position);

            }
        });
        return frag;
    }


    static SettingsFragment createSettingsFragment(final int layout) {

        ArrayList<SettingsListItem> settingsList = new ArrayList<>();

        settingsList.add(new SettingsListItem("My Devices"));
        settingsList.add(new SettingsListItem("My Profile"));
        settingsList.add(new SettingsListItem("Support"));
        settingsList.add(new SettingsListItem("Six Week Sleep Challenge"));
        settingsList.add(new SettingsListItem("Privacy Policy"));
        settingsList.add(new SettingsListItem("Terms of Service"));

        frag.configure(R.string.settings_more_title, layout, settingsList, new SettingsFragment.SettingsAdapterOnItemClickListener() {
            @Override
            public void onItemClick(String s, int position) {
                switch(s) {

                    case "My Devices":
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new DeviceFragments()).commit();
                        break;
                    case "My Profile":
                        frag.mBaseFragment.replaceFragment(new ProfileFragment());
                        break;
                    case "Support":
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new SupportFragment()).commit();
                        break;
                    case "Six Week Sleep Challenge":
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new SWSCFragments()).commit();
                        break;
                    case "Privacy Policy":
                        frag.buttonTitle = s;
                        frag.mBaseFragment.replaceFragment(new AssistFragment());
                        break;
                    case "Terms of Service":
                        frag.buttonTitle = s;
                        frag.mBaseFragment.replaceFragment(new AssistFragment());
                        break;

                }
            }
        });

        return frag;
    }



}

public class SettingsFragment extends BaseFragment {

    protected SettingsBaseFragment mBaseFragment;

    protected TextView titleTextView;
    protected RecyclerView settingsView;
    protected SettingsAdapter adapter;

    protected int titleRes;
    private int layoutName;
    private SettingsAdapterOnItemClickListener clickListener;

    List<SettingsListItem> settingsList;

    String buttonTitle;

    static SettingsFragment frag;

    public SettingsFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {;
        View v = inflater.inflate(layoutName, container, false);

        createSettings(v, R.id.settings_txt);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        titleTextView.setText(titleRes);
    }

    public static SettingsFragment newInstance(SettingsBaseFragment baseFragment){
        frag = new SettingsFragment();
        frag.mBaseFragment = baseFragment;
        return frag;
    }

    public static SettingsFragment getInstance(){ return frag;}

    public void createSettings(View v, int settingsText){
        settingsView = (RecyclerView) v.findViewById(settingsText);
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

    public void configure(int titleRes, int layoutId, List<SettingsListItem> items, SettingsAdapterOnItemClickListener itemClicked) {
        this.titleRes = titleRes;
        this.layoutName = layoutId;
        settingsList = items;
        clickListener = itemClicked;
    }


    public int getLayout(){ return layoutName; }

    interface SettingsAdapterOnItemClickListener{
        void onItemClick(String buttonTitle, int position);
    }
}
