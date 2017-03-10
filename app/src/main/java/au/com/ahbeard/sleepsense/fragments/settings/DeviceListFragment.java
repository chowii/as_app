package au.com.ahbeard.sleepsense.fragments.settings;

import android.content.Context;
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

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.NewOnBoardActivity;
import au.com.ahbeard.sleepsense.activities.PreferenceActivity;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;
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

    public void configureDevice(SettingsBaseFragment baseFragment, int titleRes, int layoutId, int viewContainerId,
                                int viewItemId, List<DeviceListItem> deviceItems, DeviceAdapterOnItemClickListener itemClicked){
        this.mBaseFragment = baseFragment;
        this.titleRes = titleRes;                /* Fragment Title   */
        this.layoutName = layoutId;              /* Fragment Layout  */
        this.viewContainerId = viewContainerId;  /* RecyclerView     */
        this.viewItemId = viewItemId;            /* Item TextView    */
        this.deviceList = deviceItems;
        deviceButtonClickListener = itemClicked;
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
