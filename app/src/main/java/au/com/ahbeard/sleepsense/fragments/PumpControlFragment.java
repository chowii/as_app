package au.com.ahbeard.sleepsense.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.adapters.SimpleItemAnimator;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * The pump controls, excluding left and right selection, but this control does know about left and right.
 *
 */
public class PumpControlFragment extends Fragment {



    public PumpControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pump_control, container, false);

        ButterKnife.bind(this, view);

        return view;

    }

    public static PumpControlFragment newInstance() {
        return new PumpControlFragment();
    }


}
