package au.com.ahbeard.sleepsense.services;

import android.content.Context;

/**
 * Created by neal on 2/05/2016.
 */
public class RemoteSleepDataService {

    private static RemoteSleepDataService sRemoteSleepDataService;

    private Context mContext;

    public static void initialize(Context context) {
        sRemoteSleepDataService = new RemoteSleepDataService(context);
    }

    public static RemoteSleepDataService instance() {
        return sRemoteSleepDataService;
    }

    private RemoteSleepDataService(Context context) {
        mContext = context;
    }


}
