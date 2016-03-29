package au.com.ahbeard.sleepsense.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpEvent;
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
 *
 */
public class PumpControlFragment extends Fragment {

    @Bind(R.id.firmness_control_firmness_control_left)
    FirmnessControlView mFirmnessControlLeftView;

    @Bind(R.id.firmness_control_firmness_control_right)
    FirmnessControlView mFirmnessControlRightView;

    @Bind(R.id.firmness_control_text_view_left)
    StyledTextView mLeftTextView;

    @Bind(R.id.firmness_control_text_view_right)
    StyledTextView mRightTextView;

    @OnClick(R.id.firmness_control_text_view_left)
    void leftClicked() {
        mLeftTextView.setDrawTopBorder(true);
        mRightTextView.setDrawTopBorder(false);
        mLeftTextView.setTextColor(getResources().getColor(R.color.firmnessTabHighlight));
        mRightTextView.setTextColor(getResources().getColor(R.color.firmnessTabNormal));
        mFirmnessControlLeftView.setVisibility(View.VISIBLE);
        mFirmnessControlRightView.setVisibility(View.GONE);
    }

    @OnClick(R.id.firmness_control_text_view_right)
    void rightClicked() {
        mLeftTextView.setDrawTopBorder(false);
        mRightTextView.setDrawTopBorder(true);
        mLeftTextView.setTextColor(getResources().getColor(R.color.firmnessTabNormal));
        mRightTextView.setTextColor(getResources().getColor(R.color.firmnessTabHighlight));
        mFirmnessControlLeftView.setVisibility(View.GONE);
        mFirmnessControlRightView.setVisibility(View.VISIBLE);
    }

    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    public PumpControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_firmness, container, false);

        ButterKnife.bind(this, view);

        leftClicked();

        mFirmnessControlLeftView.setOnTargetValueSetListener(new FirmnessControlView.OnTargetValueSetListener() {
            @Override
            public void onTargetValueSet(float targetValue) {
                if ( SleepSenseDeviceService.instance().hasPumpDevice() ) {
                    SleepSenseDeviceService.instance().getPumpDevice().inflateToTarget(PumpDevice.Side.Left, (int) (targetValue * 6));
                }
            }
        });

        mFirmnessControlRightView.setOnTargetValueSetListener(new FirmnessControlView.OnTargetValueSetListener() {
            @Override
            public void onTargetValueSet(float targetValue) {
                if ( SleepSenseDeviceService.instance().hasPumpDevice() ) {
                    SleepSenseDeviceService.instance().getPumpDevice().inflateToTarget(PumpDevice.Side.Right, (int) (targetValue * 6));
                }
            }
        });

        mSubscriptions.add(SleepSenseDeviceService.instance().getChangeEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if ( SleepSenseDeviceService.instance().hasPumpDevice() ) {
                    SleepSenseDeviceService.instance().getPumpDevice().getPumpEventObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<PumpEvent>() {
                        @Override
                        public void call(PumpEvent pumpEvent) {
                            mFirmnessControlLeftView.setActualValue((pumpEvent.getLeftPressure()-1)/6f);
                            mFirmnessControlRightView.setActualValue((pumpEvent.getRightPressure()-1)/6f);
                        }
                    });
                }
            }
        }));

        return view;

    }

    @Override
    public void onDestroyView() {
        mSubscriptions.clear();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    public static PumpControlFragment newInstance() {
        return new PumpControlFragment();
    }


}
