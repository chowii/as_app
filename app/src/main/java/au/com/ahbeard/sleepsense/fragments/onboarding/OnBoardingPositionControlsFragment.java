package au.com.ahbeard.sleepsense.fragments.onboarding;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.FirmnessControlFragment;
import au.com.ahbeard.sleepsense.fragments.HardwareControlListener;
import au.com.ahbeard.sleepsense.fragments.PositionControlFragment;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnBoardingPositionControlsFragment  extends OnBoardingFragment {

    @OnClick(R.id.on_board_button_continue)
    void continueClicked() {
        if ( mOnActionListener != null ) {
            AnalyticsService.instance().logOnboardingTouchPosition(mControlsTouched);
            mOnActionListener.onPositionControlsAction();
        }
    }

    boolean mControlsTouched = false;

    public static OnBoardingPositionControlsFragment newInstance() {
        OnBoardingPositionControlsFragment fragment = new OnBoardingPositionControlsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_on_boarding_position_controls, container, false);

        ButterKnife.bind(this,view);

        PositionControlFragment controlFragment = PositionControlFragment.newInstance(true);
        controlFragment.setHardwareControlListener(new HardwareControlListener() {
            @Override
            public void didTouchControl() {
                mControlsTouched = true;
            }
        });
        getChildFragmentManager().beginTransaction().add(R.id.on_boarding_layout_controls, controlFragment).commit();

        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private OnActionListener mOnActionListener;

    public interface OnActionListener {
        void onPositionControlsAction();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActionListener) {
            mOnActionListener = (OnActionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnActionListener = null;
    }



}
