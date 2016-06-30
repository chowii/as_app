package au.com.ahbeard.sleepsense.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.BaseActivity;
import au.com.ahbeard.sleepsense.activities.HelpActivity;
import au.com.ahbeard.sleepsense.activities.HomeActivity;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpEvent;
import au.com.ahbeard.sleepsense.model.Firmness;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import au.com.ahbeard.sleepsense.services.PreferenceService;
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

    @Bind(R.id.firmness_control_layout_choose_side)
    View mChooseSideLayout;

    private String mSide;
    private boolean mControlOnly;

    @OnClick(R.id.firmness_control_text_view_left)
    void setLeftSideActive() {
        mLeftTextView.setDrawBottomBorder(true);
        mRightTextView.setDrawBottomBorder(false);
        mLeftTextView.setTextColor(getResources().getColor(R.color.controlHighlight));
        mRightTextView.setTextColor(getResources().getColor(R.color.controlNormal));
        mFirmnessControlLeftView.setVisibility(View.VISIBLE);
        mFirmnessControlRightView.setVisibility(View.GONE);
        mFirmnessLeftTextView.setVisibility(View.VISIBLE);
        mFirmnessRightTextView.setVisibility(View.GONE);

    }

    @OnClick(R.id.firmness_control_text_view_right)
    void setRightSideActive() {
        mLeftTextView.setDrawBottomBorder(false);
        mRightTextView.setDrawBottomBorder(true);
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
        return newInstance(PreferenceService.instance().getSideOfBed(), false);
    }

    public static FirmnessControlFragment newInstance(String side, boolean controlOnly) {
        FirmnessControlFragment firmnessControlFragment = new FirmnessControlFragment();
        Bundle arguments = new Bundle();
        arguments.putString("side", side);
        arguments.putBoolean("controlOnly", controlOnly);
        firmnessControlFragment.setArguments(arguments);
        return firmnessControlFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSide = getArguments().getString("side", "LEFT");
            mControlOnly = getArguments().getBoolean("controlOnly", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_firmness, container, false);

        ButterKnife.bind(this, view);
        bind(view);

        if ("left".equalsIgnoreCase(mSide)) {
            setLeftSideActive();
        } else {
            setRightSideActive();
        }

        mHeaderLayout.setVisibility(mControlOnly ? View.GONE : View.VISIBLE);
        mChooseSideLayout.setVisibility(mControlOnly ? View.GONE : View.VISIBLE);

        mFirmnessControlLeftView.setOnTargetValueSetListener(new FirmnessControlView.OnTargetValueSetListener() {
            @Override
            public void onTargetValueSet(float targetValue) {
                if (SleepSenseDeviceService.instance().hasPumpDevice()) {
                    AnalyticsService.instance().logEvent(AnalyticsService.EVENT_FIRMNESS_CONTROL_TOUCH,
                            AnalyticsService.PROPERTY_SIDE, "Left",
                            AnalyticsService.PROPERTY_PREFERENCE, Firmness.getAnalyticsValueForControlValue(targetValue));
                    SleepSenseDeviceService.instance().getPumpDevice().inflateToTarget(PumpDevice.Side.Left, Firmness.getPressureForControlValue(targetValue));
                }
            }
        });

        mFirmnessControlRightView.setOnTargetValueSetListener(new FirmnessControlView.OnTargetValueSetListener() {
            @Override
            public void onTargetValueSet(float targetValue) {
                if (SleepSenseDeviceService.instance().hasPumpDevice()) {
                    AnalyticsService.instance().logEvent(AnalyticsService.EVENT_FIRMNESS_CONTROL_TOUCH,
                            AnalyticsService.PROPERTY_SIDE, "Right",
                            AnalyticsService.PROPERTY_PREFERENCE, Firmness.getAnalyticsValueForControlValue(targetValue));
                    SleepSenseDeviceService.instance().getPumpDevice().inflateToTarget(PumpDevice.Side.Right, Firmness.getPressureForControlValue(targetValue));
                }
            }
        });


        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        connectPump();
    }

    @Override
    public void onDestroyView() {
        mSubscriptions.clear();
        unbind();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    public void connectPump() {

        if (SleepSenseDeviceService.instance().hasPumpDevice()) {

            mSubscriptions.add(SleepSenseDeviceService.instance().getPumpDevice().getPumpEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<PumpEvent>() {
                @Override
                public void call(PumpEvent pumpEvent) {

                    mFirmnessControlLeftView.setActualValue(Firmness.getControlValueForPressure(pumpEvent.getLeftPressure()));
                    mFirmnessControlRightView.setActualValue(Firmness.getControlValueForPressure(pumpEvent.getRightPressure()));

                    if (pumpEvent.getStatuses().contains(PumpEvent.PumpStatus.LeftChamberActive) && pumpEvent.isAdjusting()) {
                        mFirmnessLeftTextView.setText("Adjusting");
                    } else {
                        mFirmnessLeftTextView.setText(Firmness.getFirmnessForPressure(pumpEvent.getLeftPressure()).getLabel());
                    }

                    if (pumpEvent.getStatuses().contains(PumpEvent.PumpStatus.RightChamberActive) && pumpEvent.isAdjusting()) {
                        mFirmnessRightTextView.setText("Adjusting");
                    } else {
                        mFirmnessRightTextView.setText(Firmness.getFirmnessForPressure(pumpEvent.getRightPressure()).getLabel());
                    }

                }
            }));

            mSubscriptions.add(SleepSenseDeviceService.instance().getPumpDevice().getChangeObservable().onBackpressureBuffer().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Device>() {
                @Override
                public void call(Device device) {
                    if (device.getConnectionState() == Device.CONNECTION_STATE_CONNECTING && device.getElapsedConnectingTime() > 250) {
                        startProgress();
                        mFirmnessRightTextView.setText("Connecting");
                    } else if (device.getConnectionState() == Device.CONNECTION_STATE_DISCONNECTED && device.getLastConnectionStatus() > 0) {
                        stopProgress();
                        showToast("Connection timeout", "Try again", new Action1<Void>() {
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

    // Everything from here down should be in a superclass, but thanks to
    // ButterKnife not dealing properly with incremental builds, it's not.

    @Bind(R.id.image_view_progress_icon)
    protected ImageView mProgressImageView;

    @OnClick(R.id.image_view_help_icon)
    void onHelpClicked() {
        if (getActivity() instanceof HomeActivity) {
            startActivity(HelpActivity.getIntent(getActivity(), "Dashboard Help", "http://sleepsense.com.au/app-faq"));
        } else if (getActivity() instanceof BaseActivity ){
            ((BaseActivity)getActivity()).settingsIconClicked();
        }
    }

    @Bind(R.id.controls_layout_header)
    protected View mHeaderLayout;

    @Bind(R.id.progress_layout)
    protected View mLayout;

    @Bind(R.id.progress_layout_text_view_message)
    protected TextView mMessageTextView;

    @Bind(R.id.progress_layout_text_view_action)
    protected TextView mActionTextView;

    private Action1<Void> mAction;

    @OnClick(R.id.progress_layout_text_view_action)
    protected void progressAction() {
        if (mAction != null) {
            mAction.call(null);
            mLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        stopProgress();
    }

    protected void startProgress() {
        if (mProgressImageView != null && mProgressImageView.getVisibility() == View.INVISIBLE) {
            mProgressImageView.setVisibility(View.VISIBLE);
            ((AnimationDrawable) mProgressImageView.getDrawable()).start();
        }
    }

    protected void stopProgress() {
        if (mProgressImageView != null && mProgressImageView.getVisibility() == View.VISIBLE) {
            ((AnimationDrawable) mProgressImageView.getDrawable()).stop();
            mProgressImageView.setVisibility(View.INVISIBLE);
        }
    }

    protected void bind(View view) {
        ButterKnife.bind(this, view);
    }

    protected void unbind() {
        ButterKnife.unbind(this);
    }

    protected void showToast(String message, String actionText, Action1<Void> action) {

        mAction = action;

        mMessageTextView.setText(message);
        mActionTextView.setText(actionText);

        mLayout.setVisibility(View.VISIBLE);

    }


}
