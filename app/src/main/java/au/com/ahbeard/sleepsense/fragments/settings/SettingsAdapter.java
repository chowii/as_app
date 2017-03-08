package au.com.ahbeard.sleepsense.fragments.settings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.log.SSLog;

import java.util.List;

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
    public String getButtonTitle(){ return button; }


    public SettingsAdapter(List<SettingsListItem> listItems, Context context, int viewItemId) {
        this.settingsItems = listItems;
        this.context = context;
        this.viewItemId = viewItemId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == R.layout.item_devices_connected) {
            SSLog.d("TAG_DEV con viewType: " + viewType);
        }
        if(viewType == R.layout.item_devices_disconnected) {
            SSLog.d("TAG_DEV dis viewType: " + viewType);
        }
        SSLog.d("TAG_DEV dis viewType: " + parent.getId());


        if(
                (parent.findViewById(R.id.head) == null)
                        &&
                (parent.findViewById(R.id.device_title) == null))
        {
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
        }
        else if(
                ((parent.findViewById(R.id.device_subhead_1) != null)
                        &&
                (parent.findViewById(R.id.device_title) != null))
                        &&
                (parent.findViewById(R.id.device_subhead_1).getId() == R.id.device_subhead_1))
        {
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devices_connected, parent, false));
        }
        else if(
                (parent.findViewById(R.id.device_title) != null)
                        &&
                (parent.findViewById(R.id.device_subhead_1) == null))
        {
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devices_disconnected, parent, false));
        }



        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(final SettingsAdapter.ViewHolder holder, final int position) {
        SSLog.d("TAG_TAG A " + settingsItems.size());
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
            holder.textViewWarningMessage.setText("You currently don’t have a Sleep Tracker set up");
        }

//
//        if(holder.textViewSubHead_1 == null
//                &&
//                holder.textViewTitle == null) {
//            SSLog.d("TAG_TAG B " + settingsItems.size());
//
//        } else {
//            SSLog.d("TAG_TAG C " + settingsItems.size());
//
//        }

    }

    @Override
    public int getItemViewType(int position) {
        SettingsListItem item = settingsItems.get(position);
        return item.isTextRow() ? R.layout.item_devices_connected : R.layout.item_settings;
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

        LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);

            if(
                    (itemView.findViewById(R.id.device_title) == null)
                            &&
                            (itemView.findViewById(R.id.head).getId() == R.id.head))
            {
                textViewHead = (TextView) itemView.findViewById(R.id.head);
                linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            }
            else if(
                    ((itemView.findViewById(R.id.device_subhead_1) != null)
                                        &&
                    (itemView.findViewById(R.id.device_title) != null))
                                        &&
                    (itemView.findViewById(R.id.device_subhead_1).getId() == R.id.device_subhead_1))
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
            }
        }
    }




}
