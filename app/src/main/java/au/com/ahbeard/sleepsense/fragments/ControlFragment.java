package au.com.ahbeard.sleepsense.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * THIS CLASS WAS DEPRECATED AND CODE WAS COPIED INTO SUBCLASSES BECAUSE BUTTERKNIFE DOESN'T HANDLE INJECTION
 * VERY WELL WITH INCREMENTAL BUILDS :-(
 */
public class ControlFragment extends Fragment {

    @Bind(R.id.image_view_progress_icon)
    protected ImageView mProgressImageView;

    @Bind(R.id.controls_layout_header)
    protected View mHeaderLayout;

    @Bind(R.id.progress_layout)
    protected View mLayout;

    @Bind(R.id.progress_layout_text_view_message)
    protected TextView mMessageTextView;

    @Bind(R.id.progress_layout_text_view_action)
    protected TextView mActionTextView;

    private Action1<Void> mAction;

    @OnClick(R.id.progress_layout_text_view_action)
    protected void progressAction() {
        if (mAction != null) {
            mAction.call(null);
            mLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        stopProgress();
    }

    protected void startProgress() {
        if (mProgressImageView != null && mProgressImageView.getVisibility() == View.INVISIBLE) {
            mProgressImageView.setVisibility(View.VISIBLE);
            ((AnimationDrawable) mProgressImageView.getDrawable()).start();
        }
    }

    protected void stopProgress() {
        if (mProgressImageView != null && mProgressImageView.getVisibility() == View.VISIBLE) {
            ((AnimationDrawable) mProgressImageView.getDrawable()).stop();
            mProgressImageView.setVisibility(View.INVISIBLE);
        }
    }

    protected void bind(View view) {
        ButterKnife.bind(this,view);
    }

    protected void unbind() {
        ButterKnife.unbind(this);
    }

    protected void showToast(String message, String actionText, Action1<Void> action) {

        mAction = action;

        mMessageTextView.setText(message);
        mActionTextView.setText(actionText);

        mLayout.setVisibility(View.VISIBLE);

    }
}
