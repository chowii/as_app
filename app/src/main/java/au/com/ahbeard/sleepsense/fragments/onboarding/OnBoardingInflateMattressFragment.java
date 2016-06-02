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
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.NewOnBoardActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 */
public class OnBoardingInflateMattressFragment extends Fragment {

    private OnActionListener mOnActionListener;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Bind(R.id.on_board_button_continue)
    Button mContinueButton;

    @OnClick(R.id.on_board_button_continue)
    public void onContinueClicked() {
        if (mOnActionListener != null) {
            mOnActionListener.onInflateContinueClicked();
        }
    }

    @Bind(R.id.onboarding_image_filling_mattress_happy)
    ImageView mMattressHappyImageView;

    @Bind(R.id.onboarding_image_filling_mattress_in)
    ImageView mMattressInImageView;

    @Bind(R.id.onboarding_image_filling_mattress_out)
    ImageView mMattressOutImageView;

    @Bind(R.id.on_board_inflate_mattress_text_view_heading)
    TextView mHeadingTextView;

    @Bind(R.id.on_board_inflate_mattress_text_view_description)
    TextView mDescriptionTextView;

    public interface OnActionListener {
        void onInflateContinueClicked();
    }

    public OnBoardingInflateMattressFragment() {
        // Required empty public constructor
    }

    public static OnBoardingInflateMattressFragment newInstance() {
        OnBoardingInflateMattressFragment fragment = new OnBoardingInflateMattressFragment();
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
        View view = inflater.inflate(R.layout.fragment_on_boarding_inflate_mattress, container, false);

        ButterKnife.bind(this,view);

        mContinueButton.setAlpha(0.0f);

        mCompositeSubscription.add(((NewOnBoardActivity)getActivity()).getOnBoardingObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<OnBoardingState>() {
            @Override
            public void call(OnBoardingState onBoardingState) {
                if ( onBoardingState.state == OnBoardingState.State.Inflating ) {
                    mMattressHappyImageView.setAlpha(0.0f);
                    mMattressInImageView.setAlpha(1.0f);
                    mMattressOutImageView.setAlpha(0.0f);
                    mHeadingTextView.setText("I'm filling up");
                    mDescriptionTextView.setText("Please wait a few moments longer");
                } else if ( onBoardingState.state == OnBoardingState.State.InflationComplete ) {
                    mMattressHappyImageView.setAlpha(1.0f);
                    mMattressInImageView.setAlpha(0.0f);
                    mMattressOutImageView.setAlpha(0.0f);
                    mHeadingTextView.setText("All done!");
                    mDescriptionTextView.setText("Now you're ready to adjust your comfort levels");
                    mContinueButton.setText("Continue");
                    mContinueButton.setAlpha(1.0f);
                } else if ( onBoardingState.state == OnBoardingState.State.InflationError ) {
                    mMattressHappyImageView.setAlpha(1.0f);
                    mMattressInImageView.setAlpha(0.0f);
                    mMattressOutImageView.setAlpha(0.0f);
                    mHeadingTextView.setText("Hmmm");
                    mDescriptionTextView.setText("We seem to have difficulties filling your mattress");
                    mContinueButton.setText("Get help");
                    mContinueButton.setAlpha(1.0f);
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
