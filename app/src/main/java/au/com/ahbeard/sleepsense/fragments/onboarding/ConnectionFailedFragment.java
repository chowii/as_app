package au.com.ahbeard.sleepsense.fragments.onboarding;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import au.com.ahbeard.sleepsense.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ConnectionFailedFragment extends BottomSheetDialogFragment {

    ConnectionFailedFragment.OnActionListener mOnActionListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connection_failed, container, false);
        ButterKnife.bind(this,v);

        v.setFocusableInTouchMode(true);
        v.setOnKeyListener( new View.OnKeyListener(){
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event ){
                if( keyCode == KeyEvent.KEYCODE_BACK ){
                    if (mOnActionListener != null) {
                        mOnActionListener.fragmentBackButtonPressed();
                    }
                }
                return false;
            }
        } );

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof ConnectionFailedFragment.OnActionListener) {
            mOnActionListener = (ConnectionFailedFragment.OnActionListener)context;
        }
    }

    @Override
    public void onDetach() {
        mOnActionListener = null;
        super.onDetach();
    }

    @OnClick(R.id.button_connection_failed_help_me)
    void optionClicked1() {
        if (mOnActionListener != null) {
            mOnActionListener.helpMeClicked();
        }
    }

    @OnClick(R.id.button_connection_failed_try_again)
    void optionClicked2() {
        if (mOnActionListener != null) {
            mOnActionListener.tryAgainClicked();
        }
    }

    @OnClick(R.id.button_connection_failed_set_up_later)
    void optionClicked3() {
        if (mOnActionListener != null) {
            mOnActionListener.setupLaterClicked();
        }
    }

    public static  ConnectionFailedFragment newInstance() {
        ConnectionFailedFragment frag = new ConnectionFailedFragment();
        return frag;
    }

    public interface OnActionListener{
        void helpMeClicked();
        void tryAgainClicked();
        void setupLaterClicked();
        void fragmentBackButtonPressed();
    }
}
