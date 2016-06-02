package au.com.ahbeard.sleepsense.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseCommand;
import au.com.ahbeard.sleepsense.bluetooth.base.BaseStatusEvent;
import au.com.ahbeard.sleepsense.widgets.StyledButton;
import au.com.ahbeard.sleepsense.widgets.StyledImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 *
 */
public class PositionControlFragment extends Fragment {

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    private boolean mControlOnly;

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

    @Bind(R.id.progress_layout)
    View mProgressLayout;

    @Bind(R.id.controls_layout_header)
    View mHeaderLayout;

    @OnClick({R.id.position_button_rest, R.id.position_button_recline, R.id.position_button_relax, R.id.position_button_recover})
    void onClick(View clickedButton) {

        if (clickedButton.getId() == R.id.position_button_rest) {
            SleepSenseDeviceService.instance().getBaseDevice().sendCommand(BaseCommand.presetFlat());
        } else if (clickedButton.getId() == R.id.position_button_recline) {
            SleepSenseDeviceService.instance().getBaseDevice().sendCommand(BaseCommand.presetLounge());
        } else if (clickedButton.getId() == R.id.position_button_relax) {
            SleepSenseDeviceService.instance().getBaseDevice().sendCommand(BaseCommand.presetTV());
        } else if (clickedButton.getId() == R.id.position_button_recover) {
            SleepSenseDeviceService.instance().getBaseDevice().sendCommand(BaseCommand.presetZeroG());
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
        args.putBoolean("controlOnly",controlOnly);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_position_control, container, false);

        ButterKnife.bind(this, view);

        mHeaderLayout.setVisibility(mControlOnly?View.GONE:View.VISIBLE);

        if ( SleepSenseDeviceService.instance().getBaseDevice() !=null) {
            mCompositeSubscription.add(SleepSenseDeviceService.instance().getBaseDevice().getBaseEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<BaseStatusEvent>() {
                @Override
                public void call(BaseStatusEvent pumpEvent) {

                }
            }));

            mCompositeSubscription.add(SleepSenseDeviceService.instance().getBaseDevice().getChangeObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Device>() {
                @Override
                public void call(Device device) {
                    if (device.getConnectionState() == Device.CONNECTION_STATE_CONNECTING && device.getElapsedConnectingTime() > 250) {
                        mProgressLayout.setVisibility(View.VISIBLE);
                    } else {
                        mProgressLayout.setVisibility(View.GONE);
                    }
                }
            }));

            mHeadPositionUpButton.setOnPressPulseListener(new StyledImageButton.OnPressPulseListener() {
                @Override
                public void onPressPulse(StyledImageButton view) {
                    SleepSenseDeviceService.instance().getBaseDevice().sendCommand(BaseCommand.headPositionUp());
                }
            });

            mHeadPositionDownButton.setOnPressPulseListener(new StyledImageButton.OnPressPulseListener() {
                @Override
                public void onPressPulse(StyledImageButton view) {
                    SleepSenseDeviceService.instance().getBaseDevice().sendCommand(BaseCommand.headPositionDown());
                }
            });

            mFootPositionUpButton.setOnPressPulseListener(new StyledImageButton.OnPressPulseListener() {
                @Override
                public void onPressPulse(StyledImageButton view) {
                    SleepSenseDeviceService.instance().getBaseDevice().sendCommand(BaseCommand.footPositionUp());
                }
            });

            mFootPositionDownButton.setOnPressPulseListener(new StyledImageButton.OnPressPulseListener() {
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





}
