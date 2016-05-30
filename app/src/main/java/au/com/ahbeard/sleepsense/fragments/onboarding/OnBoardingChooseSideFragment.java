package au.com.ahbeard.sleepsense.fragments.onboarding;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import au.com.ahbeard.sleepsense.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
public class OnBoardingChooseSideFragment extends Fragment {

    private OnActionListener mOnActionListener;

    private String mChosenSide;

    @Bind(R.id.onboarding_image_view_choose_left)
    ImageView mChooseLeftImageView;

    @Bind(R.id.onboarding_image_view_choose_right)
    ImageView mChooseRightImageView;

    @Bind(R.id.on_board_button_continue)
    Button mContinueButton;

    @OnClick(R.id.on_board_button_continue)
    public void onContinueClicked() {
        if (mOnActionListener != null) {
            mOnActionListener.onChooseSideContinueClicked(mChosenSide);
        }
    }

    @OnClick(R.id.onboarding_view_choose_left)
    public void leftSideClicked() {
        mChooseLeftImageView.setAlpha(1.0f);
        mChooseRightImageView.setAlpha(0.0f);
        mChosenSide="LEFT";

        if ( mContinueButton.getAlpha() < 1.0f) {
            mContinueButton.animate().alpha(1.0f).start();
        }
    }

    @OnClick(R.id.onboarding_view_choose_right)
    public void rightSideClicked() {
        mChooseLeftImageView.setAlpha(0.0f);
        mChooseRightImageView.setAlpha(1.0f);
        mChosenSide="RIGHT";

        if ( mContinueButton.getAlpha() < 1.0f) {
            mContinueButton.animate().alpha(1.0f).start();
        }
    }


    public interface OnActionListener {
        void onChooseSideContinueClicked(String side);
    }

    public OnBoardingChooseSideFragment() {
        // Required empty public constructor
    }

    public static OnBoardingChooseSideFragment newInstance() {
        OnBoardingChooseSideFragment fragment = new OnBoardingChooseSideFragment();
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
        View view = inflater.inflate(R.layout.fragment_on_boarding_choose_side, container, false);

        ButterKnife.bind(this,view);

        mContinueButton.setAlpha(0.0f);
        mChooseLeftImageView.setAlpha(0.0f);
        mChooseRightImageView.setAlpha(0.0f);

        ObjectAnimator anim = ObjectAnimator.ofFloat(mChooseLeftImageView, "alpha", 0f, 1f, 0f, 1f, 0f);
        anim.setDuration(2000);
        anim.start();



        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
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
