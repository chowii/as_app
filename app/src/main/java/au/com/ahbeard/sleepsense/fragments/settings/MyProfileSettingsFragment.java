package au.com.ahbeard.sleepsense.fragments.settings;

import android.content.Context;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import au.com.ahbeard.sleepsense.R;

/**
 * Created by luisramos on 9/03/2017.
 */

public class MyProfileSettingsFragment extends SettingsListFragment {

    @Override
    protected void setupAdapter() {
        adapter = new MyProfileSettingsAdapter(settingsList, getActivity(), 0);
    }

    private static class MyProfileSettingsAdapter extends SettingsAdapter {

        public MyProfileSettingsAdapter(List<SettingsListItem> listItems, Context context, int viewItemId) {
            super(listItems, context, viewItemId);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return R.layout.item_settings_my_profile_top;
            } else if (position == getItemCount() - 1) {
                return R.layout.item_settings_my_profile_bottom;
            } else {
                return R.layout.item_settings_my_profile_middle;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(viewType, parent, false);
            return new ViewHolder(view);
        }
    }
}
