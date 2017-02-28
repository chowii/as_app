package au.com.ahbeard.sleepsense.fragments.settings;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.settings.ProfileFragments;

import java.util.List;

import static au.com.ahbeard.sleepsense.R.layout.settings_view;

/**
 * Created by Sabbib on 28/02/2017.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder>{

    List<SettingsList> listItems;
    Context context;
    private static final String TAG = SettingsAdapter.class.getSimpleName();


    public SettingsAdapter(List<SettingsList> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(
                        parent.getContext())
                        .inflate(settings_view,
                                parent,
                                false));
    }

    @Override
    public void onBindViewHolder(final SettingsAdapter.ViewHolder holder, final int position) {
        holder.textViewHead.setText(listItems.get(position).getHead());
        holder.linearLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.v(SettingsAdapter.class.getSimpleName(),"You clicked " + listItems.get(position).getHead());
                AppCompatActivity act = (AppCompatActivity) v.getContext();

                /***
                 * Still need to modularize this section
                 * Decouple switch
                 */

                String s = listItems.get(position).getHead().toString();
                switch(s) {

                    case "My Devices":
                        Toast.makeText(v.getContext(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new DeviceFragments()).commit();
                            break;
                    case "My Profile":
                        Toast.makeText(v.getContext(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                      act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragments()).commit();
                            break;
                    case "Support":
                        Toast.makeText(v.getContext(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new SupportFragment()).commit();
                        break;
                    case "Six Week Sleep Challenge":
                        Toast.makeText(v.getContext(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new SWSCFragments()).commit();
                        break;
                    case "Privacy Policy":
                        Toast.makeText(v.getContext(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new PrivacyFragments()).commit();
                        break;
                    case "Terms of Service":
                        Toast.makeText(v.getContext(), "unavailable now " + s, Toast.LENGTH_SHORT).show();
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new ToSFragments()).commit();
                        break;

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
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
