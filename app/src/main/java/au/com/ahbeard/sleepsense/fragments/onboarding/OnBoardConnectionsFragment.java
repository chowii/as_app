package au.com.ahbeard.sleepsense.fragments.onboarding;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.OnBoardActivity;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnBoardConnectionsFragment extends Fragment {

    @OnClick(R.id.on_board_button_continue)
    void continueOn() {
        ((OnBoardActivity) getActivity()).successContinue();
    }

    @Bind(R.id.on_board_image_view_base_found)
    ImageView mBaseFoundImageView;

    @Bind(R.id.on_board_image_view_mattress_found)
    ImageView mMattressFoundImageView;

    @Bind(R.id.on_board_image_view_tracker_found)
    ImageView mTrackerFoundImageView;

    @Bind(R.id.on_board_text_view_heading)
    TextView mHeadingTextView;

    @Bind(R.id.on_board_text_view_text)
    TextView mTextTextView;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public static OnBoardConnectionsFragment newInstance() {
        OnBoardConnectionsFragment fragment = new OnBoardConnectionsFragment();
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
        View view = inflater.inflate(R.layout.fragment_on_board_connections, container, false);

        ButterKnife.bind(this, view);

        if ( SleepSenseDeviceService.instance().countDevices() == 0 ) {
            mHeadingTextView.setText("Sorry");
            mTextTextView.setText("I didn't find any devices.");
        } else {
            mHeadingTextView.setText("Success");
            mTextTextView.setText("This is what I found.");
        }

        mBaseFoundImageView.setImageResource(SleepSenseDeviceService.instance().hasBaseDevice() ? R.drawable.success_tick : R.drawable.failure_cross);
        mMattressFoundImageView.setImageResource(SleepSenseDeviceService.instance().hasPumpDevice() ? R.drawable.success_tick : R.drawable.failure_cross);
        mTrackerFoundImageView.setImageResource(SleepSenseDeviceService.instance().hasTrackerDevice() ? R.drawable.success_tick : R.drawable.failure_cross);

        return view;
    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.clear();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
