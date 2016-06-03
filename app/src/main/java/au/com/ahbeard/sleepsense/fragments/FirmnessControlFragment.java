package au.com.ahbeard.sleepsense.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
public class FirmnessControlFragment extends ControlFragment {

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

    @Bind(R.id.firmness_control_layout_choose_side)
    View mChooseSideLayout;

    private String mSide;
    private boolean mControlOnly;

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

    public static FirmnessControlFragment newInstance() {
        return newInstance("LEFT",false);
    }

    public static FirmnessControlFragment newInstance(String side, boolean controlOnly) {
        FirmnessControlFragment firmnessControlFragment = new FirmnessControlFragment();
        Bundle arguments = new Bundle();
        arguments.putString("side",side);
        arguments.putBoolean("controlOnly",controlOnly);
        firmnessControlFragment.setArguments(arguments);
        return firmnessControlFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null) {
            mSide = getArguments().getString("side","LEFT");
            mControlOnly = getArguments().getBoolean("controlOnly",false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_firmness, container, false);

        ButterKnife.bind(this, view);

        if ( "left".equalsIgnoreCase(mSide)) {
            setLeftSideActive();
        } else {
            setRightSideActive();
        }

        mHeaderLayout.setVisibility(mControlOnly?View.GONE:View.VISIBLE);
        mChooseSideLayout.setVisibility(mControlOnly?View.GONE:View.VISIBLE);

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

        connectPump();

        return view;

    }

    @Override
    public void onDestroyView() {
        mSubscriptions.clear();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    public void connectPump() {

        if ( SleepSenseDeviceService.instance().hasPumpDevice() ) {

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
                        startProgress();
                        mFirmnessRightTextView.setText("Connecting");
                    } else if ( device.getConnectionState() == Device.CONNECTION_STATE_DISCONNECTED && device.getLastConnectionStatus() > 0 ){
                        stopProgress();
                        showToast("Connection timeout","Try again",new Action1<Void>(){
                            @Override
                            public void call(Void aVoid) {
                                connectPump();
                            }

                        });
                    } else {
                        stopProgress();
                    }
                }
            }));

            SleepSenseDeviceService.instance().getPumpDevice().fetchStatus();
        }

    }

}
