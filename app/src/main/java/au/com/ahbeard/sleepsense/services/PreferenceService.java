package au.com.ahbeard.sleepsense.services;

import android.content.Context;
import android.content.SharedPreferences;

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
        return true;//!(getPumpDeviceAddress()==null&&getBaseDeviceAddress()==null&&getTrackerDeviceAddress()==null);
    }

    public void setPumpDeviceAddress(String address) {
        getSharedPreferences().edit().putString("pumpDeviceAddress", address).commit();
    }

    public String getPumpDeviceAddress() {
        return getSharedPreferences().getString("pumpDeviceAddress",null);
    }

    public void clearPumpDeviceAddress() {
        getSharedPreferences().edit().remove("pumpDeviceAddress").commit();
    }

    public void setBaseDeviceAddress(String address) {
        getSharedPreferences().edit().putString("baseDeviceAddress", address).commit();
    }

    public String getBaseDeviceAddress() {
        return getSharedPreferences().getString("baseDeviceAddress",null);
    }

    public void clearBaseDeviceAddress() {
        getSharedPreferences().edit().remove("baseDeviceAddress").commit();
    }

    public void setTrackerDeviceAddress(String address) {
        getSharedPreferences().edit().putString("trackerDeviceAddress", address).commit();
    }

    public String getTrackerDeviceAddress() {
        return getSharedPreferences().getString("trackerDeviceAddress",null);
    }

    public void clearTrackerDeviceAddress() {
        getSharedPreferences().edit().remove("trackerDeviceAddress").commit();
    }

    public float getSleepTargetTime() {
        return getSharedPreferences().getFloat("sleepTargetTime",8.0f);
    }

    public void getSleepTargetTime(float sleepTargetTime) {
        getSharedPreferences().edit().putFloat("sleepTargetTime",sleepTargetTime).commit();
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences("preferences.dat", Context.MODE_PRIVATE);
    }

}
