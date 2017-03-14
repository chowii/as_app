package au.com.ahbeard.sleepsense.fragments.settings;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.Buffer;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import au.com.ahbeard.sleepsense.BuildConfig;
import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;
import au.com.ahbeard.sleepsense.services.log.SSLog;

import static io.reactivex.BackpressureStrategy.BUFFER;

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
	    titleTextView.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    String deviceInfo = "MODEL: " + Build.MODEL;
			    deviceInfo += "\nRELEASE: " + Build.VERSION.RELEASE + "\nSDK_INT: " + Build.VERSION.SDK_INT;
			    deviceInfo += "\nAPP V_NAME: " + BuildConfig.VERSION_NAME + "\nAPP V_CODE: " + BuildConfig.VERSION_CODE;
			    deviceInfo += "\nDIR: " +Environment.getExternalStorageDirectory();
			    new AlertDialog.Builder(getContext()).setTitle("Device Details").setMessage(deviceInfo).show();

		    }
	    });
    }

    protected void createSettings(View v, int settingsText){
        settingsView = (RecyclerView) v.findViewById(settingsText);
        settingsView.setHasFixedSize(true);
        settingsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();
        settingsView.setAdapter(adapter);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = adapter.getPosition();
                String s = adapter.getSettingsItem().get(position).getHead();
                clickListener.onItemClick(s, adapter.position);
            }
        });
    }

    protected void setupAdapter() {
        adapter = new SettingsAdapter(settingsList, getActivity(), viewItemId);
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
