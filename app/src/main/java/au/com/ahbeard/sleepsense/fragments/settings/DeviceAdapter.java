package au.com.ahbeard.sleepsense.fragments.settings;

import android.content.Context;
import android.support.v7.app.AlertDialog;
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
    private View.OnClickListener listener;

    private String string;
    public int position;
    private View view;

    public DeviceAdapter(List<DeviceListItem> deviceItems, Context context, int viewType) {
        this.deviceItems = deviceItems;
        this.context = context;
        this.viewType = viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.viewType = viewType;
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(viewType == R.layout.item_devices_connected){
            holder.textViewTitle.setText(deviceItems.get(position).getTitle());
            holder.textViewHead.setText(deviceItems.get(position).getHead());
            holder.textViewSubHead.setText(deviceItems.get(position).getSubHead());
        }else if(viewType == R.layout.item_devices_disconnected){
            holder.textViewTitle.setText(deviceItems.get(position).getTitle());
            String message = holder.textViewTitle.getText().toString();
            holder.textViewWarningMessage.setText("You currently don’t have a " + processedMessage(message) + " set up");
            holder.setUpDeviceButton.setText("Set up " + processedMessage(message));

            holder.setUpDeviceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * TODO: 9/03/2017
                     * add logic to setup correct device
                     */
                    listener.onClick(v);
                }
            });
        }else if(viewType == R.layout.device_reset_button){
            holder.deviceResetLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v);
                }
            });
        }
    }

    private String processedMessage(String message) {
        String[] messageValues = message.split(" ");
        if(messageValues[0].equalsIgnoreCase("Adjustable")) message = messageValues[1];
        return message.toLowerCase();
    }

    public List<DeviceListItem> getDeviceItems(){ return deviceItems; }

    public void onItemClick(View v){
        this.view = v;
    }

    public void setOnClickListener(View.OnClickListener listener) { this.listener = listener; }
    
    @Override
    public int getItemViewType(int position) {
        if(!deviceItems.get(position).isButton())
            return deviceItems.get(position).isConnected() ?
                    R.layout.item_devices_connected : R.layout.item_devices_disconnected;
        return R.layout.device_reset_button;
    }

    @Override
    public int getItemCount() { return deviceItems.size(); }
    public int getPosition() { return position; }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        LinearLayout deviceResetLayout;

        public TextView textViewTitle;
        public TextView textViewHead;
        public TextView textViewSubHead;
        public TextView textViewWarningMessage;
        public Button setUpDeviceButton;
        public Button reConnectDeviceButton;


        public ViewHolder(View itemView) {
            super(itemView);
            textViewHead = (TextView) itemView.findViewById(R.id.device_head);
            deviceResetLayout = (LinearLayout) itemView.findViewById(R.id.device_reset_layout);

            if (textViewHead == null) {
                linearLayout = (LinearLayout) itemView.findViewById(R.id.device_disconnected_layout);
                textViewTitle = (TextView) itemView.findViewById(R.id.device_title);
                textViewWarningMessage = (TextView) itemView.findViewById(R.id.devices_warning_message);
                setUpDeviceButton = (Button) itemView.findViewById(R.id.set_up_device_button);
            }else if (textViewHead != null) {
                linearLayout = (LinearLayout) itemView.findViewById(R.id.device_connected_layout);
                textViewTitle = (TextView) itemView.findViewById(R.id.device_title);
                textViewSubHead = (TextView) itemView.findViewById(R.id.device_subhead);
                reConnectDeviceButton = (Button) itemView.findViewById(R.id.set_up_device_button);
            }
        }
    }
}
