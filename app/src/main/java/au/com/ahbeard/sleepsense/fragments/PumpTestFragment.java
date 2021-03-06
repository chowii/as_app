package au.com.ahbeard.sleepsense.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpEvent;
import au.com.ahbeard.sleepsense.services.log.SSLog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class PumpTestFragment extends Fragment {

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

    private CompositeDisposable mCompositeSubscription = new CompositeDisposable();

    public PumpTestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pump_test, container, false);

        ButterKnife.bind(this, view);

        mConnectDisconnectButton.setEnabled(false);

        updateControls(false);

        SleepSenseDeviceService.instance().getEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Consumer<SleepSenseDeviceService.SleepSenseDeviceServiceEvent>() {
                    @Override
                    public void accept(SleepSenseDeviceService.SleepSenseDeviceServiceEvent message) {
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

            mCompositeSubscription.add(mPumpDevice.getDeviceEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new Consumer<Device.DeviceEvent>() {
                        @Override
                        public void accept(Device.DeviceEvent deviceEvent) {
                            if (deviceEvent instanceof Device.DeviceConnectedEvent) {
                                updateControls(true);
                            } else if (deviceEvent instanceof Device.DeviceDisconnectedEvent) {
                                updateControls(false);
                            }
                        }
                    }));

            mCompositeSubscription.add(mPumpDevice.getPumpEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new Consumer<PumpEvent>() {
                        @Override
                        public void accept(PumpEvent pumpEvent) {
                            SSLog.d("PUMP EVENT: " + pumpEvent.toString());
                        }
                    }));

        } else {
            mConnectDisconnectButton.setEnabled(false);
            updateControls(false);
        }

    }

}
