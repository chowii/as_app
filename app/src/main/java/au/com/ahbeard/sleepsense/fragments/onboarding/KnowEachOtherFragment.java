package au.com.ahbeard.sleepsense.fragments.onboarding;

import android.content.Context;
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

public class KnowEachOtherFragment extends OnBoardingFragment {

    private KnowEachOtherFragment.OnActionListener mOnActionListener;
    public interface OnActionListener {
        void onContinueClicked(Part part);
    }
    @Bind(R.id.textTitle)
    TextView title;
    @Bind(R.id.textOther)
    TextView other;
    @Bind(R.id.buttonContinue)
    Button button;
    Part displayingPart;

    @OnClick(R.id.buttonContinue)
    void optionClicked() {
        if (mOnActionListener != null) {
            mOnActionListener.onContinueClicked(displayingPart);
        }
    }

    public static KnowEachOtherFragment newInstance(String titleString, String otherString, String buttonString, Part part) {
        KnowEachOtherFragment fragment = new KnowEachOtherFragment();
        Bundle bundle = new Bundle();
        bundle.putString(GlobalVars.QUESTIONNAIRE_TITLE, titleString);
        bundle.putString(GlobalVars.QUESTIONNAIRE_OTHER_TEXT, otherString);
        bundle.putString(GlobalVars.QUESTIONNAIRE_BUTTON, buttonString);
        bundle.putSerializable(GlobalVars.QUESTIONNAIRE_PART, part);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof KnowEachOtherFragment.OnActionListener) {
            mOnActionListener = (KnowEachOtherFragment.OnActionListener)context;
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
        View view =  inflater.inflate(R.layout.fragment_know_each_other, container, false);

        ButterKnife.bind(this,view);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            title.setText(bundle.getString(GlobalVars.QUESTIONNAIRE_TITLE));
            other.setText(bundle.getString(GlobalVars.QUESTIONNAIRE_OTHER_TEXT));
            button.setText(bundle.getString(GlobalVars.QUESTIONNAIRE_BUTTON));
            displayingPart = (Part)bundle.getSerializable(GlobalVars.QUESTIONNAIRE_PART);
        }
        return view;
    }

    public enum Part {
        PART1, PART2
    }

}