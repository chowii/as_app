package au.com.ahbeard.sleepsense.fragments.settings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import au.com.ahbeard.sleepsense.R;

/**
 * Created by Sabbib on 28/02/2017.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder>
        implements SettingsListFragment.SettingsAdapterOnItemClickListener {

    List<SettingsListItem> settingsItems;
    Context context;
    String button;
    int position;
    int viewItemId;

    public List<SettingsListItem> getSettingsItem(){ return settingsItems; }
    public int getPosition() { return position; }


    public SettingsAdapter(List<SettingsListItem> listItems, Context context, int viewItemId) {
        this.settingsItems = listItems;
        this.context = context;
        this.viewItemId = viewItemId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == R.layout.item_devices_connected)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devices_connected, parent, false));
        if(viewType == R.layout.item_devices_disconnected)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devices_disconnected, parent, false));
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(viewItemId, parent, false));
    }

    @Override
    public void onBindViewHolder(final SettingsAdapter.ViewHolder holder, final int position) {
        holder.linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onItemClick(holder.textViewHead.getText().toString(), position);
                mListener.onClick(v);
            }
        });
        if(     (holder.textViewTitle == null)
                        &&
                (holder.textViewHead != null))
        {
            holder.textViewHead.setText(settingsItems.get(position).getHead());
        }else if((holder.textViewTitle != null)
                        &&
                (holder.textViewHead != null))
        {
            holder.textViewTitle.setText(settingsItems.get(position).getTitle());
            holder.textViewHead.setText(settingsItems.get(position).getHead());
            holder.textViewSubHead_1.setText(settingsItems.get(position).getSubHead1());
        }else if((holder.textViewTitle != null)
                        &&
                (holder.textViewHead == null))
        {
            holder.textViewTitle.setText(settingsItems.get(position).getTitle());

            String message = (String) holder.textViewTitle.getText();
            String[] messageValues = message.split(" ");
            if(messageValues[0].equalsIgnoreCase("Adjustable"))
                message = messageValues[1];
            holder.textViewWarningMessage.setText("You currently don’t have a " + message.toLowerCase() + " set up");
            holder.setUpDeviceButton.setText("Set up " + message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        SettingsListItem item = settingsItems.get(position);
        if(item.isTextRow() == true && item.getSubHead1() == null)
            return R.layout.item_devices_disconnected;
        else if(item.isTextRow() == true && item.getSubHead1() != null)
            return R.layout.item_devices_connected;
        else return R.layout.item_settings;
    }

    @Override
    public int getItemCount() { return settingsItems.size(); }

    private View.OnClickListener mListener;

    public void setOnClickListener(View.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onItemClick(String buttonTitle, int position) {
        button = buttonTitle;
        this.position = position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewHead;
        public TextView textViewSubHead_1;

        public TextView textViewTitle;
        public TextView textViewWarningMessage;
        public Button setUpDeviceButton;

        LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            if(
                    (itemView.findViewById(R.id.device_title) == null)
                            &&
                            (itemView.findViewById(R.id.head) != null))
            {
                textViewHead = (TextView) itemView.findViewById(R.id.head);
                linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            }
            else if(
                    ((itemView.findViewById(R.id.device_subhead_1) != null)
                                        &&
                    (itemView.findViewById(R.id.device_title) != null)))
            {
                linearLayout = (LinearLayout) itemView.findViewById(R.id.device_linear_layout);
                textViewTitle = (TextView) itemView.findViewById(R.id.device_title);
                textViewHead = (TextView) itemView.findViewById(R.id.device_subhead_1);
                textViewSubHead_1 = (TextView) itemView.findViewById(R.id.device_subhead_2);

            }else if((itemView.findViewById(R.id.device_title) != null)
                                        &&
                    (itemView.findViewById(R.id.device_subhead_1) == null))
            {
                linearLayout = (LinearLayout) itemView.findViewById(R.id.device_linear_layout_disconnected);
                textViewTitle = (TextView) itemView.findViewById(R.id.device_title);
                textViewWarningMessage = (TextView) itemView.findViewById(R.id.devices_warning_message);
                setUpDeviceButton = (Button) itemView.findViewById(R.id.set_up_device);
            }
        }
    }




}
