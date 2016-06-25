package au.com.ahbeard.sleepsense.fragments;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.apache.commons.lang3.StringUtils;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.BluetoothService;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.tracker.LiveFeedbackTrackerDevice;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import au.com.ahbeard.sleepsense.widgets.LiveFeedbackGraph;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by neal on 22/06/2016.
 */

public class LiveFeedbackFragment extends Fragment {

    private LiveFeedbackTrackerDevice mLeftTrackingDevice;
    private LiveFeedbackTrackerDevice mRightTrackerDevice;

    @Bind(R.id.live_feedback_graph)
    LiveFeedbackGraph mLiveFeedbackGraph;

    @Bind(R.id.live_feedback_button_start_stop)
    Button mStartStopButton;

    @OnClick(R.id.live_feedback_button_start_stop)
    void onClickStartStop() {

        if ( mLeftTrackingDevice != null ) {
            mLeftTrackingDevice.startSession().observeOn(AndroidSchedulers.mainThread()).onBackpressureBuffer().subscribe(new Observer<byte[]>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(byte[] bytes) {
                    mLiveFeedbackGraph.addToLeftChannel(bytesToInts(bytes));
                }
            });
        }
        if ( mRightTrackerDevice != null ) {
            mRightTrackerDevice.startSession().observeOn(AndroidSchedulers.mainThread()).onBackpressureBuffer().subscribe(new Observer<byte[]>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(byte[] bytes) {
                    mLiveFeedbackGraph.addToRightChannel(bytesToInts(bytes));
                }
            });
        }

    }


    public LiveFeedbackFragment() {
        // Required empty public constructor
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

        if ( PreferenceService.instance().getTrackerDeviceAddress() != null ) {
            mRightTrackerDevice = new LiveFeedbackTrackerDevice();
            mRightTrackerDevice.link(context, BluetoothService.instance().createDeviceFromAddress(PreferenceService.instance().getTrackerDeviceAddress()));
        }

        if ( PreferenceService.instance().getAltTrackerDeviceAddress() != null ) {
            mLeftTrackingDevice = new LiveFeedbackTrackerDevice();
            mLeftTrackingDevice.link(context, BluetoothService.instance().createDeviceFromAddress(PreferenceService.instance().getAltTrackerDeviceAddress()));
        }

    }

    private static int[] bytesToInts(byte[] bytes) {

        int[] ints = new int[bytes.length / 2-1];

        for(int i=2; i < bytes.length; i+=2) {
            ints[i/2-1] = ( bytes[i] & 0xff ) + ( ( bytes[i+1] & 0xff ) << 8 );
            ints[i/2-1] -= 32768;
        }

        // Log.d("DEBUG",""+StringUtils.join(ints,' '));

        return ints;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_feedback, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {

        if (mRightTrackerDevice!=null) {
            mRightTrackerDevice.disconnect();
            mRightTrackerDevice = null;
        }

        if ( mLeftTrackingDevice!=null) {
            mLeftTrackingDevice.disconnect();
            mLeftTrackingDevice = null;
        }

        super.onDetach();

    }
}
