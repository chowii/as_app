package au.com.ahbeard.sleepsense.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.SleepTrackingActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardNoSleepsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardNoSleepsFragment extends Fragment {

    @OnClick(R.id.dashboard_fab_start_sleep)
    void onStartSleepClicked() {
        Intent intent = new Intent(getActivity(), SleepTrackingActivity.class);
        startActivity(intent);
    }

    public DashboardNoSleepsFragment() {
        // Required empty public constructor
    }

    public static DashboardNoSleepsFragment newInstance() {
        DashboardNoSleepsFragment fragment = new DashboardNoSleepsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_no_sleeps, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

}
