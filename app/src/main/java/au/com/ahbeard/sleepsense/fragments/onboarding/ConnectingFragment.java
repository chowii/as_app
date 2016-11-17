package au.com.ahbeard.sleepsense.fragments.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.HelpActivity;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import au.com.ahbeard.sleepsense.utils.GlobalVars;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnectingFragment extends OnBoardingFragment {

    @Bind(R.id.connecting_fragment_header)
    TextView header;

//    private ConnectingFragment.OnActionListener mOnActionListener;
//
//    public interface OnActionListener {
//        void onBluetoothContinueClicked();
//    }

//    @Bind(R.id.on_board_button_continue)
//    Button mContinueButton;

//    @OnClick(R.id.on_board_button_continue)
//    void continueClicked() {
//        if (mOnActionListener != null) {
//            mOnActionListener.onBluetoothContinueClicked();
//            mContinueButton.animate().alpha(0.0f).start();
//        }
//    }

//    @OnClick(R.id.on_board_text_view_find_out_more)
//    void findOutMoreClicked() {
//        AnalyticsService.instance().logOnboardingFindOutMoreTouch();
//        startActivity(HelpActivity.getIntent(getActivity(),"More About Sleepsense", "http://sleepsense.com.au"));
//    }

    public static ConnectingFragment newInstance(String headerText) {
        ConnectingFragment fragment = new ConnectingFragment();
        Bundle args = new Bundle();
        args.putString(GlobalVars.CONNECTING_HEADER, headerText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if ( context instanceof ConnectingFragment.OnActionListener) {
//            mOnActionListener = (ConnectingFragment.OnActionListener)context;
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onDetach() {
//        mOnActionListener = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_connecting, container, false);

        ButterKnife.bind(this,view);
        if(getArguments() != null)
        header.setText(getArguments().getString(GlobalVars.CONNECTING_HEADER));
        return view;
    }

}
