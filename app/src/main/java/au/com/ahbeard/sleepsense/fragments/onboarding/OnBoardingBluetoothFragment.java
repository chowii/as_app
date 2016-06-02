package au.com.ahbeard.sleepsense.fragments.onboarding;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import au.com.ahbeard.sleepsense.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnBoardingBluetoothFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnBoardingBluetoothFragment extends Fragment {

    private OnActionListener mOnActionListener;

    public interface OnActionListener {
        void onBluetoothContinueClicked();
    }

    @OnClick(R.id.on_board_button_continue)
    void continueClicked() {
        if (mOnActionListener != null) {
            mOnActionListener.onBluetoothContinueClicked();
            mContinueButton.animate().alpha(0.0f).start();
        }
    }

    @Bind(R.id.on_board_button_continue)
    Button mContinueButton;

    public static OnBoardingBluetoothFragment newInstance() {
        OnBoardingBluetoothFragment fragment = new OnBoardingBluetoothFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof OnActionListener) {
            mOnActionListener = (OnActionListener)context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
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
        View view =  inflater.inflate(R.layout.fragment_on_boarding_bluetooth, container, false);

        ButterKnife.bind(this,view);

        return view;
    }

}
