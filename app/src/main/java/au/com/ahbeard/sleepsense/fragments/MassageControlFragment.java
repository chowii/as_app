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
import au.com.ahbeard.sleepsense.widgets.StyledLinearLayout;
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
public class MassageControlFragment extends ControlFragment {


    @Bind(R.id.massage_button_timer)
    StyledLinearLayout mTimerButton;

    private BaseDevice mBaseDevice;

    private boolean mControlOnly;

    @OnClick(R.id.massage_button_whole_body)
    void wholeBodyMassageClicked() {
        mBaseDevice.sendCommand(BaseCommand.wholeBody());
    }

    @OnClick(R.id.massage_button_timer)
    void timerClicked() {
        mBaseDevice.sendCommand(BaseCommand.timer());
    }

    @OnClick(R.id.massage_button_stop)
    void stopClicked() {
        mBaseDevice.massageStop();
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

    @Bind({R.id.massage_text_view_off,R.id.massage_text_view_10_min, R.id.massage_text_view_20_min, R.id.massage_text_view_30_min})
    List<View> mTimeTextViews;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public MassageControlFragment() {

    }

    private void updateViews(BaseStatusEvent massageTimerState) {

        mTimerButton.setSelected(false);

        for (View view : mTimeTextViews) {
            view.setSelected(false);
        }

        mTimerButton.setSelected(true);
        mTimeTextViews.get(massageTimerState.getTimerLightStatus()).setSelected(true);

    }

    public static MassageControlFragment newInstance() {
        return newInstance(false);
    }
    /**
     * @return A new instance of fragment MassageControlFragment.
     */
    public static MassageControlFragment newInstance(boolean controlOnly) {
        MassageControlFragment fragment = new MassageControlFragment();
        Bundle args = new Bundle();
        args.putBoolean("controlOnly",controlOnly);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mControlOnly = getArguments().getBoolean("controlOnly");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_massage_control, container, false);

        ButterKnife.bind(this, view);

        mHeaderLayout.setVisibility(mControlOnly?View.GONE:View.VISIBLE);

        mBaseDevice = SleepSenseDeviceService.instance().getBaseDevice();

        if (mBaseDevice != null) {

            mCompositeSubscription.add(mBaseDevice.getBaseEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<BaseStatusEvent>() {
                @Override
                public void call(BaseStatusEvent baseStatusEvent) {
                    updateViews(baseStatusEvent);

                }
            }));

            mCompositeSubscription.add(SleepSenseDeviceService.instance()
                    .getBaseDevice()
                    .getChangeObservable()
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Device>() {
                        @Override
                        public void call(Device device) {
                            if (device.getConnectionState() == Device.CONNECTION_STATE_CONNECTING && device.getElapsedConnectingTime() > 250) {
                                startProgress();
                            } else if ( device.getConnectionState() == Device.CONNECTION_STATE_DISCONNECTED && device.getLastConnectionStatus() > 0 ){
                                stopProgress();
                                showToast("Connection timeout","Try again",new Action1<Void>(){
                                    @Override
                                    public void call(Void aVoid) {
                                        SleepSenseDeviceService.instance().getBaseDevice().connect();
                                    }

                                });
                            } else {
                                stopProgress();
                            }
                        }
                    }));

        }

        return view;

    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.clear();

        ButterKnife.unbind(this);

        super.onDestroyView();
    }


}
