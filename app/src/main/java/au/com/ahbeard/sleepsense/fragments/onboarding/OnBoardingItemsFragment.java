package au.com.ahbeard.sleepsense.fragments.onboarding;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import au.com.ahbeard.sleepsense.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnBoardingItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnBoardingItemsFragment extends Fragment {

    private OnActionListener mOnActionListener;

    private boolean mHasBase;
    private boolean mHasPump;
    private boolean mHasTracker;

    public interface OnActionListener {
        void onItemsContinueClicked(boolean hasMattress, boolean hasTracker, boolean hasBase);
    }

    @Bind(R.id.on_board_button_mattress)
    ImageView mMattressImageView;

//    @OnClick(R.id.on_board_button_mattress)
//    void mattressClicked() {
//
//    }

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

        mMattressImageView.setSelected(true);

        mBaseImageView.setSelected(mHasBase);
        mBaseItemImageView.setAlpha(mBaseImageView.isSelected()?1.0f:0.0f);

        mMattressImageView.setSelected(true);
        mMattressImageView.setAlpha(1.0f);

        mTrackerImageView.setSelected(mHasTracker);
        mTrackerItemImageView.setAlpha(mTrackerImageView.isSelected()?1.0f:0.0f);


        return view;

    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mOnActionListener = null;
        super.onDetach();
    }
}
