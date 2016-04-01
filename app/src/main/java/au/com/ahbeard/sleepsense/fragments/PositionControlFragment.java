package au.com.ahbeard.sleepsense.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.widgets.StyledButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */
public class PositionControlFragment extends Fragment {

    @Bind({R.id.position_button_rest,R.id.position_button_recline,R.id.position_button_relax,R.id.position_button_recover})
    List<StyledButton> mPositionButtons;

    @OnClick({R.id.position_button_rest,R.id.position_button_recline,R.id.position_button_relax,R.id.position_button_recover})
    void onClick(View clickedButton) {
        for ( StyledButton button : mPositionButtons ) {
            if ( button == clickedButton ) {
                button.setSelected(true);
            } else {
                button.setSelected(false);
            }
        }
    }

    public PositionControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PositionControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PositionControlFragment newInstance() {
        PositionControlFragment fragment = new PositionControlFragment();
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
        View view = inflater.inflate(R.layout.fragment_position_control, container, false);
        ButterKnife.bind(this,view);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
