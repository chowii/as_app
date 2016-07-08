package au.com.ahbeard.sleepsense.fragments;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.BaseActivity;
import au.com.ahbeard.sleepsense.activities.HelpActivity;
import au.com.ahbeard.sleepsense.activities.HomeActivity;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseCommand;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseStatusEvent;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
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
public class MassageControlFragment extends Fragment {


    @Bind(R.id.massage_button_timer)
    StyledLinearLayout mTimerButton;

    private BaseDevice mBaseDevice;

    private boolean mControlOnly;

    @OnClick(R.id.massage_button_whole_body)
    void wholeBodyMassageClicked() {
        AnalyticsService.instance().logMassageControlTouch(AnalyticsService.VALUE_COMMAND_INTENSITY);
        mBaseDevice.sendCommand(BaseCommand.wholeBody());
    }

    @OnClick(R.id.massage_button_timer)
    void timerClicked() {
        AnalyticsService.instance().logMassageControlTouch(AnalyticsService.VALUE_COMMAND_TIMER);
        mBaseDevice.sendCommand(BaseCommand.timer());
    }

    @OnClick(R.id.massage_button_head_plus)
    void headPlusClicked() {
        AnalyticsService.instance().logMassageControlTouch(AnalyticsService.VALUE_COMMAND_ADJUST_HEAD_UP);
        mBaseDevice.sendCommand(BaseCommand.headMassageIncrease());
    }

    @OnClick(R.id.massage_button_head_minus)
    void headMinusClicked() {
        AnalyticsService.instance().logMassageControlTouch(AnalyticsService.VALUE_COMMAND_ADJUST_HEAD_DOWN);
        mBaseDevice.sendCommand(BaseCommand.headMassageDecrease());
    }

    @OnClick(R.id.massage_button_foot_plus)
    void footPlusClicked() {
        AnalyticsService.instance().logMassageControlTouch(AnalyticsService.VALUE_COMMAND_ADJUST_FOOT_UP);
        mBaseDevice.sendCommand(BaseCommand.footMassageIncrease());
    }

    @OnClick(R.id.massage_button_foot_minus)
    void footMinusClicked() {
        AnalyticsService.instance().logMassageControlTouch(AnalyticsService.VALUE_COMMAND_ADJUST_FOOT_DOWN);
        mBaseDevice.sendCommand(BaseCommand.footMassageDecrease());
    }

    @Bind({R.id.massage_text_view_full_off,R.id.massage_text_view_full_low, R.id.massage_text_view_full_medium, R.id.massage_text_view_full_high})
    List<View> mIntensityTextViews;

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

        for (View view : mIntensityTextViews) {
            view.setSelected(false);
        }

        mTimerButton.setSelected(massageTimerState.getTimerLightNormalised()>0);

        mTimeTextViews.get(massageTimerState.getTimerLightNormalised()).setSelected(true);
        mIntensityTextViews.get(massageTimerState.getIntensityLightNormalised()).setSelected(true);

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
        bind(view);

        mHeaderLayout.setVisibility(mControlOnly?View.GONE:View.VISIBLE);

        mBaseDevice = SleepSenseDeviceService.instance().getBaseDevice();

        if (mBaseDevice != null) {

            mBaseDevice.connect();

            mCompositeSubscription.add(mBaseDevice.getBaseEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<BaseStatusEvent>() {
                @Override
                public void call(BaseStatusEvent baseStatusEvent) {
                    updateViews(baseStatusEvent);

                }
            }));

            mCompositeSubscription.add(SleepSenseDeviceService.instance()
                    .getBaseDevice()
                    .getChangeObservable()
                    .onBackpressureBuffer()
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
    unbind();
        ButterKnife.unbind(this);

        super.onDestroyView();
    }

    // Everything from here down should be in a superclass, but thanks to
    // ButterKnife not dealing properly with incremental builds, it's not.

    @Bind(R.id.image_view_progress_icon)
    protected ImageView mProgressImageView;

    @OnClick(R.id.image_view_help_icon)
    void onHelpClicked() {
        if ( getActivity() instanceof HomeActivity) {
            startActivity(HelpActivity.getIntent(getActivity(), "Dashboard Help", "http://sleepsense.com.au/app-faq"));
        } else if (getActivity() instanceof BaseActivity){
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
        ButterKnife.bind(this,view);
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
