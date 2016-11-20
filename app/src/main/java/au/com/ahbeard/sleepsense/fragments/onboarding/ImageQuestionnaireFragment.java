package au.com.ahbeard.sleepsense.fragments.onboarding;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.support.v4.graphics.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.utils.GlobalVars;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ImageQuestionnaireFragment extends OnBoardingFragment implements View.OnTouchListener {

    @Bind(R.id.image_questionnaire_header)
    TextView header;
    @Bind(R.id.image_questionnaire_option1)
    TextView label1;
    @Bind(R.id.image_questionnaire_option2)
    TextView label2;
    @Bind(R.id.button_image_questionnaire_skip)
    Button buttonSkip;
    @Bind(R.id.button_image_questionnaire_continue)
    Button buttonContinue;

    OnActionListener mOnActionListener;

    private ViewGroup buttonsContainer;
    private ViewGroup activeButton = null;
    private final int MAX_BUTTONS = 2;

    public interface OnActionListener {
        void onSelectionClicked(ButtonActioned buttonActioned);
    }

    @OnClick(R.id.button_image_questionnaire_skip)
    void optionClicked1() {
        if (mOnActionListener != null) {
            mOnActionListener.onSelectionClicked(ButtonActioned.SKIP);
        }
    }

    @OnClick(R.id.button_image_questionnaire_continue)
    void optionClicked2() {
        if(activeButton != null) {
            if (mOnActionListener != null) {
                mOnActionListener.onSelectionClicked(ButtonActioned.CONTINUE);
            }
        }
        else {
            Toast.makeText(getContext(), "Please select a gender to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_questionnaire, container, false);
        ButterKnife.bind(this,view);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            header.setText(bundle.getString(GlobalVars.IMAGE_QUESTIONNAIRE_HEADER));
            label1.setText(bundle.getString(GlobalVars.IMAGE_QUESTIONNAIRE_LABEL1));
            label2.setText(bundle.getString(GlobalVars.IMAGE_QUESTIONNAIRE_LABEL2));
        }

        //code for circle button animmation
        //{
        this.buttonsContainer = (ViewGroup) view.findViewById(R.id.buttonsContainer);

        final int buttonsSpacing = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        final int buttonSize = (int) getResources().getDimension(R.dimen.circular_button_size);
        Outline circularOutline = new Outline();
        circularOutline.setOval(0, 0, buttonSize, buttonSize);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, buttonSize, buttonSize);
            }
        };

        for (int i = 0; i < MAX_BUTTONS; i++) {
            ViewGroup buttonHost = (ViewGroup) getLayoutInflater(savedInstanceState).inflate(R.layout.circular_button_layout, buttonsContainer, false);
            ImageView button = (ImageView) buttonHost.getChildAt(0);

            buttonHost.setOutlineProvider(viewOutlineProvider);
            if(i == 0)
                button.setImageResource(R.drawable.arrow);
            else
                button.setImageResource(R.drawable.arrow);

            buttonHost.setOnTouchListener(this);
            buttonsContainer.addView(buttonHost);

            //Add margin between buttons manually
            if (i != MAX_BUTTONS - 1) {
                buttonsContainer.addView(new Space(this.getContext()), new ViewGroup.LayoutParams(buttonsSpacing, buttonSize));
            }
        }
        //selectButton(((ViewGroup) buttonsContainer.getChildAt(0)), false);
        //}

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof ImageQuestionnaireFragment.OnActionListener) {
            mOnActionListener = (ImageQuestionnaireFragment.OnActionListener)context;
        }
    }

    @Override
    public void onDetach() {
        mOnActionListener = null;
        super.onDetach();
    }

    public static ImageQuestionnaireFragment newInstance(String headerText, String label1, String label2) {
        ImageQuestionnaireFragment fragment = new ImageQuestionnaireFragment();
        Bundle args = new Bundle();
        args.putString(GlobalVars.IMAGE_QUESTIONNAIRE_HEADER, headerText);
        args.putString(GlobalVars.IMAGE_QUESTIONNAIRE_LABEL1, label1);
        args.putString(GlobalVars.IMAGE_QUESTIONNAIRE_LABEL2, label2);
        fragment.setArguments(args);
        return fragment;
    }

    private void selectButton(ViewGroup buttonHost, boolean reveal) {
        selectButton(buttonHost, reveal, buttonHost.getWidth(), buttonHost.getHeight());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void selectButton(ViewGroup buttonHost, boolean reveal, int startX, int startY) {
        if (buttonHost == activeButton) {
            return;
        }

        if (activeButton != null) {
            activeButton.setSelected(false);
            activeButton = null;
        }

        activeButton = buttonHost;
        activeButton.setSelected(true);

            View button = activeButton.getChildAt(0);
        if (reveal) {
            ViewAnimationUtils.createCircularReveal(button,
                    startX,
                    startY,
                    0,
                    button.getHeight()).start();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ((ViewGroup) view).getChildAt(0).getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                break;
            case MotionEvent.ACTION_UP:
                selectButton((ViewGroup) view, true, (int) motionEvent.getX(), (int) motionEvent.getY());
                break;

        }
        return false;
    }

    public enum ButtonActioned {
        SKIP, CONTINUE
    }

    public enum Gender {
        MALE, FEMALE
    }
}
