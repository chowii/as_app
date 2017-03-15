package au.com.ahbeard.sleepsense.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;
import au.com.ahbeard.sleepsense.services.PreferenceService;

/**
 * Created by luisramos on 9/03/2017.
 */

public class MyProfileSettingsFragment extends BaseFragment {

    private TextView mTitleTextView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyProfileSettingsAdapter mAdapter;

    private ItemClickListener clickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MyProfileSettingsAdapter(clickListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        //This is set onResume, so it updates when coming back from the
        //setup fragments
        String sleepTarget = String.format(Locale.getDefault(), "%.0fh", PreferenceService.instance().getSleepTargetTime());
        Integer weightInt = PreferenceService.instance().getUserWeight();
        String weight = weightInt == null ? "--" : String.format(Locale.getDefault(), "%dKg", weightInt);
        Integer heightInt = PreferenceService.instance().getUserHeight();
        String height = heightInt == null ? "--" : String.format(Locale.getDefault(), "%dcm", heightInt);
        String age = PreferenceService.instance().getProfileAge();
        age = age == null ? "--" : age;
        String gender = PreferenceService.instance().getProfileSex();
        gender = gender == null ? "--" : gender;

        ArrayList<ProfileViewModel> viewModels = new ArrayList<>();
        viewModels.add(new ProfileViewModel(R.string.settings_profile_sleep_target, sleepTarget));
        viewModels.add(new ProfileViewModel(R.string.settings_profile_weight, weight));
        viewModels.add(new ProfileViewModel(R.string.settings_profile_height, height));
        viewModels.add(new ProfileViewModel(R.string.settings_profile_age, age));
        viewModels.add(new ProfileViewModel(R.string.settings_profile_gender, StringUtils.capitalize(gender)));

        mAdapter.setData(viewModels);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mTitleTextView.setText(R.string.settings_profile_title);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.clickListener = listener;
    }

    private static class ProfileViewModel {
        int titleRes;
        String desc;

        ProfileViewModel(int titleRes, String desc) {
            this.titleRes = titleRes;
            this.desc = desc;
        }
    }

    interface ItemClickListener {
        void onItemClick(int titleRes);
    }

    static class MyProfileSettingsAdapter extends RecyclerView.Adapter<MyProfileSettingsAdapter.ViewHolder> {

        List<ProfileViewModel> data;
        ItemClickListener listener;

        MyProfileSettingsAdapter(ItemClickListener listener) {
            super();
            data = new ArrayList<>();
            this.listener = listener;
        }

        public void setData(List<ProfileViewModel> data) {
            this.data = data;
            notifyDataSetChanged();
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
        public int getItemCount() {
            return data.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final ProfileViewModel viewModel = data.get(position);
            holder.titleTextView.setText(viewModel.titleRes);
            holder.descTextView.setText(viewModel.desc);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(viewModel.titleRes);
                    }
                }
            });
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            TextView titleTextView;
            TextView descTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
                descTextView = (TextView) itemView.findViewById(R.id.descTextView);
            }
        }
    }
}
