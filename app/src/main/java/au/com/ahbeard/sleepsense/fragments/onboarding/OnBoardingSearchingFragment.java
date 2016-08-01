package au.com.ahbeard.sleepsense.fragments.onboarding;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.NewOnBoardActivity;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 */
public class OnBoardingSearchingFragment  extends OnBoardingFragment {

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Bind(R.id.on_board_button_continue)
    Button mContinueButton;

    @Bind(R.id.on_board_searching_text_view_heading)
    TextView mHeadingTextView;

    @Bind(R.id.on_board_searching_text_view_description)
    TextView mTextTextView;

    @Bind(R.id.on_board_searching_layout_devices)
    View mDevicesLayout;

    @Bind(R.id.on_board_image_view_phone)
    ImageView mPhoneImageView;

    @Bind(R.id.on_board_searching_layout_base)
    View mBaseLayout;

    @Bind(R.id.on_board_searching_layout_mattress)
    View mMattressLayout;

    @Bind(R.id.on_board_searching_layout_tracker)
    View mTrackerLayout;

    @Bind(R.id.on_board_searching_image_view_base_state)
    ImageView mBaseFoundImageView;

    @Bind(R.id.on_board_searching_image_view_mattress_state)
    ImageView mMattressFoundImageView;

    @Bind(R.id.on_board_searching_image_view_tracker_state)
    ImageView mTrackerFoundImageView;

    @OnClick(R.id.on_board_button_continue)
    public void onContinueClicked() {
        if (mOnActionListener != null) {
            mOnActionListener.onSearchingAction();
        }
    }

    @Bind({R.id.pulse_1, R.id.pulse_2, R.id.pulse_3})
    List<ImageView> pulses;

    OnBoardingState currentState;

    public OnBoardingSearchingFragment() {
        // Required empty public constructor
    }

    public static OnBoardingSearchingFragment newInstance() {
        OnBoardingSearchingFragment fragment = new OnBoardingSearchingFragment();
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
        View view = inflater.inflate(R.layout.fragment_on_boarding_searching, container, false);

        ButterKnife.bind(this, view);

//        if (mPhoneImageView.getDrawable() instanceof AnimationDrawable) {
//            ((AnimationDrawable) mPhoneImageView.getDrawable()).start();
//        }
        final PulseAnimator.ShouldStopCallback callback = new PulseAnimator.ShouldStopCallback() {
            @Override
            public boolean shouldStop() {
                return getContext() == null || (currentState != null && currentState.state != OnBoardingState.State.Acquiring);
            }
        };

        PulseAnimator.startAnimation(pulses, getContext(), callback);
        mDevicesLayout.setVisibility(View.INVISIBLE);
        mContinueButton.setAlpha(0.0f);

        mCompositeSubscription.add(((NewOnBoardActivity) getActivity()).getOnBoardingObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<OnBoardingState>() {
            @Override
            public void call(OnBoardingState onBoardingState) {
                currentState = onBoardingState;
                if (onBoardingState.state == OnBoardingState.State.Acquiring) {
                    PulseAnimator.startAnimation(pulses, getContext(), callback);
                    mDevicesLayout.setVisibility(View.INVISIBLE);
                    mContinueButton.setAlpha(0.0f);
                } else {

                    mDevicesLayout.setVisibility(View.VISIBLE);

                    PulseAnimator.stopAnimation(pulses);

                    mBaseLayout.setVisibility(onBoardingState.requiredBase ? View.VISIBLE : View.GONE);
                    mMattressLayout.setVisibility(onBoardingState.requiredPump ? View.VISIBLE : View.GONE);
                    mTrackerLayout.setVisibility(onBoardingState.requiredTracker ? View.VISIBLE : View.GONE);

                    mBaseFoundImageView.setImageResource(onBoardingState.foundBase ? R.drawable.success_tick : R.drawable.failure_cross);
                    mMattressFoundImageView.setImageResource(onBoardingState.foundPump ? R.drawable.success_tick : R.drawable.failure_cross);
                    mTrackerFoundImageView.setImageResource(onBoardingState.foundTracker ? R.drawable.success_tick : R.drawable.failure_cross);

                    boolean errorFindingMattress = onBoardingState.requiredPump && onBoardingState.foundPump;
                    boolean errorFindingBase = onBoardingState.requiredBase && onBoardingState.foundBase;
                    boolean errorFindingTracker = onBoardingState.requiredTracker && onBoardingState.foundTracker;

                    if (onBoardingState.state == OnBoardingState.State.RequiredDevicesFound) {
                        AnalyticsService.instance().logSetupSuccessPairing();
                        mContinueButton.animate().alpha(1.0f).start();
                        mPhoneImageView.setImageResource(R.drawable.onboarding_phone_searching_success);
                        mHeadingTextView.setText(R.string.onboarding_searching_success_title);
                        mTextTextView.setText(R.string.onboarding_searching_success_description);
                        mContinueButton.setText(R.string.continue_text);
                    } else if (onBoardingState.state == OnBoardingState.State.DevicesMissingAllowRetry) {
                        AnalyticsService.instance().logSetupErrorPairing(errorFindingMattress, errorFindingBase, errorFindingTracker);
                        mPhoneImageView.setImageResource(R.drawable.onboarding_phone_worried);
                        mContinueButton.animate().alpha(1.0f).start();
                        mHeadingTextView.setText(R.string.onboarding_searching_error_title);
                        mTextTextView.setText(R.string.onboarding_searching_error_description);
                        mContinueButton.setText(R.string.try_again_text);
                    } else if (onBoardingState.state == OnBoardingState.State.DevicesMissingShowHelp) {
                        AnalyticsService.instance().logSetupErrorPairing(errorFindingMattress, errorFindingBase, errorFindingTracker);
                        mPhoneImageView.setImageResource(R.drawable.onboarding_phone_worried);
                        mContinueButton.animate().alpha(1.0f).start();
                        mHeadingTextView.setText(R.string.onboarding_searching_error_title);
                        mTextTextView.setText(R.string.onboarding_searching_error_description);
                        mContinueButton.setText(R.string.help_me_text);
                    }

                }
            }
        }));

        return view;
    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.clear();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }


    private OnActionListener mOnActionListener;

    public interface OnActionListener {
        void onSearchingAction();
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
