package au.com.ahbeard.sleepsense.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpEvent;
import au.com.ahbeard.sleepsense.model.Firmness;
import au.com.ahbeard.sleepsense.widgets.FirmnessControlView;
import au.com.ahbeard.sleepsense.widgets.StyledTextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * The pump controls, excluding left and right selection, but this control does know about left and right.
 */
public class FirmnessControlFragment extends Fragment {

    @Bind(R.id.firmness_control_firmness_control_left)
    FirmnessControlView mFirmnessControlLeftView;

    @Bind(R.id.firmness_control_firmness_control_right)
    FirmnessControlView mFirmnessControlRightView;

    @Bind(R.id.firmness_control_text_view_left)
    StyledTextView mLeftTextView;

    @Bind(R.id.firmness_control_text_view_right)
    StyledTextView mRightTextView;

    @Bind(R.id.firmness_control_text_view_firmness_left)
    TextView mFirmnessLeftTextView;

    @Bind(R.id.firmness_control_text_view_firmness_right)
    TextView mFirmnessRightTextView;

    @Bind(R.id.progress_layout)
    View mProgressLayout;

    @OnClick(R.id.firmness_control_text_view_left)
    void setLeftSideActive() {
        mLeftTextView.setDrawTopBorder(true);
        mRightTextView.setDrawTopBorder(false);
        mLeftTextView.setTextColor(getResources().getColor(R.color.controlHighlight));
        mRightTextView.setTextColor(getResources().getColor(R.color.controlNormal));
        mFirmnessControlLeftView.setVisibility(View.VISIBLE);
        mFirmnessControlRightView.setVisibility(View.GONE);
        mFirmnessLeftTextView.setVisibility(View.VISIBLE);
        mFirmnessRightTextView.setVisibility(View.GONE);

    }

    @OnClick(R.id.firmness_control_text_view_right)
    void setRightSideActive() {
        mLeftTextView.setDrawTopBorder(false);
        mRightTextView.setDrawTopBorder(true);
        mLeftTextView.setTextColor(getResources().getColor(R.color.controlNormal));
        mRightTextView.setTextColor(getResources().getColor(R.color.controlHighlight));
        mFirmnessControlLeftView.setVisibility(View.GONE);
        mFirmnessControlRightView.setVisibility(View.VISIBLE);
        mFirmnessLeftTextView.setVisibility(View.GONE);
        mFirmnessRightTextView.setVisibility(View.VISIBLE);
    }

    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    public FirmnessControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_firmness, container, false);

        ButterKnife.bind(this, view);

        setLeftSideActive();



        mFirmnessControlLeftView.setOnTargetValueSetListener(new FirmnessControlView.OnTargetValueSetListener() {
            @Override
            public void onTargetValueSet(float targetValue) {
                if ( SleepSenseDeviceService.instance().hasPumpDevice() ) {
                    SleepSenseDeviceService.instance().getPumpDevice().inflateToTarget(PumpDevice.Side.Left, Firmness.getPressureForControlValue(targetValue) );
                }
            }
        });

        mFirmnessControlRightView.setOnTargetValueSetListener(new FirmnessControlView.OnTargetValueSetListener() {
            @Override
            public void onTargetValueSet(float targetValue) {
                if ( SleepSenseDeviceService.instance().hasPumpDevice() ) {
                    SleepSenseDeviceService.instance().getPumpDevice().inflateToTarget(PumpDevice.Side.Right, Firmness.getPressureForControlValue(targetValue) );
                }
            }
        });

//        mSubscriptions.add(SleepSenseDeviceService.instance().getEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SleepSenseDeviceService.SleepSenseDeviceServiceEvent>() {
//            @Override
//            public void call(SleepSenseDeviceService.SleepSenseDeviceServiceEvent event) {
//                if ( event == SleepSenseDeviceService.SleepSenseDeviceServiceEvent.DeviceListChanged ) {
//                    connectPump();
//                }
//            }
//        }));
//
        connectPump();

        return view;

    }

    @Override
    public void onDestroyView() {
        mSubscriptions.clear();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    public static FirmnessControlFragment newInstance() {
        return new FirmnessControlFragment();
    }

    public void connectPump() {

        if ( SleepSenseDeviceService.instance().hasPumpDevice() ) {

//            int currentLeftPressure = SleepSenseDeviceService.instance().getPumpDevice().getChamberState(PumpDevice.Side.Left).getCurrentPressure();
//            int currentRightPressure = SleepSenseDeviceService.instance().getPumpDevice().getChamberState(PumpDevice.Side.Right).getCurrentPressure();
//
//            mFirmnessControlLeftView.setActualValue(Firmness.getControlValueForPressure(currentLeftPressure));
//            mFirmnessControlRightView.setActualValue(Firmness.getControlValueForPressure(culorrentRightPressure));
//
//            mFirmnessLeftTextView.setText(Firmness.getFirmnessForPressure(currentLeftPressure).getLabel());
//            mFirmnessRightTextView.setText(Firmness.getFirmnessForPressure(currentRightPressure).getLabel());

            mSubscriptions.add(SleepSenseDeviceService.instance().getPumpDevice().getPumpEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<PumpEvent>() {
                @Override
                public void call(PumpEvent pumpEvent) {

                    mFirmnessControlLeftView.setActualValue(Firmness.getControlValueForPressure(pumpEvent.getLeftPressure()));
                    mFirmnessControlRightView.setActualValue(Firmness.getControlValueForPressure(pumpEvent.getRightPressure()));

                    if (pumpEvent.getStatuses().contains(PumpEvent.PumpStatus.LeftChamberActive)&&pumpEvent.isAdjusting()) {
                        mFirmnessLeftTextView.setText("Adjusting");
                    } else {
                        mFirmnessLeftTextView.setText(Firmness.getFirmnessForPressure(pumpEvent.getLeftPressure()).getLabel());
                    }

                    if (pumpEvent.getStatuses().contains(PumpEvent.PumpStatus.RightChamberActive)&&pumpEvent.isAdjusting()) {
                        mFirmnessRightTextView.setText("Adjusting");
                    } else {
                        mFirmnessRightTextView.setText(Firmness.getFirmnessForPressure(pumpEvent.getRightPressure()).getLabel());
                    }

                }
            }));

            mSubscriptions.add(SleepSenseDeviceService.instance().getPumpDevice().getChangeObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Device>() {
                @Override
                public void call(Device device) {
                    if (device.getConnectionState() == Device.CONNECTION_STATE_CONNECTING && device.getElapsedConnectingTime() > 250) {
                        mProgressLayout.setVisibility(View.VISIBLE);
                    } else {
                        mProgressLayout.setVisibility(View.GONE);
                    }
                }
            }));

            SleepSenseDeviceService.instance().getPumpDevice().fetchStatus();
        }

    }

}
