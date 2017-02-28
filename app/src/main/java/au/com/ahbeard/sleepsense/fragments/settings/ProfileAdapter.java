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


import java.util.List;

import static au.com.ahbeard.sleepsense.R.layout.my_profile_view;
import au.com.ahbeard.sleepsense.R;

/**
 * Created by Sabbib on 28/02/2017.
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>{

    List<ProfileList> listItems;
    Context context;

    public ProfileAdapter(List<ProfileList> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(
                        parent.getContext())
                        .inflate(my_profile_view,
                                parent,
                                false));
    }

    @Override
    public void onBindViewHolder(final ProfileAdapter.ViewHolder holder, final int position) {
        holder.textViewHead.setText(listItems.get(position).getHead());
        holder.linearLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.v(ProfileAdapter.class.getSimpleName(),"You clicked " + listItems.get(position).getHead());
                AppCompatActivity act = (AppCompatActivity) v.getContext();

//                if(listItems.get(position).getHead().toString().equalsIgnoreCase("My Profile"))
//                    act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragments()).commit();


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
