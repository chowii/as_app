package au.com.ahbeard.sleepsense.fragments;

import android.content.Context;
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
import au.com.ahbeard.sleepsense.widgets.StyledButton;
import au.com.ahbeard.sleepsense.widgets.StyledImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 *
 */
public class PositionControlFragment extends Fragment {

    private CompositeDisposable mCompositeSubscription = new CompositeDisposable();

    private boolean mControlOnly;

    private HardwareControlListener listener;

    @Bind({R.id.position_button_rest, R.id.position_button_recline, R.id.position_button_relax, R.id.position_button_recover})
    List<StyledButton> mPositionButtons;

    @Bind(R.id.position_button_head_position_up)
    StyledImageButton mHeadPositionUpButton;
    @Bind(R.id.position_button_head_position_down)
    StyledImageButton mHeadPositionDownButton;
    @Bind(R.id.position_button_foot_position_up)
    StyledImageButton mFootPositionUpButton;
    @Bind(R.id.position_button_foot_position_down)
    StyledImageButton mFootPositionDownButton;

    @OnClick({R.id.position_button_rest, R.id.position_button_recline, R.id.position_button_relax, R.id.position_button_recover})
    void onClick(View clickedButton) {

        callHardwareControlListener();

        BaseDevice baseDevice = SleepSenseDeviceService.instance().getBaseDevice();
        if (baseDevice != null) {
            if (clickedButton.getId() == R.id.position_button_rest) {
                AnalyticsService.instance().logPositionControlTouch(AnalyticsService.VALUE_COMMAND_PRESET_REST);
                baseDevice.sendCommand(BaseCommand.presetFlat());
            } else if (clickedButton.getId() == R.id.position_button_recline) {
                AnalyticsService.instance().logPositionControlTouch(AnalyticsService.VALUE_COMMAND_PRESET_RECLINE);
                baseDevice.sendCommand(BaseCommand.presetLounge());
            } else if (clickedButton.getId() == R.id.position_button_relax) {
                AnalyticsService.instance().logPositionControlTouch(AnalyticsService.VALUE_COMMAND_PRESET_RELAX);
                baseDevice.sendCommand(BaseCommand.presetTV());
            } else if (clickedButton.getId() == R.id.position_button_recover) {
                AnalyticsService.instance().logPositionControlTouch(AnalyticsService.VALUE_COMMAND_PRESET_RECOVER);
                baseDevice.sendCommand(BaseCommand.presetZeroG());
            }
        }

        /*
        for ( StyledButton button : mPositionButtons ) {
            if ( button == clickedButton ) {
                button.setSelected(true);
            } else {
                button.setSelected(false);
            }
        }
        */
    }

    public PositionControlFragment() {
        // Required empty public constructor
    }

    public static PositionControlFragment newInstance() {
        return newInstance(false);
    }

    public static PositionControlFragment newInstance(boolean controlOnly) {
        PositionControlFragment fragment = new PositionControlFragment();
        Bundle args = new Bundle();
        args.putBoolean("controlOnly", controlOnly);
        fragment.setArguments(args);
        return fragment;
    }

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

        View view = inflater.inflate(R.layout.fragment_position_control, container, false);

        ButterKnife.bind(this, view);
        bind(view);

        mHeaderLayout.setVisibility(mControlOnly ? View.GONE : View.VISIBLE);

        if (SleepSenseDeviceService.instance().getBaseDevice() != null) {
            mCompositeSubscription.add(SleepSenseDeviceService.instance().getBaseDevice().getBaseEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BaseStatusEvent>() {
                @Override
                public void accept(BaseStatusEvent pumpEvent) {

                }
            }));

            mCompositeSubscription.add(SleepSenseDeviceService.instance().getBaseDevice().getChangeObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Device>() {
                @Override
                public void accept(Device device) {
                    if (device.getConnectionState() == Device.CONNECTION_STATE_CONNECTING && device.getElapsedConnectingTime() > 250) {
                        startProgress();
                    } else if (device.getConnectionState() == Device.CONNECTION_STATE_DISCONNECTED && device.getLastConnectionStatus() > 0) {
                        stopProgress();
                        showToast("Connection timeout", "Try again", new Runnable() {
                            @Override
                            public void run() {
                                SleepSenseDeviceService.instance().getBaseDevice().connect();
                            }

                        });
                    } else {
                        stopProgress();
                    }


                }
            }));

            mHeadPositionUpButton.setOnPressPulseListener(new StyledImageButton.OnPressPulseListener() {
                @Override
                public void onDown(StyledImageButton view) {
                    callHardwareControlListener();
                    AnalyticsService.instance().logPositionControlTouch(AnalyticsService.VALUE_COMMAND_ADJUST_HEAD_UP);
                }

                @Override
                public void onPressPulse(StyledImageButton view) {
                    SleepSenseDeviceService.instance().getBaseDevice().sendCommand(BaseCommand.headPositionUp());
                }
            });

            mHeadPositionDownButton.setOnPressPulseListener(new StyledImageButton.OnPressPulseListener() {
                @Override
                public void onDown(StyledImageButton view) {
                    callHardwareControlListener();
                    AnalyticsService.instance().logPositionControlTouch(AnalyticsService.VALUE_COMMAND_ADJUST_HEAD_DOWN);
                }

                @Override
                public void onPressPulse(StyledImageButton view) {
                    SleepSenseDeviceService.instance().getBaseDevice().sendCommand(BaseCommand.headPositionDown());
                }
            });

            mFootPositionUpButton.setOnPressPulseListener(new StyledImageButton.OnPressPulseListener() {
                @Override
                public void onDown(StyledImageButton view) {
                    callHardwareControlListener();
                    AnalyticsService.instance().logPositionControlTouch(AnalyticsService.VALUE_COMMAND_ADJUST_FOOT_UP);
                }

                @Override
                public void onPressPulse(StyledImageButton view) {
                    SleepSenseDeviceService.instance().getBaseDevice().sendCommand(BaseCommand.footPositionUp());
                }
            });

            mFootPositionDownButton.setOnPressPulseListener(new StyledImageButton.OnPressPulseListener() {
                @Override
                public void onDown(StyledImageButton view) {
                    callHardwareControlListener();
                    AnalyticsService.instance().logPositionControlTouch(AnalyticsService.VALUE_COMMAND_ADJUST_FOOT_DOWN);
                }

                @Override
                public void onPressPulse(StyledImageButton view) {
                    SleepSenseDeviceService.instance().getBaseDevice().sendCommand(BaseCommand.footPositionDown());
                }
            });

        }

        return view;
    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.clear();
        ButterKnife.unbind(this);
        unbind();
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    private Runnable mAction;

    @OnClick(R.id.progress_layout_text_view_action)
    protected void progressAction() {
        if (mAction != null) {
            mAction.run();
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

    protected void showToast(String message, String actionText, Runnable action) {

        mAction = action;

        mMessageTextView.setText(message);
        mActionTextView.setText(actionText);

        mLayout.setVisibility(View.VISIBLE);

    }

    public void setHardwareControlListener(HardwareControlListener listener) {
        this.listener = listener;
    }

    private void callHardwareControlListener() {
        if (listener != null) {
            listener.didTouchControl();
        }
    }
}
