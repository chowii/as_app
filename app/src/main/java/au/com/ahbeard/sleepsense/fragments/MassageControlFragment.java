package au.com.ahbeard.sleepsense.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MassageControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MassageControlFragment extends Fragment {



    @Bind(R.id.massage_button_timer)
    View mTimerButton;

    @OnClick(R.id.massage_button_timer)
    void timerButtonClicked() {
        mMassageTimerState = (mMassageTimerState + 1) % 4;
        Log.d("TIMER","mMassageTimerState"+mMassageTimerState);
        updateViews();

    }

    @Bind({R.id.massage_text_view_10_min, R.id.massage_text_view_20_min, R.id.massage_text_view_30_min})
    List<View> mTimeTextViews;

    // Temporary while building back end.
    private int mMassageTimerState = 0;

    public MassageControlFragment() {

    }

    private void updateViews() {

        mTimerButton.setSelected(false);

        for (View view: mTimeTextViews) {
            view.setSelected(false);
        }

        switch (mMassageTimerState) {
            case 0:
                break;
            case 1:
                mTimerButton.setSelected(true);
                mTimeTextViews.get(0).setSelected(true);
                break;
            case 2:
                mTimerButton.setSelected(true);
                mTimeTextViews.get(1).setSelected(true);
                break;
            case 3:
                mTimerButton.setSelected(true);
                mTimeTextViews.get(2).setSelected(true);
                break;
            default:

        }
    }

    /**
     * @return A new instance of fragment MassageControlFragment.
     */
    public static MassageControlFragment newInstance() {
        MassageControlFragment fragment = new MassageControlFragment();
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
        View view = inflater.inflate(R.layout.fragment_massage_control, container, false);

        ButterKnife.bind(this, view);

        return view;

    }

    @Override
    public void onDestroyView() {

        ButterKnife.unbind(this);

        super.onDestroyView();
    }


}
