package au.com.ahbeard.sleepsense.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseCommand;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseStatusEvent;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MassageControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MassageControlFragment extends Fragment {



    @Bind(R.id.massage_button_timer)
    View mTimerButton;

    private BaseDevice mBaseDevice;

    @OnClick(R.id.massage_button_head_plus)
    void wholeBodyClicked() {
        mBaseDevice.sendCommand(BaseCommand.wholeBody());
    }

    @OnClick(R.id.massage_button_whole_body)
    void wholeBodyMassageClicked() {
        mBaseDevice.sendCommand(BaseCommand.wholeBody());
    }

    @OnClick(R.id.massage_button_timer)
    void timerClicked() {
        mBaseDevice.sendCommand(BaseCommand.timer());
    }

    @OnClick(R.id.massage_button_head_plus)
    void headPlusClicked() {
        mBaseDevice.sendCommand(BaseCommand.headMassageIncrease());
    }

    @OnClick(R.id.massage_button_head_minus)
    void headMinusClicked() {
        mBaseDevice.sendCommand(BaseCommand.headMassageDecrease());
    }

    @OnClick(R.id.massage_button_foot_plus)
    void footPlusClicked() {
        mBaseDevice.sendCommand(BaseCommand.footMassageIncrease());
    }

    @OnClick(R.id.massage_button_foot_minus)
    void footMinusClicked() {
        mBaseDevice.sendCommand(BaseCommand.footMassageDecrease());
    }

    @Bind({R.id.massage_text_view_10_min, R.id.massage_text_view_20_min, R.id.massage_text_view_30_min})
    List<View> mTimeTextViews;

    @Bind(R.id.progress_layout)
    View mProgressLayout;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public MassageControlFragment() {

    }

    private void updateViews(int massageTimerState) {

        mTimerButton.setSelected(false);

        for (View view: mTimeTextViews) {
            view.setSelected(false);
        }

        switch (massageTimerState) {
            case 0:
                break;
            case 1:
                mTimerButton.setSelected(true);
                mTimeTextViews.get(0).setSelected(true);
                break;
            case 2:
                mTimerButton.setSelected(true);
                mTimeTextViews.get(1).setSelected(true);
                break;
            case 3:
                mTimerButton.setSelected(true);
                mTimeTextViews.get(2).setSelected(true);
                break;
            default:

        }
    }

    /**
     * @return A new instance of fragment MassageControlFragment.
     */
    public static MassageControlFragment newInstance() {
        MassageControlFragment fragment = new MassageControlFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     * @param savedInstanceState
     */
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
        View view = inflater.inflate(R.layout.fragment_massage_control, container, false);

        ButterKnife.bind(this, view);

        mBaseDevice = SleepSenseDeviceService.instance().getBaseDevice();

        mCompositeSubscription.add(mBaseDevice.getBaseEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<BaseStatusEvent>() {
            @Override
            public void call(BaseStatusEvent baseStatusEvent) {
                if ( baseStatusEvent.isTimerLightActive() ) {
                    updateViews(baseStatusEvent.getTimerLightStatus());
                } else {
                    updateViews(0);
                }

            }
        }));

        mCompositeSubscription.add(SleepSenseDeviceService.instance().getBaseDevice().getChangeObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Device>() {
            @Override
            public void call(Device device) {
                if (device.getConnectionState() == Device.CONNECTION_STATE_CONNECTING && device.getElapsedConnectingTime() > 250) {
                    mProgressLayout.setVisibility(View.VISIBLE);
                } else {
                    mProgressLayout.setVisibility(View.GONE);
                }
            }
        }));

        return view;

    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.clear();
        ButterKnife.unbind(this);

        super.onDestroyView();
    }


}
