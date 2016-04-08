package au.com.ahbeard.sleepsense.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.adapters.SimpleItemAnimator;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseCommand;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseTestFragment extends Fragment {

    LogAdapter mLogAdapter = new LogAdapter();

    BaseDevice mBaseDevice;

    @OnClick(R.id.base_test_button_connect_disconnect)
    void connectOrDisconnect() {
        if (mBaseDevice != null) {
            if (mBaseDevice.isConnected()) {
                mBaseDevice.disconnect();
            } else {
                mBaseDevice.connect();
            }
        }
    }

    @Bind(R.id.base_test_recycler_view_log)
    RecyclerView mLogRecyclerView;

    @Bind(R.id.base_test_button_connect_disconnect)
    Button mConnectDisconnectButton;

    @Bind(R.id.base_test_button_head_up)
    Button mHeadUpButton;

    @Bind(R.id.base_test_button_head_down)
    Button mHeadDownButton;

    @Bind(R.id.base_test_button_foot_up)
    Button mFootUpButton;

    @Bind(R.id.base_test_button_foot_down)
    Button mFootDownButton;

    @Bind(R.id.base_test_button_head_increase)
    Button mHeadIncrease;

    @Bind(R.id.base_test_button_head_decrease)
    Button mHeadDecrease;

    @Bind(R.id.base_test_button_foot_increase)
    Button mFootIncrease;

    @Bind(R.id.base_test_button_foot_decrease)
    Button mFootDecrease;

    @Bind(R.id.base_test_button_motor_stop)
    Button mMotorStopButton;

    @Bind(R.id.base_test_button_preset_flat)
    Button mPresetFlatButton;

    @Bind(R.id.base_test_button_preset_lounge)
    Button mPresetLoungeButton;

    @Bind(R.id.base_test_button_preset_tv)
    Button mPresetTVButton;

    @Bind(R.id.base_test_button_preset_zero_g)
    Button mPresetZeroGButton;

    @Bind(R.id.base_test_button_whole_body)
    Button mWholeBodyButton;

    @Bind(R.id.base_test_button_timer)
    Button mTimerButton;

    @OnClick(R.id.base_test_button_head_up)
    void headUpClicked() {
        mBaseDevice.sendCommand(BaseCommand.headPositionUp());
    }

    @OnClick(R.id.base_test_button_head_down)
    void headDownClicked() {
        mBaseDevice.sendCommand(BaseCommand.headPositionDown());
    }

    @OnClick(R.id.base_test_button_foot_up)
    void footUpClicked() {
        mBaseDevice.sendCommand(BaseCommand.footPositionUp());
    }

    @OnClick(R.id.base_test_button_foot_down)
    void footDownClicked() {
        mBaseDevice.sendCommand(BaseCommand.footPositionDown());
    }

    @OnClick(R.id.base_test_button_head_increase)
    void headIncreaseClicked() {
        mBaseDevice.sendCommand(BaseCommand.headMassageIncrease());
    }

    @OnClick(R.id.base_test_button_head_decrease)
    void headDecreaseClicked() {
        mBaseDevice.sendCommand(BaseCommand.headMassageDecrease());
    }

    @OnClick(R.id.base_test_button_foot_increase)
    void footIncreaseClicked() {
        mBaseDevice.sendCommand(BaseCommand.footMassageIncrease());
    }

    @OnClick(R.id.base_test_button_foot_decrease)
    void footDecreaseClicked() {
        mBaseDevice.sendCommand(BaseCommand.footMassageDecrease());
    }

    @OnClick(R.id.base_test_button_motor_stop)
    void motorStopClicked() {
        mBaseDevice.sendCommand(BaseCommand.motorStop());
    }

    @OnClick(R.id.base_test_button_preset_flat)
    void presetFlatClicked() {
        mBaseDevice.sendCommand(BaseCommand.presetFlat());
    }

    @OnClick(R.id.base_test_button_preset_lounge)
    void presetLoungeClicked() {
        mBaseDevice.sendCommand(BaseCommand.presetLounge());
    }

    @OnClick(R.id.base_test_button_preset_tv)
    void presetTVClicked() {
        mBaseDevice.sendCommand(BaseCommand.presetTV());
    }

    @OnClick(R.id.base_test_button_preset_zero_g)
    void presetZeroGClicked() {
        mBaseDevice.sendCommand(BaseCommand.presetZeroG());
    }

    @OnClick(R.id.base_test_button_whole_body)
    void wholeBodyClicked() {
        mBaseDevice.sendCommand(BaseCommand.wholeBody());
    }

    @OnClick(R.id.base_test_button_timer)
    void timerClicked() {
        mBaseDevice.sendCommand(BaseCommand.timer());
    }


    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public BaseTestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SleepSenseDeviceService.instance().getLogObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String message) {
                        mLogAdapter.log(message);
                    }
                });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_base_test, container, false);

        ButterKnife.bind(this, view);

        mLogRecyclerView.setAdapter(mLogAdapter);
        mLogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLogRecyclerView.setItemAnimator(new SimpleItemAnimator());

        mConnectDisconnectButton.setEnabled(false);

        updateControls(false);

        SleepSenseDeviceService.instance().getEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Action1<SleepSenseDeviceService.SleepSenseDeviceServiceEvent>() {
                    @Override
                    public void call(SleepSenseDeviceService.SleepSenseDeviceServiceEvent message) {
                       attachToBase();
                    }
                });

        attachToBase();

        return view;
    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.clear();
        super.onDestroyView();
    }

    private void updateControls(boolean isConnected) {
        mConnectDisconnectButton.setText(isConnected ? "Disconnect" : "Connect");
        mHeadUpButton.setEnabled(isConnected);
        mHeadDownButton.setEnabled(isConnected);
        mFootUpButton.setEnabled(isConnected);
        mFootDownButton.setEnabled(isConnected);
        mHeadIncrease.setEnabled(isConnected);
        mHeadDecrease.setEnabled(isConnected);
        mFootIncrease.setEnabled(isConnected);
        mFootDecrease.setEnabled(isConnected);
        mMotorStopButton.setEnabled(isConnected);
        mPresetFlatButton.setEnabled(isConnected);
        mPresetLoungeButton.setEnabled(isConnected);
        mPresetTVButton.setEnabled(isConnected);
        mPresetZeroGButton.setEnabled(isConnected);
        mTimerButton.setEnabled(isConnected);
        mWholeBodyButton.setEnabled(isConnected);
    }

    public static Fragment newInstance() {
        return new BaseTestFragment();
    }

    /**
     * Log adapter... manages the log entries.
     */
    public class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {

        int mMaxLogItems = 128;
        List<String> mLogItems = new ArrayList<>();

        public LogAdapter() {
        }

        public LogAdapter(int maxLogItems) {
            mMaxLogItems = maxLogItems;
        }

        @Override
        public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LogViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_log, parent, false));
        }

        @Override
        public void onBindViewHolder(LogViewHolder holder, int position) {
            holder.bind(mLogItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mLogItems.size();
        }

        public void clear() {
            mLogItems.clear();
            notifyDataSetChanged();
        }

        public void log(String message) {
            if (mLogItems.size() >= mMaxLogItems) {
                mLogItems.remove(0);
            }
            mLogItems.add(message);
            notifyDataSetChanged();
        }
    }

    public class LogViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.log_text_view)
        TextView mDebugTextView;

        public LogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(String message) {
            mDebugTextView.setText(message);
        }
    }

    public void attachToBase() {

        mBaseDevice = SleepSenseDeviceService.instance().getBaseDevice();

        if (mBaseDevice != null) {

            mConnectDisconnectButton.setEnabled(true);
            updateControls(mBaseDevice.isConnected());

            mCompositeSubscription.add(
                    mBaseDevice.getLogObservable()
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    new Action1<String>() {
                                        @Override
                                        public void call(String message) {
                                            mLogAdapter.log(message);
                                        }
                                    }));

            mCompositeSubscription.add(mBaseDevice.getDeviceEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new Action1<Device.DeviceEvent>() {
                        @Override
                        public void call(Device.DeviceEvent deviceEvent) {
                            if (deviceEvent instanceof Device.DeviceConnectedEvent) {
                                updateControls(true);
                            } else if (deviceEvent instanceof Device.DeviceDisconnectedEvent) {
                                updateControls(false);
                            }
                        }
                    }));


        } else {
            mConnectDisconnectButton.setEnabled(false);
            updateControls(false);
        }

    }

}
