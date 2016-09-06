package au.com.ahbeard.sleepsense.services;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by luisramos on 6/09/16.
 */
public class SSActivityCallbacks implements Application.ActivityLifecycleCallbacks {

    private static SSActivityCallbacks instance = new SSActivityCallbacks();

    private SSActivityCallbacks() {}

    public static void initialize(Application app, final AppActivityInterface activityDelegate) {
        app.registerActivityLifecycleCallbacks(instance);
        instance.activityInterface = activityDelegate;
    }

    private Handler handler = new Handler();
    private Runnable pauseCheck;

    private boolean paused = true;
    private boolean foreground = false;

    private AppActivityInterface activityInterface;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (pauseCheck != null)
            handler.removeCallbacks(pauseCheck);

        if (wasBackground && activityInterface != null) {
            activityInterface.appIsActive();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        paused = true;

        if (pauseCheck != null)
            handler.removeCallbacks(pauseCheck);

        handler.postDelayed(pauseCheck = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;

                    if (activityInterface != null)
                        activityInterface.appWentToBackground();
                }
            }
        }, 500);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public interface AppActivityInterface {
        void appIsActive();
        void appWentToBackground();
    }
}