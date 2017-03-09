package au.com.ahbeard.sleepsense.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;

/**
 * Created by sabbib on 1/03/2017.
 */

public class SettingsListFragment extends BaseFragment {

    protected SettingsBaseFragment mBaseFragment;

    protected TextView titleTextView;
    protected RecyclerView settingsView;
    protected SettingsAdapter adapter;

    protected int titleRes;
    private int layoutName;

    protected int viewContainerId;
    private SettingsAdapterOnItemClickListener clickListener;

    List<SettingsListItem> settingsList;
    private int viewItemId;

    public SettingsListFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {;
        View v = inflater.inflate(layoutName, container, false);
        createSettings(v, viewContainerId);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        titleTextView.setText(titleRes);
    }

    public void createSettings(View v, int settingsText){
        settingsView = (RecyclerView) v.findViewById(settingsText);
        settingsView.setHasFixedSize(true);
        settingsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SettingsAdapter(settingsList, getActivity(), viewItemId);
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

    public void configure(SettingsBaseFragment baseFragment, int titleRes, int layoutId, int viewContainerId,
                          int viewItemId, List<SettingsListItem> items, SettingsAdapterOnItemClickListener itemClicked) {
        this.mBaseFragment = baseFragment;
        this.titleRes = titleRes;
        this.layoutName = layoutId;
        this.viewContainerId = viewContainerId;
        this.viewItemId = viewItemId;
        settingsList = items;
        settingsList = items;
        clickListener = itemClicked;
    }

    public int getLayout(){ return layoutName; }

    interface SettingsAdapterOnItemClickListener{
        void onItemClick(String buttonTitle, int position);
    }
}
