package au.com.ahbeard.sleepsense.fragments.onboarding;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnBoardingLieOnBedFragment  extends OnBoardingFragment {

    public static final String ARG_SIDE = "side";

    public static final String VALUE_LEFT = "LEFT";
    public static final String VALUE_RIGHT = "RIGHT";

    private String mSide;
    private OnActionListener mListener;
    private long mStartTime;

    @OnClick(R.id.on_board_button_continue)
    void continueClicked() {
        if ( mListener != null ) {
            float timePassed = (int) ((System.currentTimeMillis()-mStartTime) / 1000f);
            AnalyticsService.instance().logSetupTimeToSkipLayOnBed(timePassed);
            mListener.onLieOnBedContinueClicked();
        }
    }

    public OnBoardingLieOnBedFragment() {
        // Required empty public constructor
    }

    public static OnBoardingLieOnBedFragment newInstance(String side) {
        OnBoardingLieOnBedFragment fragment = new OnBoardingLieOnBedFragment();
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
        View view = inflater.inflate(R.layout.fragment_on_boarding_lie_on_bed, container, false);

        ButterKnife.bind(this,view);

        mStartTime = System.currentTimeMillis();

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
        void onLieOnBedContinueClicked();
    }
}
