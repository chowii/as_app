package au.com.ahbeard.sleepsense.fragments.onboarding;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.NewOnBoardActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnBoardingItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnBoardingItemsFragment  extends OnBoardingFragment {

    private OnActionListener mOnActionListener;

    private boolean mHasBase;
    private boolean mHasPump;
    private boolean mHasTracker;

    private int mNoOfTimesHeaderClicked;
    private boolean mCanChangeHasMattress;

    private CompositeDisposable mCompositeSubscription = new CompositeDisposable();

    public interface OnActionListener {
        void onItemsContinueClicked(boolean hasMattress, boolean hasTracker, boolean hasBase);
    }

    @Bind(R.id.on_board_button_mattress)
    ImageView mMattressImageView;

    @Bind(R.id.onboarding_image_view_bed_with_headboard)
    ImageView mMattressItemImageView;

    @Bind(R.id.onboarding_layout_items)
    View mItemsLayout;

    @Bind(R.id.on_boarding_items_text_view_header)
    TextView mHeaderTextView;

    @Bind(R.id.on_boarding_items_text_view_description)
    TextView mDescriptionTextView;

    @OnClick(R.id.on_board_button_mattress)
    void mattressClicked() {

        if (mCanChangeHasMattress) {
            mMattressImageView.setSelected(!mMattressImageView.isSelected());

            if ( mMattressImageView.isSelected() ) {
                mMattressItemImageView.setAlpha(0.0f);

                mMattressItemImageView.animate().alpha(1.0f).setDuration(250).start();
            } else {
                mMattressItemImageView.setAlpha(1.0f);

                mMattressItemImageView.animate().alpha(0.0f).setDuration(250).start();
            }
        }
    }


    @Bind(R.id.on_board_button_tracker)
    ImageView mTrackerImageView;

    @Bind(R.id.onboarding_image_view_background_beddit)
    ImageView mTrackerItemImageView;

    @OnClick(R.id.on_board_button_tracker)
    void trackerClicked() {
        mTrackerImageView.setSelected(!mTrackerImageView.isSelected());

        if ( mTrackerImageView.isSelected() ) {
            mTrackerItemImageView.setTranslationY(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -40, getResources().getDisplayMetrics()));
            mTrackerItemImageView.setAlpha(0.0f);

            mTrackerItemImageView.animate().translationY(0).alpha(1.0f).setDuration(250).start();
        } else {
            mTrackerItemImageView.setTranslationY(0);
            mTrackerItemImageView.setAlpha(1.0f);

            mTrackerItemImageView.animate().translationY(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -40, getResources().getDisplayMetrics())).alpha(0.0f).setDuration(250).start();
        }
    }

    @Bind(R.id.on_board_button_base)
    ImageView mBaseImageView;

    @Bind(R.id.onboarding_image_view_background_base)
    ImageView mBaseItemImageView;

    @OnClick(R.id.on_board_button_base)
    void baseClicked() {

        mBaseImageView.setSelected(!mBaseImageView.isSelected());

        if ( mBaseImageView.isSelected() ) {
            mBaseItemImageView.setTranslationY(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));
            mBaseItemImageView.setAlpha(0.0f);

            mBaseItemImageView.animate().translationY(0).alpha(1.0f).setDuration(250).start();
        } else {
            mBaseItemImageView.setTranslationY(0);
            mBaseItemImageView.setAlpha(1.0f);

            mBaseItemImageView.animate().translationY(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())).alpha(0.0f).setDuration(250).start();
        }
    }

    @OnClick(R.id.on_board_button_continue)
    void continueClicked() {
        if ( mOnActionListener != null ) {
            mOnActionListener.onItemsContinueClicked(mMattressImageView.isSelected(),mTrackerImageView.isSelected(),mBaseImageView.isSelected());
        }
    }

    @OnClick(R.id.on_boarding_items_text_view_header)
    void headerClicked() {
        if ( ++mNoOfTimesHeaderClicked >= 2 ) {
            mCanChangeHasMattress = true;
        }
    }

    public OnBoardingItemsFragment() {
        // Required empty public constructor
    }

    public static OnBoardingItemsFragment newInstance(boolean hasBase, boolean hasPump, boolean hasTracker) {
        OnBoardingItemsFragment fragment = new OnBoardingItemsFragment();
        Bundle args = new Bundle();
        args.putBoolean("hasBase",hasBase);
        args.putBoolean("hasPump",hasPump);
        args.putBoolean("hasTracker",hasTracker);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHasBase=getArguments().getBoolean("hasBase");
            mHasPump=getArguments().getBoolean("hasPump");
            mHasTracker=getArguments().getBoolean("hasTracker");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof OnActionListener ) {
            mOnActionListener = (OnActionListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_on_boarding_items, container, false);

        ButterKnife.bind(this,view);

        mItemsLayout.setAlpha(0.0f);

        mCompositeSubscription.add(((NewOnBoardActivity)getActivity()).getOnBoardingObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<OnBoardingState>() {
            @Override
            public void accept(OnBoardingState onBoardingState) {
                if (onBoardingState.state == OnBoardingState.State.ChoosingDevices) {

                    mHasPump = true;
                    mHasBase = onBoardingState.foundBase;
                    mHasTracker = onBoardingState.foundTracker;

                    mBaseImageView.setSelected(onBoardingState.foundBase);
                    mBaseItemImageView.setAlpha(mBaseImageView.isSelected()?1.0f:0.0f);

                    mMattressImageView.setSelected(true);
                    mMattressItemImageView.setAlpha(mMattressImageView.isSelected()?1.0f:0.0f);

                    mTrackerImageView.setSelected(onBoardingState.foundTracker);
                    mTrackerItemImageView.setAlpha(mTrackerImageView.isSelected()?1.0f:0.0f);

                    mHeaderTextView.setText("Success!");
                    mDescriptionTextView.setText("This is what I found. Tap on the item if it's missing and I'll double check");

                    mItemsLayout.animate().alpha(1.0f).start();
                }
            }
        }));

        mHeaderTextView.setText("I'm searching ...");
        mDescriptionTextView.setText("Please wait while I locate your sleeping devices");

        return view;

    }

    @Override
    public void onDestroyView() {
        mCompositeSubscription.clear();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mOnActionListener = null;
        super.onDetach();
    }
}
