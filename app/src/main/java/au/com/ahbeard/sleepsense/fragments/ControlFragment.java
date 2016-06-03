package au.com.ahbeard.sleepsense.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import au.com.ahbeard.sleepsense.R;
import butterknife.Bind;

/**
 * Created by neal on 2/06/2016.
 */
public class ControlFragment extends Fragment {

    @Bind(R.id.image_view_progress_icon)
    ImageView mProgressImageView;

    @Bind(R.id.controls_layout_header)
    View mHeaderLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void startProgress() {
        if ( mProgressImageView != null && mProgressImageView.getVisibility() == View.INVISIBLE ) {
            mProgressImageView.setVisibility(View.VISIBLE);
            ((AnimationDrawable)mProgressImageView.getDrawable()).start();
        }
    }

    protected void stopProgress() {
        if ( mProgressImageView != null && mProgressImageView.getVisibility() == View.VISIBLE ) {
            ((AnimationDrawable)mProgressImageView.getDrawable()).stop();
            mProgressImageView.setVisibility(View.INVISIBLE);
        }
    }
}
