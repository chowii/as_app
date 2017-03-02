package au.com.ahbeard.sleepsense.fragments.settings_wth_factory;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import au.com.ahbeard.sleepsense.R;

/**
 * Created by Sabbib on 28/02/2017.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> implements SettingsFragment.SettingsAdapterOnItemClickListener{

    List<SettingsListItem> settingsItems;
    Context context;
    int position;
    private static final String TAG = SettingsAdapter.class.getSimpleName();


    public SettingsAdapter(List<SettingsListItem> listItems, Context context) {
        this.settingsItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(
                        parent.getContext())
                        .inflate(R.layout.settings_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final SettingsAdapter.ViewHolder holder, final int position) {
        holder.textViewHead.setText(settingsItems.get(position).getHead());
        holder.linearLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onItemClick(position);
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
    public void onItemClick(int pos) {
        this.position = pos;

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
