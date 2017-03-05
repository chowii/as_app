package au.com.ahbeard.sleepsense.fragments.settings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;

import java.util.List;

/**
 * Created by Sabbib on 28/02/2017.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> implements SettingsFragment.SettingsAdapterOnItemClickListener{

    List<SettingsListItem> settingsItems;
    Context context;
    String button;
    int position;

    public List<SettingsListItem> getSettingsItem(){ return settingsItems; }
    public int getPosition() { return position; }
    public String getButtonTitle(){ return button; }


    public SettingsAdapter(List<SettingsListItem> listItems, Context context) {
        this.settingsItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v("Settings", "" + viewType);
        return new ViewHolder(
                LayoutInflater.from(
                        parent.getContext())
                        .inflate(R.layout.item_settings, parent, false));
    }

    @Override
    public void onBindViewHolder(final SettingsAdapter.ViewHolder holder, final int position) {
        holder.textViewHead.setText(settingsItems.get(position).getHead());
        holder.linearLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onItemClick(holder.textViewHead.getText().toString(), position);

                mListener.onClick(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return settingsItems.size();
    }

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
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewHead = (TextView) itemView.findViewById(R.id.head);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }

    }




}
