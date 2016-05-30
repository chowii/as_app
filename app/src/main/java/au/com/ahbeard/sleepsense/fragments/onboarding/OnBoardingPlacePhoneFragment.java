package au.com.ahbeard.sleepsense.fragments.onboarding;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import au.com.ahbeard.sleepsense.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnBoardingPlacePhoneFragment extends Fragment {

    public static final String ARG_SIDE = "side";

    public static final String VALUE_LEFT = "LEFT";
    public static final String VALUE_RIGHT = "RIGHT";

    private String mSide;

    private OnActionListener mListener;

    @Bind(R.id.onboarding_image_view_place_phone_bed)
    ImageView mPlacePhoneBedImageView;

    @Bind(R.id.onboarding_image_view_phone)
    ImageView mPhoneImageView;

    @OnClick(R.id.on_board_button_continue)
    void continueClicked() {
        if ( mListener != null ) {
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

        if ( mPhoneImageView.getDrawable() instanceof AnimationDrawable ) {
            ((AnimationDrawable)mPhoneImageView.getDrawable()).start();
        }

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
