package au.com.ahbeard.sleepsense.fragments.onboarding;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.OnBoardActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnBoardInitialFragment extends Fragment {

    @OnClick(R.id.on_board_button_locate_devices)
    void acquireDevices() {
        ((OnBoardActivity)getActivity()).acquireDevices();
        mLocateDevicesButton.animate().alpha(0.0f).start();
    }

    @Bind(R.id.on_board_button_locate_devices)
    Button mLocateDevicesButton;


    public static OnBoardInitialFragment newInstance() {
        OnBoardInitialFragment fragment = new OnBoardInitialFragment();
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
        View view = inflater.inflate(R.layout.fragment_on_board_initial, container, false);

        ButterKnife.bind(this,view);

        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
