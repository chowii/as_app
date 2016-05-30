package au.com.ahbeard.sleepsense.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.HomeActivity;
import au.com.ahbeard.sleepsense.services.SleepService;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends Fragment {

    @OnClick(R.id.debug_button_power_management)
    void powerManagement() {
        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        startActivity(intent);
    }


    @OnClick(R.id.debug_button_clear)
    void clear() {
        ((HomeActivity)getActivity()).clearDevices();
    }

    @OnClick(R.id.debug_button_new_onboarding)
    void newOnboarding() {
        ((HomeActivity)getActivity()).doOnboarding();
    }

    @OnClick(R.id.sleep_tracking_button_batch_analysis)
    void onRunBatchAnalysis() {
        SleepService.instance().runBatchAnalysis();
    }

    @OnClick(R.id.debug_button_generate_fake_data)
    void generateFakeData() {
        Schedulers.io().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                SleepService.instance().generateFakeData(Calendar.getInstance(),60);
            }
        });
    }

    public MoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
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
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

}
