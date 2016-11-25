package au.com.ahbeard.sleepsense.fragments.onboarding;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.ahbeard.sleepsense.R;

public class MattressSizeNotSureDialogFragment extends BottomSheetDialogFragment {

    public static  MattressSizeNotSureDialogFragment newInstance() {
        MattressSizeNotSureDialogFragment frag = new MattressSizeNotSureDialogFragment();
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mattress_size_not_sure_dialog, container, false);
        return v;
    }
}
