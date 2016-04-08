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
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpEvent;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class PumpTestFragment extends Fragment {

    LogAdapter mLogAdapter = new LogAdapter();

    PumpDevice mPumpDevice;

    @OnClick(R.id.pump_test_button_connect_disconnect)
    void connectOrDisconnect() {
        if (mPumpDevice != null) {
            if (mPumpDevice.isConnected()) {
                mPumpDevice.disconnect();
            } else {
                mPumpDevice.connect();
            }
        }
    }

    @OnClick(R.id.pump_test_button_inflate)
    void inflate() {
        mPumpDevice.inflate(PumpDevice.Side.Left);
    }

    @OnClick(R.id.pump_test_button_deflate)
    void deflate() {
        mPumpDevice.deflate(PumpDevice.Side.Left);
    }

    @OnClick(R.id.pump_test_button_stop)
    void stop() {
        mPumpDevice.stop(PumpDevice.Side.Left);
    }

    @Bind(R.id.pump_test_recycler_view_log)
    RecyclerView mLogRecyclerView;

    @Bind(R.id.pump_test_button_connect_disconnect)
    Button mConnectDisconnectButton;

    @Bind(R.id.pump_test_button_inflate)
    Button mInflateButton;

    @Bind(R.id.pump_test_button_deflate)
    Button mDeflateButton;

    @Bind(R.id.pump_test_button_stop)
    Button mStopButton;

    @Bind(R.id.pump_test_button_toggle_left)
    Button mToggleLeftButton;

    @Bind(R.id.pump_test_button_toggle_right)
    Button mToggleRightButton;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public PumpTestFragment() {
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

        View view = inflater.inflate(R.layout.fragment_pump_test, container, false);

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
                       attachToPump();
                    }
                });

        attachToPump();

        return view;
    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.clear();
        super.onDestroyView();
    }

    private void updateControls(boolean isConnected) {
        mConnectDisconnectButton.setText(isConnected ? "Disconnect" : "Connect");
        mInflateButton.setEnabled(isConnected);
        mDeflateButton.setEnabled(isConnected);
        mStopButton.setEnabled(isConnected);
        mToggleLeftButton.setEnabled(isConnected);
        mToggleRightButton.setEnabled(isConnected);
    }

    public static Fragment newInstance() {
        return new PumpTestFragment();
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

    public void attachToPump() {

        mPumpDevice = SleepSenseDeviceService.instance().getPumpDevice();

        if (mPumpDevice != null) {

            mConnectDisconnectButton.setEnabled(true);
            updateControls(mPumpDevice.isConnected());

            mCompositeSubscription.add(
                    mPumpDevice.getLogObservable()
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    new Action1<String>() {
                                        @Override
                                        public void call(String message) {
                                            mLogAdapter.log(message);
                                        }
                                    }));

            mCompositeSubscription.add(mPumpDevice.getDeviceEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(
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

            mCompositeSubscription.add(mPumpDevice.getPumpEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new Action1<PumpEvent>() {
                        @Override
                        public void call(PumpEvent pumpEvent) {
                            mLogAdapter.log("PUMP EVENT: " + pumpEvent.toString());
                        }
                    }));

        } else {
            mConnectDisconnectButton.setEnabled(false);
            updateControls(false);
        }

    }

}
