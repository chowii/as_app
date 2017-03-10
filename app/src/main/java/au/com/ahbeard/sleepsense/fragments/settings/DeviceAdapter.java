package au.com.ahbeard.sleepsense.fragments.settings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.log.SSLog;

/**
 * Created by sabbib on 9/03/2017.
 */

class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder>
        implements DeviceListItem.DeviceAdapterOnItemOnClickListener {


    private final Context context;
    private List<DeviceListItem> deviceItems;
    private int viewType;

    public DeviceAdapter(List<DeviceListItem> deviceItems, Context context, int viewType) {
        this.deviceItems = deviceItems;
        this.context = context;
        this.viewType = viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if(viewType == R.layout.item_devices_connected)
//            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devices_connected, parent, false));
//        else return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devices_disconnected, parent, false));
        this.viewType = viewType;
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO: 9/03/2017
//            }
//        });

        String s = "VIEW CONTAINER ID \t" + viewType + "\nR.layout.fragment_device " + R.layout.fragment_device;
        SSLog.d(s);
        if(viewType == R.layout.item_devices_connected){
            holder.textViewTitle.setText(deviceItems.get(position).getTitle());
            holder.textViewHead.setText(deviceItems.get(position).getHead());
            holder.textViewSubHead.setText(deviceItems.get(position).getSubHead());
        }else if(viewType == R.layout.item_devices_disconnected){
            holder.textViewTitle.setText(deviceItems.get(position).getTitle());
            String message = holder.textViewTitle.getText().toString();
            holder.textViewWarningMessage.setText("You currently don’t have a " + processedMessage(message) + " set up");
            holder.setUpDeviceButton.setText("Set up " + processedMessage(message));
        }else{
            Log.d("TAG---S", "onBindViewHolder: reachedElse");
        }
    }

    private String processedMessage(String message) {
        String[] messageValues = message.split(" ");
        if(messageValues[0].equalsIgnoreCase("Adjustable")) message = messageValues[1];
        return message.toLowerCase();
    }


    @Override
    public int getItemViewType(int position) {
        if(!deviceItems.get(position).isButton()) {
            return deviceItems.get(position).isConnected() ?
                    R.layout.item_devices_connected : R.layout.item_devices_disconnected;
        }
        return R.layout.device_reset_button;
    }

    @Override
    public int getItemCount() { return deviceItems.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;

        public TextView textViewTitle;
        public TextView textViewHead;
        public TextView textViewSubHead;
        public TextView textViewWarningMessage;
        public Button setUpDeviceButton;
        public Button reConnectDeviceButton;
        public Button resetAllDevices;
        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.device_linear_layout);
            textViewHead = (TextView) itemView.findViewById(R.id.device_head);

            if (textViewHead == null) {
                textViewTitle = (TextView) itemView.findViewById(R.id.device_title);
                textViewWarningMessage = (TextView) itemView.findViewById(R.id.devices_warning_message);
                setUpDeviceButton = (Button) itemView.findViewById(R.id.set_up_device_button);
            }else {
                textViewTitle = (TextView) itemView.findViewById(R.id.device_title);
                textViewSubHead = (TextView) itemView.findViewById(R.id.device_subhead);
                reConnectDeviceButton = (Button) itemView.findViewById(R.id.set_up_device_button);

            }
        }
    }
}
