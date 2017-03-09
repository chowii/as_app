package au.com.ahbeard.sleepsense.fragments.settings;

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
import au.com.ahbeard.sleepsense.fragments.BaseFragment;
import au.com.ahbeard.sleepsense.services.log.SSLog;

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
    private DeviceAdapterOnItemClickListener deviceClickListener;

    RecyclerView deviceView;
    private DeviceAdapter deviceAdapter;
    private TextView titleTextView;

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
        deviceClickListener = itemClicked;
    }


    interface DeviceAdapterOnItemClickListener{
        void onItemClick(String buttonTitle, int position);
    }


}
