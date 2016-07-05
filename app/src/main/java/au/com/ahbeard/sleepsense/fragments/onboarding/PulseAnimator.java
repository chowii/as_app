package au.com.ahbeard.sleepsense.fragments.onboarding;

import android.content.Context;
import android.media.Image;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.List;

import au.com.ahbeard.sleepsense.R;
import rx.functions.Action1;

/**
 * Created by luisramos on 5/07/16.
 */
public class PulseAnimator {

    interface ShouldStopCallback {
        boolean shouldStop();
    }

    public static void startAnimation(List<ImageView> pulses, final Context context, final ShouldStopCallback callback) {
        long animationDelay = 0;
        for (final ImageView imageView : pulses) {
            // Delay each animation by 200ms
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (context == null || (callback != null && callback.shouldStop())) {
                        return;
                    }
                    Animation pulse = AnimationUtils.loadAnimation(context, R.anim.pulse);
                    pulse.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    imageView.startAnimation(pulse);
                }
            }, animationDelay);
            animationDelay += 400;
        }
    }

    public static void stopAnimation(List<ImageView> pulses) {
        for (ImageView imageView : pulses) {
            imageView.clearAnimation();
            imageView.setAnimation(null);
            imageView.setVisibility(View.INVISIBLE);
        }
    }
}
