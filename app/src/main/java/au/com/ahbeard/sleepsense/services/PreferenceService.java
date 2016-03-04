package au.com.ahbeard.sleepsense.services;

import android.content.Context;

/**
 * Created by neal on 4/03/2016.
 */
public class PreferenceService {

    private static PreferenceService sPreferenceService;

    public static void initialize(Context context) {
        sPreferenceService = new PreferenceService(context);
    }

    public static PreferenceService instance() {
        return sPreferenceService;
    }

    private Context mContext;

    public PreferenceService(Context context) {
        mContext = context;
    }


    public boolean requiresOnBoarding() {
        return true;
    }
}
