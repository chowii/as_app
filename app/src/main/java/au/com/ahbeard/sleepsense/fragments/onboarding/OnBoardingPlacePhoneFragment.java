package au.com.ahbeard.sleepsense.fragments.onboarding;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.ArrayList;
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

    @Bind(R.id.onboarding_image_view_place_phone_bed)
    ImageView mPlacePhoneBedImageView;

    @Bind(R.id.onboarding_image_view_phone)
    ImageView mPhoneImageView;

    private long mStartTime;

    @OnClick(R.id.on_board_button_continue)
    void continueClicked() {
        if ( mListener != null ) {
            AnalyticsService.instance().logEvent(AnalyticsService.EVENT_SETUP_SETUP_TIME_TO_SKIP_LAY_ON_BED,
                    AnalyticsService.PROPERTY_TIME_TO_SKIP_SECONDS,(float)(SystemClock.currentThreadTimeMillis()-mStartTime)/1000f);

            mListener.onPlacePhoneContinueClicked();
        }
    }

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

        if ( VALUE_LEFT.equals(mSide)) {
            mPlacePhoneBedImageView.setImageResource(R.drawable.onboarding_background_place_phone_left);
        } else {
            mPlacePhoneBedImageView.setImageResource(R.drawable.onboarding_background_place_phone_right);
        }

//        if ( mPhoneImageView.getDrawable() instanceof AnimationDrawable ) {
//            ((AnimationDrawable)mPhoneImageView.getDrawable()).start();
//        }

        startPulseAnimation();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mStartTime = System.currentTimeMillis();
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

    @Bind({R.id.pulse_1, R.id.pulse_2, R.id.pulse_3})
    List<ImageView> pulses;

    private void startPulseAnimation() {
        long animationDelay = 0;
        for (final ImageView imageView : pulses) {
            // Delay each animation by 200ms
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation pulse = AnimationUtils.loadAnimation(getContext(), R.anim.pulse);
                    pulse.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    imageView.startAnimation(pulse);
                }
            }, animationDelay);
            animationDelay += 400;
        }
    }
}
