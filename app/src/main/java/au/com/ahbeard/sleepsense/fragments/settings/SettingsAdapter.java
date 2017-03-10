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
import au.com.ahbeard.sleepsense.services.log.SSLog;

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
	private int viewType;

	public List<SettingsListItem> getSettingsItem(){ return settingsItems; }
    public int getPosition() { return position; }


    public SettingsAdapter(List<SettingsListItem> listItems, Context context, int viewItemId) {
        this.settingsItems = listItems;
        this.context = context;
        this.viewItemId = viewItemId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	    this.viewType = viewType;
	    if(viewType == R.layout.item_settings)
		    return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settings, parent, false));
	    else if(viewType == R.layout.settings_version)
		    return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_version, parent, false));
	    return new ViewHolder(
			    LayoutInflater.from(parent.getContext()).inflate(viewItemId, parent, false));
    }

    @Override
    public void onBindViewHolder(final SettingsAdapter.ViewHolder holder, final int position) {
	    holder.linearLayout.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    onItemClick(holder.textViewHead.getText().toString(), position);
			    mListener.onClick(v);
		    }
	    });

	    if(viewType == R.layout.settings_version){
		    holder.textViewVersion.setText(settingsItems.get(position).getHead());
	    }else holder.textViewHead.setText(settingsItems.get(position).getHead());
    }


	@Override
    public int getItemViewType(int position) {
		SettingsListItem item = settingsItems.get(position);
		if(item.isTextRow())  return R.layout.settings_version;
		return R.layout.item_settings;
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

	    public TextView textViewVersion;
	    public TextView textViewHead;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewHead = (TextView) itemView.findViewById(R.id.head);
	        textViewVersion = (TextView) itemView.findViewById(R.id.settings_version);
	        linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }
    }
}
