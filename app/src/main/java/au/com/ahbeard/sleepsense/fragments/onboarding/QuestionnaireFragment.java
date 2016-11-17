package au.com.ahbeard.sleepsense.fragments.onboarding;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.utils.GlobalVars;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuestionnaireFragment extends OnBoardingFragment {

    private QuestionnaireFragment.OnActionListener mOnActionListener;

    @Bind(R.id.textHeader)
    TextView header;
    @Bind(R.id.button_option1)
    Button option1;
    @Bind(R.id.button_option2)
    Button option2;

    public interface OnActionListener {
        void onSelectionClicked(SelectedOption selectedOption);
    }

//    @Bind(R.id.on_board_button_continue)
//    Button mContinueButton;

    @OnClick(R.id.button_option1)
    void optionClicked1() {
        if (mOnActionListener != null) {
            mOnActionListener.onSelectionClicked(SelectedOption.OPTION_1);
//            mContinueButton.animate().alpha(0.0f).start();
        }
    }

    @OnClick(R.id.button_option2)
    void option2Clicked() {
        if(mOnActionListener != null) {
            mOnActionListener.onSelectionClicked(SelectedOption.OPTION_2);
        }
    }

//    @OnClick(R.id.on_board_text_view_find_out_more)
//    void findOutMoreClicked() {
//        AnalyticsService.instance().logOnboardingFindOutMoreTouch();
//        startActivity(HelpActivity.getIntent(getActivity(),"More About Sleepsense", "http://sleepsense.com.au"));
//    }

    public static QuestionnaireFragment newInstance(String headerString, String option1String, String option2String) {
        QuestionnaireFragment fragment = new QuestionnaireFragment();
        Bundle bundle = new Bundle();
        bundle.putString(GlobalVars.QUESTIONNAIRE_HEADER, headerString);
        bundle.putString(GlobalVars.QUESTIONNAIRE_OPTION1, option1String);
        bundle.putString(GlobalVars.QUESTIONNAIRE_OPTION2, option2String);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof QuestionnaireFragment.OnActionListener) {
            mOnActionListener = (QuestionnaireFragment.OnActionListener)context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        mOnActionListener = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_questionnaire, container, false);

        ButterKnife.bind(this,view);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            header.setText(bundle.getString(GlobalVars.QUESTIONNAIRE_HEADER));
            option1.setText(bundle.getString(GlobalVars.QUESTIONNAIRE_OPTION1));
            option2.setText(bundle.getString(GlobalVars.QUESTIONNAIRE_OPTION2));
        }
        return view;
    }

    public enum SelectedOption{
        OPTION_1, OPTION_2, NA
    }
}