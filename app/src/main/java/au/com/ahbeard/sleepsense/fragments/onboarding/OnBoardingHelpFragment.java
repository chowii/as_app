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
public class OnBoardingHelpFragment extends Fragment {

    private OnActionListener mOnActionListener;

    @Bind(R.id.on_board_button_try_again)
    Button mContinueButton;

    @OnClick(R.id.on_board_button_try_again)
    public void onContinueClicked() {
        if (mOnActionListener != null) {
            mOnActionListener.onRetryButtonClicked();
        }
    }

    public interface OnActionListener {
        void onRetryButtonClicked();
        void onCallButtonClicked();
    }

    public OnBoardingHelpFragment() {
        // Required empty public constructor
    }

    public static OnBoardingHelpFragment newInstance() {
        OnBoardingHelpFragment fragment = new OnBoardingHelpFragment();
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
        View view = inflater.inflate(R.layout.fragment_on_boarding_help, container, false);

        ButterKnife.bind(this,view);

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
