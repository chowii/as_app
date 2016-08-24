package au.com.ahbeard.sleepsense.fragments.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnBoardingPlacePhoneFragment  extends OnBoardingFragment{

    public static final String ARG_SIDE = "side";

    public static final String VALUE_LEFT = "LEFT";
    public static final String VALUE_RIGHT = "RIGHT";

    private String mSide;

    private OnActionListener mListener;

    @Bind(R.id.onboarding_place_phone_bed)
    LinearLayout mPlacePhoneBedLayout;

    @Bind(R.id.onboarding_image_view_phone)
    ImageView mPhoneImageView;

    @OnClick(R.id.on_board_button_continue)
    void continueClicked() {
        if ( mListener != null ) {
            mListener.onPlacePhoneContinueClicked();
        }
    }

    @Bind({R.id.pulse_1, R.id.pulse_2, R.id.pulse_3})
    List<ImageView> pulses;

    public OnBoardingPlacePhoneFragment() {
        // Required empty public constructor
    }

    public static OnBoardingPlacePhoneFragment newInstance(String side) {
        OnBoardingPlacePhoneFragment fragment = new OnBoardingPlacePhoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SIDE, side);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSide = getArguments().getString(ARG_SIDE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_on_boarding_place_phone, container, false);

        ButterKnife.bind(this,view);

        if ( !VALUE_LEFT.equals(mSide)) {
            //reverse the image
            mPlacePhoneBedLayout.animate().rotationY(180).setDuration(0).start();
        }

//        if ( mPhoneImageView.getDrawable() instanceof AnimationDrawable ) {
//            ((AnimationDrawable)mPhoneImageView.getDrawable()).start();
//        }

        PulseAnimator.startAnimation(pulses, getContext(), null);

        return view;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActionListener) {
            mListener = (OnActionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnActionListener {
        void onPlacePhoneContinueClicked();
    }
}
