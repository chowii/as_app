package au.com.ahbeard.sleepsense.services;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by neal on 4/03/2016.
 */
public class PreferenceService {

    private static PreferenceService sPreferenceService;

    private static final String HAS_RECORDED_FIRST_SLEEP = "hasRecordedFirstSleep";
    private static final String PUMP_DEVICE_ADDRESS = "pumpDeviceAddress";
    private static final String BASE_DEVICE_ADDRESS = "baseDeviceAddress";
    private static final String TRACKER_DEVICE_ADDRESS = "trackerDeviceAddress";
    private static final String TRACKER_DEVICE_NAME = "trackerDeviceName";
    private static final String ALT_TRACKER_DEVICE_ADDRESS = "altTrackerDeviceAddress";
    private static final String ALT_TRACKER_DEVICE_NAME = "altTrackerDeviceName";
    private static final String SLEEP_TARGET_TIME = "sleepTargetTime";
    private static final String SIDE_OF_BED = "sideOfBed";
    private static final String PROFILE_SEX = "profile_sex";
    private static final String PROFILE_AGE = "profile_age";
    private static final String PROFILE_EMAIL = "profile_emailAddress";
    private static final String USER_WEIGHT = "userWeight";
    private static final String USER_HEIGHT = "userHeight";
    private static final String PEOPLE_IN_BED = "peopleInBed";
    private static final String MATTRESS_LINE = "mattressLine";

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

    void setHasRecordedASleep(boolean hasRecordedFirstSleep) {
        putBoolean(HAS_RECORDED_FIRST_SLEEP, hasRecordedFirstSleep);
    }

    public boolean getHasRecordedASleep() {
        return getBoolean(HAS_RECORDED_FIRST_SLEEP,false);
    }

    public void setPumpDeviceAddress(String address) {
        putString(PUMP_DEVICE_ADDRESS, address);
    }

    public String getPumpDeviceAddress() {
        return getString(PUMP_DEVICE_ADDRESS,null);
    }

    public void clearPumpDeviceAddress() {
        remove(PUMP_DEVICE_ADDRESS);
    }

    public void setBaseDeviceAddress(String address) {
        putString(BASE_DEVICE_ADDRESS, address);
    }

    public String getBaseDeviceAddress() {
        return getString(BASE_DEVICE_ADDRESS,null);
    }

    public void clearBaseDeviceAddress() {
        remove(BASE_DEVICE_ADDRESS);
    }

    public void setTrackerDeviceAddress(String address) {
        putString(TRACKER_DEVICE_ADDRESS, address);
    }

    public String getTrackerDeviceAddress() {
        return getString(TRACKER_DEVICE_ADDRESS,null);
    }

    public void clearTrackerDeviceAddress() {
        remove(TRACKER_DEVICE_ADDRESS);
    }

    public void setTrackerDeviceName(String address) {
        putString(TRACKER_DEVICE_NAME, address);
    }

    public String getTrackerDeviceName() {
        return getString(TRACKER_DEVICE_NAME,null);
    }

    public void clearTrackerDeviceName() {
        remove(TRACKER_DEVICE_NAME);
    }

    public void setAltTrackerDeviceAddress(String address) {
        putString(ALT_TRACKER_DEVICE_ADDRESS, address);
    }

    public String getAltTrackerDeviceAddress() {
        return getString(ALT_TRACKER_DEVICE_ADDRESS,null);
    }

    public void clearAltTrackerDeviceAddress() {
        remove(ALT_TRACKER_DEVICE_ADDRESS);
    }

    public void setAltTrackerDeviceName(String address) {
        putString(ALT_TRACKER_DEVICE_NAME, address);
    }

    public String getAltTrackerDeviceName() {
        return getString(ALT_TRACKER_DEVICE_NAME,null);
    }

    public void clearAltTrackerDeviceName() {
        remove(ALT_TRACKER_DEVICE_NAME);
    }

    public float getSleepTargetTime() {
        return getFloat(SLEEP_TARGET_TIME,8.0f);
    }

    public void setSleepTargetTime(float sleepTargetTime) {
        AnalyticsService.instance().setSleepGoal(sleepTargetTime);
        putFloat(SLEEP_TARGET_TIME,sleepTargetTime);
    }

    public String getSideOfBed() {
        return getString(SIDE_OF_BED,"LEFT");
    }

    public void setSideOfBed(String sideOfBed) {
        putString(SIDE_OF_BED, sideOfBed);
    }

    public void setProfile(String sex, Integer age, String emailAddress) {
        AnalyticsService.instance().setProfile(sex,age,emailAddress);

        setProfileSex(sex);
        setProfileAge("" + age);
        putString(PROFILE_EMAIL, emailAddress);
    }

    public void setProfileSex(String sex) {
        putString(PROFILE_SEX, sex);
    }

    public void setProfileAge(String age) {
        putString(PROFILE_AGE, age);
    }

    public String getProfileSex() {
        return getString(PROFILE_SEX,null);
    }

    public String getProfileAge() {
        return getString(PROFILE_AGE,null);
    }

    public String getProfileEmailAddress() {
        return getString(PROFILE_EMAIL,null);
    }

    public void setUserWeight(Integer weight) {
        putInt(USER_WEIGHT, weight == null ? -1 : weight);
    }

    public Integer getUserWeight() {
        int value = getInt(USER_WEIGHT, -1);
        return value == -1 ? null : value;
    }

    public void setUserHeight(Integer height) {
        putInt(USER_HEIGHT, height == null ? -1 : height);
    }

    public Integer getUserHeight() {
        int value = getInt(USER_HEIGHT, -1);
        return value == -1 ? null : value;
    }

    public void setPeopleInBed(Integer value) {
        putInt(PEOPLE_IN_BED, value);
    }

    public Integer getPeopleInBed() {
        return getInt(PEOPLE_IN_BED, 1);
    }

    public void setMattressLine(int mattressLine) {
        String s = mContext.getString(mattressLine);
        putString(MATTRESS_LINE, s);
    }

    public String getMattressLine() {
        return getString(MATTRESS_LINE, null);
    }


    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences("preferences.dat", Context.MODE_PRIVATE);
    }

    private void remove(String key) {
        getSharedPreferences().edit().remove(key).apply();
    }

    private void putString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }

    private void putFloat(String key, Float value) {
        getSharedPreferences().edit().putFloat(key, value).apply();
    }

    private void putBoolean(String key, Boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }

    private void putInt(String key, Integer value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }

    private String getString(String key, String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    private Float getFloat(String key, Float defaultValue) {
        return getSharedPreferences().getFloat(key, defaultValue);
    }

    private Boolean getBoolean(String key, Boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }

    private int getInt(String key, int defaultValue) {
        return getSharedPreferences().getInt(key, defaultValue);
    }

}
