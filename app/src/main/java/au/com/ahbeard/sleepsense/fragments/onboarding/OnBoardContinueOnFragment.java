package au.com.ahbeard.sleepsense.fragments.onboarding;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.ahbeard.sleepsense.R;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnBoardContinueOnFragment extends Fragment {

    public static OnBoardContinueOnFragment newInstance() {
        OnBoardContinueOnFragment fragment = new OnBoardContinueOnFragment();
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
        View view = inflater.inflate(R.layout.fragment_on_board_continue_on, container, false);

        ButterKnife.bind(this,view);

        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}