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
        mContext = context.getApplicationContext();
    }

    public boolean requiresOnBoarding() {
        return getPumpDeviceAddress()==null&&getBaseDeviceAddress()==null&&getTrackerDeviceAddress()==null;
    }

    public void setHasRecordedASleep(boolean hasRecordedFirstSleep) {
        getSharedPreferences().edit().putBoolean("hasRecordedFirstSleep", hasRecordedFirstSleep).commit();
    }

    public boolean getHasRecordedASleep() {
        return getSharedPreferences().getBoolean("hasRecordedFirstSleep",false);
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

    public void setTrackerDeviceName(String address) {
        getSharedPreferences().edit().putString("trackerDeviceName", address).commit();
    }

    public String getTrackerDeviceName() {
        return getSharedPreferences().getString("trackerDeviceName",null);
    }

    public void clearTrackerDeviceName() {
        getSharedPreferences().edit().remove("trackerDeviceName").commit();
    }

    public float getSleepTargetTime() {
        return getSharedPreferences().getFloat("sleepTargetTime",8.0f);
    }

    public void setSleepTargetTime(float sleepTargetTime) {
        AnalyticsService.instance().setSleepGoal(sleepTargetTime);
        getSharedPreferences().edit().putFloat("sleepTargetTime",sleepTargetTime).commit();
    }

    public String getSideOfBed() {
        return getSharedPreferences().getString("sideOfBed","LEFT");
    }

    public void setSideOfBed(String sideOfBed) {
        getSharedPreferences().edit().putString("sideOfBed",sideOfBed).commit();
    }

    public void setProfile(String sex, String age, String emailAddress) {

        AnalyticsService.instance().setProfile(sex,age,emailAddress);

        SharedPreferences.Editor edit = getSharedPreferences().edit();

        edit.putString("profile_sex", sex);
        edit.putString("profile_age", age);
        edit.putString("profile_emailAddress", emailAddress);

        edit.commit();
    }

    public String getProfileSex() {
        return getSharedPreferences().getString("profile_sex",null);
    }

    public String getProfileAge() {
        return getSharedPreferences().getString("profile_age",null);
    }

    public String getProfileEmailAddress() {
        return getSharedPreferences().getString("profile_emailAddress",null);
    }


    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences("preferences.dat", Context.MODE_PRIVATE);
    }



}
