package au.com.ahbeard.sleepsense.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.BaseActivity;
import au.com.ahbeard.sleepsense.bluetooth.BluetoothService;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.tracker.LiveFeedbackTrackerDevice;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.widgets.LiveFeedbackGraph;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by neal on 22/06/2016.
 */

public class LiveFeedbackFragment extends Fragment {

    private LiveFeedbackTrackerDevice mLeftTrackingDevice;
    private LiveFeedbackTrackerDevice mRightTrackingDevice;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Bind(R.id.live_feedback_graph)
    LiveFeedbackGraph mLiveFeedbackGraph;

    @Bind(R.id.live_feedback_button_start)
    Button mStartButton;

    @Nullable
    @OnClick(R.id.live_feedback_image_view_help)
    void onClickHelp() {
        ((BaseActivity)getActivity()).startHelpActivity();
    }

    @OnClick(R.id.live_feedback_button_start)
    void onClickStart() {

        // Yes this is a hack.
        if ( "Stop".equalsIgnoreCase(mStartButton.getText().toString())) {
            onClickStop();
            return;
        }

        if (mLeftTrackingDevice != null && !mLeftTrackingDevice.isTracking()) {
            mCompositeSubscription.add(mLeftTrackingDevice.startSession()
                    .observeOn(AndroidSchedulers.mainThread()).onBackpressureDrop().subscribe(new Observer<byte[]>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(byte[] bytes) {
                            mLiveFeedbackGraph.addToLeftChannel(bytesToAverage(bytes));
                        }
                    }));
        }

        if (mRightTrackingDevice != null && !mRightTrackingDevice.isTracking()) {
            mCompositeSubscription.add(mRightTrackingDevice.startSession()
                    .observeOn(AndroidSchedulers.mainThread()).onBackpressureDrop().subscribe(new Observer<byte[]>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(byte[] bytes) {
                            mLiveFeedbackGraph.addToRightChannel(bytesToAverage(bytes));
                        }
                    }));
        }

    }

    @Bind(R.id.live_feedback_button_stop)
    Button mStopButton;

    @OnClick(R.id.live_feedback_button_stop)
    void onClickStop() {

        if (mLeftTrackingDevice != null && mLeftTrackingDevice.isTracking()) {
            mLeftTrackingDevice.stopSession();
        }

        if (mRightTrackingDevice != null && mRightTrackingDevice.isTracking()) {
            mRightTrackingDevice.stopSession();
        }
    }

    public static LiveFeedbackFragment newInstance() {
        LiveFeedbackFragment fragment = new LiveFeedbackFragment();
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
    public void onAttach(Context context) {

        super.onAttach(context);

        if (PreferenceService.instance().getTrackerDeviceAddress() != null) {
            mRightTrackingDevice = new LiveFeedbackTrackerDevice();
            mRightTrackingDevice.link(context, BluetoothService.instance().createDeviceFromAddress(PreferenceService.instance().getTrackerDeviceAddress()));
        }

        if (PreferenceService.instance().getAltTrackerDeviceAddress() != null) {
            mLeftTrackingDevice = new LiveFeedbackTrackerDevice();
            mLeftTrackingDevice.link(context, BluetoothService.instance().createDeviceFromAddress(PreferenceService.instance().getAltTrackerDeviceAddress()));
        }

    }

    private static int bytesToAverage(byte[] bytes) {

        int total = 0;

        for (int i = 2; i < bytes.length; i += 2) {
            total += (bytes[i] & 0xff) + ((bytes[i + 1] & 0xff) << 8);
        }

        return total / (bytes.length - 2) * 2 - 32768;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_feedback, container, false);

        ButterKnife.bind(this, view);

        if ( mLeftTrackingDevice != null ) {
            mCompositeSubscription.add(mLeftTrackingDevice.getDeviceEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Device.DeviceEvent>() {
                @Override
                public void call(Device.DeviceEvent deviceEvent) {
                    setButtons();
                }
            }));
        }

        if ( mRightTrackingDevice != null ) {
            mCompositeSubscription.add(mRightTrackingDevice.getDeviceEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Device.DeviceEvent>() {
                @Override
                public void call(Device.DeviceEvent deviceEvent) {
                    setButtons();
                }
            }));
        }

        setButtons();

        return view;
    }

    private void setButtons() {

        boolean startEnabled = mLeftTrackingDevice!=null && mLeftTrackingDevice.getConnectionState() == Device.CONNECTION_STATE_DISCONNECTED ||
                mRightTrackingDevice!=null && mRightTrackingDevice.getConnectionState() ==  Device.CONNECTION_STATE_DISCONNECTED;

        boolean stopEnabled = mLeftTrackingDevice!=null && mLeftTrackingDevice.getConnectionState() == Device.CONNECTION_STATE_CONNECTED ||
                mRightTrackingDevice!=null && mRightTrackingDevice.getConnectionState() == Device.CONNECTION_STATE_CONNECTED;

        mStartButton.setEnabled(startEnabled||stopEnabled);
        mStartButton.setText(stopEnabled?"Stop":"Start");

    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.unsubscribe();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {

        if (mRightTrackingDevice != null) {
            mRightTrackingDevice.disconnect();
            mRightTrackingDevice = null;
        }

        if (mLeftTrackingDevice != null) {
            mLeftTrackingDevice.disconnect();
            mLeftTrackingDevice = null;
        }

        super.onDetach();

    }
}
