package au.com.ahbeard.sleepsense.fragments.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.services.log.SSLog;
import au.com.ahbeard.sleepsense.ui.onboarding.MainOnboardingActivity;

/**
 * Created by sabbib on 9/03/2017.
 */


public class DeviceListFragment extends BaseFragment{


    protected SettingsBaseFragment mBaseFragment;
    private int titleRes;
    private int layoutName;
    private int viewContainerId;
    private int viewItemId;
    private List<DeviceListItem> deviceList;
    private DeviceAdapterOnItemClickListener deviceButtonClickListener;

    RecyclerView deviceView;
    private DeviceAdapter deviceAdapter;
    private TextView titleTextView;

    public DeviceListFragment()
    { // TODO: 10/03/2017 add reset button to list in constructor and edit configureDevices().method to add list then changing reference to param list
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        configureDevice(this.deviceButtonClickListener);
        createDeviceViews(view, viewContainerId);
        return view;
    }

    private void createDeviceViews(View view, int viewContainerId) {
        deviceView = (RecyclerView) view.findViewById(R.id.device_txt);
        deviceView.setHasFixedSize(true);
        deviceView.setLayoutManager(new LinearLayoutManager(getActivity()));
        deviceAdapter = new DeviceAdapter(deviceList, getActivity(), viewItemId);
        deviceView.setAdapter(deviceAdapter);
        deviceAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceButtonClickListener.onItemClick(v);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        titleTextView.setText(titleRes);
    }

    public void setDeviceOnClickListener(DeviceAdapterOnItemClickListener itemClicked){
        this.deviceButtonClickListener = itemClicked;
    }

    public void configureDevice(DeviceAdapterOnItemClickListener itemClicked){
//        this.mBaseFragment = baseFragment;
        this.titleRes = R.string.settings_device_title;                /* Fragment Title   */
        this.layoutName = R.layout.fragment_device;              /* Fragment Layout  */
        this.viewContainerId = R.id.device_txt;  /* RecyclerView     */
        this.viewItemId = R.layout.item_devices_connected;            /* Item TextView    */
        this.deviceList = populateDevices();
        deviceButtonClickListener = itemClicked;
    }


    private List<DeviceListItem> populateDevices(){
        final List<DeviceListItem> deviceItemList = new ArrayList<>();

        String[] deviceList = new String[3];
        deviceList[0] = PreferenceService.instance().getPumpDeviceAddress();    //Mattress
        deviceList[1] = PreferenceService.instance().getBaseDeviceAddress();    //Adjustable Base
        deviceList[2] = PreferenceService.instance().getTrackerDeviceAddress(); //Sleep Tracker


	    if(deviceList[0] != null)
	    	deviceItemList.add(new DeviceListItem("Mattress", deviceList[0], deviceList[0], getDeviceStatus(deviceList[0]), false));
	    else
		    deviceItemList.add(new DeviceListItem("Mattress", null, null, getDeviceStatus(deviceList[0]), false));

	    if(deviceList[1] != null)
		    deviceItemList.add(new DeviceListItem("Adjustable Tracker", deviceList[1], deviceList[1], getDeviceStatus(deviceList[1]), false));
	    else deviceItemList.add(new DeviceListItem("Adjustable Tracker", null, null, getDeviceStatus(deviceList[1]), false));

	    if(deviceList[1] != null)
	    	deviceItemList.add(new DeviceListItem("Sleep Tracker", deviceList[2], deviceList[2], getDeviceStatus(deviceList[2]), false));
		else deviceItemList.add(new DeviceListItem("Sleep Tracker", null, null, getDeviceStatus(deviceList[2]), false));

        deviceItemList.add(new DeviceListItem("Reset Device", null, null, false, true));
//
//	    deviceItemList.add(new DeviceListItem("Mattress", getDeviceConnectionData(), getDeviceConnectionData(), getDeviceStatus(deviceList), false));
//	    deviceItemList.add(new DeviceListItem("Adjustable Tracker", getDeviceConnectionData(), getDeviceConnectionData(), getDeviceStatus(deviceList), false));
//	    deviceItemList.add(new DeviceListItem("Sleep Tracker", getDeviceConnectionData(), getDeviceConnectionData(), getDeviceStatus(deviceList), false));


	    return deviceItemList;
    }

	private String[] getDeviceConnectionData(String... connectedDevice){
		// TODO: 13/03/2017 get device connection info from Device Address and return connection values
		return connectedDevice;
    }


    private boolean getDeviceStatus(String deviceAddress){
        if(deviceAddress == null) {
            getDeviceConnectionData(deviceAddress);
            return false;
        }
        else{
            getDeviceConnectionData(deviceAddress);
            return true;
        }
    }

    /***
	 * Disconnects connections with all devices, and Navigates activity to connect with all activities
	 * @param view
	 */
	public void resetDevices(final View view){
        new AlertDialog.Builder(view.getContext())
                .setPositiveButton(view.getResources().getString(R.string.preference_dialog_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent resetDeviceIntent =new Intent(getActivity(), MainOnboardingActivity.class);
                                resetDeviceIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(resetDeviceIntent);
                                getActivity().finish();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        }).setMessage(view.getResources().getString(R.string.preference_dialog_message)).create().show();
    }


    interface DeviceAdapterOnItemClickListener{
        void onItemClick(View view);
    }


}
