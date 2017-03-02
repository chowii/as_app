package au.com.ahbeard.sleepsense.fragments.settings_wth_factory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;

/**
 * Created by sabbib on 1/03/2017.
 */

public class PrivacyFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_help, container, false);

        return view;
    }
}
