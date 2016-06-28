package au.com.ahbeard.sleepsense.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.InStoreHelpActivity;
import au.com.ahbeard.sleepsense.fragments.onboarding.OnBoardingFragment;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
public class InStoreHelpFragment extends OnBoardingFragment {

    private OnActionListener mOnActionListener;

    @Bind(R.id.on_board_button_try_again)
    Button mContinueButton;

    @OnClick(R.id.on_board_button_try_again)
    public void onContinueClicked() {
        if (mOnActionListener != null) {
            mOnActionListener.onRetryButtonClicked();
        }
    }

    @OnClick(R.id.in_store_help_image_view_close)
    void onClickClose() {
        if (getActivity() instanceof InStoreHelpActivity) {
            getActivity().finish();
        } else if (mOnActionListener != null) {
            mOnActionListener.onRetryButtonClicked();
        }

    }

    @Bind(R.id.in_store_help_layout_faqs)
    ViewGroup mFaqsLayout;

    public interface OnActionListener {
        void onRetryButtonClicked();

        void onCallButtonClicked();
    }

    public InStoreHelpFragment() {
        // Required empty public constructor
    }

    public static InStoreHelpFragment newInstance() {
        InStoreHelpFragment fragment = new InStoreHelpFragment();
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
        View view = inflater.inflate(R.layout.fragment_in_store_help, container, false);

        ButterKnife.bind(this, view);

        String[] questions = getResources().getStringArray(R.array.questions);
        String[] answers = getResources().getStringArray(R.array.answers);

        for (int i = 0; i < questions.length && i < answers.length; i++) {

            View questionView = inflater.inflate(R.layout.item_in_store_help_faq, mFaqsLayout, false);

            ((TextView) questionView.findViewById(R.id.item_instore_help_faq_text_view_question)).setText(questions[i]);
            ((TextView) questionView.findViewById(R.id.item_instore_help_faq_text_view_answer)).setText(answers[i]);

            mFaqsLayout.addView(questionView);

        }


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
