package au.com.ahbeard.sleepsense.services;

import android.content.Context;
import android.os.Bundle;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

import au.com.ahbeard.sleepsense.BuildConfig;
import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.model.beddit.Sleep;
import au.com.ahbeard.sleepsense.utils.StringUtils;
import io.lqd.sdk.Liquid;

/**
 * Created by neal on 21/06/2016.
 */
public class AnalyticsService {

    private static AnalyticsService sAnalyticsService;
    private static Context sContext;

    private FirebaseAnalytics mFirebaseAnalytics;

    public AnalyticsService(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context.getApplicationContext());
    }

    public static void initialize(Context context) {
        sContext = context.getApplicationContext();

        if (BuildConfig.DEBUG) {
            Liquid.initialize(sContext, "yw-jr0gP_nGJd4WWigFEkAWyl_ifapZo", BuildConfig.DEBUG);
        } else {
            Liquid.initialize(sContext, "UnQ1PVTRJPohf9RYChvD9CXh6stMnM_0");
        }
    }

    public static AnalyticsService instance() {
        if (sAnalyticsService == null) {
            sAnalyticsService = new AnalyticsService(sContext);
        }

        return sAnalyticsService;
    }

    private void logEvent(String event, Map<String, Object> attrs) {
        CustomEvent answersEvent = new CustomEvent(event);
        Bundle bundle = new Bundle();
        if (attrs != null) {
            for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                if (entry.getValue() instanceof String) {
                    bundle.putString(entry.getKey(), (String) entry.getValue());
                    answersEvent.putCustomAttribute(entry.getKey(), (String) entry.getValue());
                } else if (entry.getValue() instanceof Boolean) {
                    bundle.putBoolean(entry.getKey(), (Boolean) entry.getValue());
                    answersEvent.putCustomAttribute(entry.getKey(), Boolean.toString((Boolean) entry.getValue()));
                } else if (entry.getValue() instanceof Float) {
                    bundle.putFloat(entry.getKey(), (Float) entry.getValue());
                    answersEvent.putCustomAttribute(entry.getKey(), (Float) entry.getValue());
                }
            }
        }

        mFirebaseAnalytics.logEvent(event, bundle);
        Liquid.getInstance().track(event, attrs);
        Answers.getInstance().logCustom(answersEvent);
    }

    private void logUserProperty(String name, String property) {
        Liquid.getInstance().setUserAttribute(name, property);
        mFirebaseAnalytics.setUserProperty(name, property);
    }

    public static final String EVENT_SETUP_ITEMS_SELECTED = "setup_items_selected";
    public static final String EVENT_CLOSE_SETUP = "close_setup";
    public static final String EVENT_SETUP_ERROR_RESOLVING = "setup_error_resolving";
    public static final String EVENT_SETUP_TIME_TO_SKIP_LAY_ON_BED = "setup_time_to_skip_lay_on_bed";

    public static final String PROPERTY_SELECTED_MATTRESS = "selected_mattress";
    public static final String PROPERTY_SELECTED_BASE = "selected_base";
    public static final String PROPERTY_SELECTED_SLEEP_TRACKER = "selected_sleep_tracker";
    public static final String PROPERTY_TIME_TO_SKIP_SECONDS = "time_to_skip_seconds";

    public static final String PROPERTY_TRY_AGAIN_BUTTON = "try_again_button";
    public static final String PROPERTY_CALL_US_BUTTON = "call_us_button";
    public static final String PROPERTY_CLOSE_BUTTON = "close_button";

    public static final String EVENT_DASHBOARD_SLEEP_SCORE_CLICKED = "dashboard_view_sleep_detail";
    public static final String EVENT_DASHBOARD_TOUCH_TRACK_SLEEP = "dashboard_touch_track_sleep";
    public static final String EVENT_SLEEP_SCREEN_STOP_TRACKING = "sleep_screen_stop_tracking";
    public static final String EVENT_FIRMNESS_CONTROL_TOUCH = "firmness_control_touch";
    public static final String EVENT_BASE_POSITION_CONTROL_TOUCH = "base_position_control_touch";
    public static final String EVENT_MASSAGE_CONTROL_TOUCH = "base_massage_control_touch";
    public static final String EVENT_DASHBOARD_VIEW_TRACKING = "dashboard_view_tracking";
    public static final String EVENT_DASHBOARD_VIEW_MATTRESS = "dashboard_view_mattress";
    public static final String EVENT_DASHBOARD_VIEW_POSITION = "dashboard_view_position";
    public static final String EVENT_DASHBOARD_VIEW_MASSAGE = "dashboard_view_massage";
    public static final String EVENT_DASHBOARD_VIEW_SETTINGS = "dashboard_view_settings";
    public static final String EVENT_DASHBOARD_VIEW_WEEKLY_STATS = "dashboard_view_weekly_stats";
    public static final String EVENT_DASHBOARD_VIEW_DAILY_STATS = "dashboard_view_daily_stats";
    public static final String EVENT_SLEEP_SESSION_END = "sleep_session_end";

    public static final String EVENT_ONBOARDING_TOUCH_FIRMNESS ="onboarding_touch_firmness";
    public static final String EVENT_ONBOARDING_TOUCH_MASSAGE ="onboarding_touch_massage";
    public static final String EVENT_ONBOARDING_TOUCH_POSITION ="onboarding_touch_position";

    public static final String PROPERTY_DID_TOUCH_CONTROL = "did_touch_control";


    public static final String EVENT_PREFERENCES_RESET_APP = "preferences_reset_app";

    public static final String PROPERTY_SIDE = "side";
    public static final String PROPERTY_PREFERENCE = "preference";
    public static final String PROPERTY_COMMAND = "command";

    public static final String PROPERTY_ORIGIN = "origin";

    public static final String PROPERTY_SLEEP_DATE = "sleep_date";
    public static final String PROPERTY_SLEEP_SCORE = "sleep_score";
    public static final String PROPERTY_MATTRESS_FIRMNESS = "mattress_firmness";
    public static final String PROPERTY_TOTAL_HOURS_SLEPT = "total_hours_slept";
    public static final String PROPERTY_TIMES_OUT_OF_BED = "times_out_of_bed";
    public static final String PROPERTY_ERROR = "error";

    public static final String VALUE_COMMAND_PRESET_REST = "preset_rest";
    public static final String VALUE_COMMAND_PRESET_RECLINE = "preset_recline";
    public static final String VALUE_COMMAND_PRESET_RELAX = "preset_relax";
    public static final String VALUE_COMMAND_PRESET_RECOVER = "preset_recover";
    public static final String VALUE_COMMAND_ADJUST_HEAD_UP = "adjust_head_up";
    public static final String VALUE_COMMAND_ADJUST_HEAD_DOWN = "adjust_head_down";
    public static final String VALUE_COMMAND_ADJUST_FOOT_UP = "adjust_foot_up";
    public static final String VALUE_COMMAND_ADJUST_FOOT_DOWN = "adjust_foot_down";
    public static final String VALUE_COMMAND_INTENSITY = "intensity";
    public static final String VALUE_COMMAND_TIMER = "timer";

    public static final String VALUE_ORIGIN_TOUCH = "touch";
    public static final String VALUE_ORIGIN_GRAPH = "graph";
    public static final String VALUE_ORIGIN_BEST_NIGHT = "best_night";
    public static final String VALUE_ORIGIN_WORST_NIGHT = "worst_night";


    public static final String USER_PROPERTY_OWNS_ADJUSTABLE_MATTRESS = "owns_adjustable_mattress";
    public static final String USER_PROPERTY_OWNS_SLEEP_TRACKER = "owns_sleep_tracker";
    public static final String USER_PROPERTY_OWNS_BASE = "owns_base";
    public static final String USER_PROPERTY_SLEEP_HOUR_GOAL = "sleep_hour_goal";
    public static final String USER_PROPERTY_GENDER = "gender";
    public static final String USER_PROPERTY_AGE = "age";
    public static final String USER_PROPERTY_SIX_WSC_SUBSCRIBER = "six_wsc_subscriber";
    public static final String USER_PROPERTY_SIDE_OF_BED = "side_of_bed";
    public static final String USER_PROPERTY_USER_EMAIL = "user_email";

    public void setSleepGoal(float sleepTargetTime) {

        String sleepHourGoal = "";

        if ( sleepTargetTime >= 6f && sleepTargetTime < 7f ) {
            sleepHourGoal = "6-7";
        } else if ( sleepTargetTime >= 7f && sleepTargetTime < 8f ) {
            sleepHourGoal = "7-8";
        } else if ( sleepTargetTime >= 8f && sleepTargetTime < 9f ) {
            sleepHourGoal = "8-9";
        } else if ( sleepTargetTime >= 9f && sleepTargetTime < 10f ) {
            sleepHourGoal = "9-10";
        }

        logUserProperty(USER_PROPERTY_SLEEP_HOUR_GOAL, sleepHourGoal);
    }

    public void setProfile(String sex, String age, String emailAddress) {
        logUserProperty(USER_PROPERTY_GENDER,sex);
        logUserProperty(USER_PROPERTY_AGE,age);
        logUserProperty(USER_PROPERTY_SIX_WSC_SUBSCRIBER, Boolean.toString(StringUtils.isNotEmpty(emailAddress)));
    }

    public void setUserOwnsBase(boolean ownsBase) {
        logUserProperty(USER_PROPERTY_OWNS_BASE, Boolean.toString(ownsBase));
    }

    public void setUserOwnsMattress(boolean ownsMattress) {
        logUserProperty(USER_PROPERTY_OWNS_ADJUSTABLE_MATTRESS, Boolean.toString(ownsMattress));
    }

    public void setUserOwnsTracker(boolean ownsTracker) {
        logUserProperty(USER_PROPERTY_OWNS_SLEEP_TRACKER, Boolean.toString(ownsTracker));
    }

    public void setUserSideOfBed(String sideOfBed) {
        logUserProperty(USER_PROPERTY_SIDE_OF_BED, sideOfBed);
    }

    public void logSleep(Sleep sleep) {
        HashMap<String, Object> attrs = new HashMap<>();

        attrs.put(PROPERTY_SLEEP_DATE, SleepService.getYYYYMMDDD(SleepService.getCalendar((int) sleep.getSleepId())));
        attrs.put(PROPERTY_SLEEP_SCORE, sleep.getTotalSleepScore());

        if (SleepSenseDeviceService.instance().hasPumpDevice()) {
            attrs.put(PROPERTY_MATTRESS_FIRMNESS, sleep.getMattressFirmness());
        }

        attrs.put(PROPERTY_TOTAL_HOURS_SLEPT, sleep.getSleepTotalTime());
        attrs.put(PROPERTY_TIMES_OUT_OF_BED, sleep.getTimesOutOfBed());

        logEvent(EVENT_SLEEP_SESSION_END, attrs);
    }

    public void logItemsSelected(boolean hasPump, boolean hasTracker, boolean hasBase) {

        HashMap<String, Object> attrs = new HashMap<>();

        attrs.put(PROPERTY_SELECTED_MATTRESS, hasPump);
        attrs.put(PROPERTY_SELECTED_BASE, hasBase);
        attrs.put(PROPERTY_SELECTED_SLEEP_TRACKER, hasTracker);

        logEvent(EVENT_SETUP_ITEMS_SELECTED, attrs);
    }

    public void logDashboardViewTracking() {
        logEvent(EVENT_DASHBOARD_VIEW_TRACKING, null);
    }

    public void logDashboardViewMattress() {
        logEvent(EVENT_DASHBOARD_VIEW_MATTRESS, null);
    }

    public void logDashboardViewPosition() {
        logEvent(EVENT_DASHBOARD_VIEW_POSITION, null);
    }

    public void logDashboardViewMassage() {
        logEvent(EVENT_DASHBOARD_VIEW_MASSAGE, null);
    }

    public void logDashboardViewSettings() {
        logEvent(EVENT_DASHBOARD_VIEW_SETTINGS, null);
    }

    public void logSetupErrorResolvingTryAgain() {
        logEvent(EVENT_SETUP_ERROR_RESOLVING, attrs()
                .put(PROPERTY_TRY_AGAIN_BUTTON, true)
                .put(PROPERTY_CALL_US_BUTTON, false)
                .put(PROPERTY_CLOSE_BUTTON, false)
                .build());
    }

    public void logSetupErrorResolvingCallUs() {
        logEvent(EVENT_SETUP_ERROR_RESOLVING, attrs()
                .put(PROPERTY_TRY_AGAIN_BUTTON, false)
                .put(PROPERTY_CALL_US_BUTTON, true)
                .put(PROPERTY_CLOSE_BUTTON, false)
                .build());
    }

    public void logCloseSetup() {
        logEvent(EVENT_CLOSE_SETUP, null);
    }

    public void logSetupTimeToSkipLayOnBed(float time) {
        logEvent(EVENT_SETUP_TIME_TO_SKIP_LAY_ON_BED, attrs()
                .put(PROPERTY_TIME_TO_SKIP_SECONDS, time)
                .build());
    }

    public void logOnboardingTouchFirmness(boolean didTouchControl) {
        logEvent(EVENT_ONBOARDING_TOUCH_FIRMNESS, attrs()
                .put(PROPERTY_DID_TOUCH_CONTROL, didTouchControl)
                .build());
    }

    public void logOnboardingTouchPosition(boolean didTouchControl) {
        logEvent(EVENT_ONBOARDING_TOUCH_POSITION, attrs()
                .put(PROPERTY_DID_TOUCH_CONTROL, didTouchControl)
                .build());
    }

    public void logOnboardingTouchMassage(boolean didTouchControl) {
        logEvent(EVENT_ONBOARDING_TOUCH_MASSAGE, attrs()
                .put(PROPERTY_DID_TOUCH_CONTROL, didTouchControl)
                .build());
    }

    public void logPreferencesResetApp() {
        logEvent(EVENT_PREFERENCES_RESET_APP, null);
    }

    public void logSleepScreenStopTracking() {
        logEvent(EVENT_SLEEP_SCREEN_STOP_TRACKING, null);
    }

    public void logDashboardSleepScoreClicked() {
        logEvent(EVENT_DASHBOARD_SLEEP_SCORE_CLICKED, null);
    }

    public void logDashboardTouchTrackSleep() {
        logEvent(EVENT_DASHBOARD_TOUCH_TRACK_SLEEP, null);
    }

    public void logDashboardViewDailyStats(String origin) {
        logEvent(EVENT_DASHBOARD_VIEW_DAILY_STATS, attrs()
                .put(PROPERTY_ORIGIN, origin)
                .build());
    }

    public void logDashboardViewWeeklyStats(String origin) {
        logEvent(EVENT_DASHBOARD_VIEW_WEEKLY_STATS, attrs()
                .put(PROPERTY_ORIGIN, origin)
                .build());
    }

    public void logFirmnessControlTouch(String side, String preference) {
        logEvent(EVENT_FIRMNESS_CONTROL_TOUCH, attrs()
                .put(PROPERTY_SIDE, side)
                .put(PROPERTY_PREFERENCE, preference)
                .build());
    }

    public void logMassageControlTouch(String command) {
        logEvent(EVENT_MASSAGE_CONTROL_TOUCH, attrs()
                .put(PROPERTY_COMMAND, command)
                .build());
    }

    public void logPositionControlTouch(String command) {
        logEvent(EVENT_BASE_POSITION_CONTROL_TOUCH, attrs()
                .put(PROPERTY_COMMAND, command)
                .build());
    }

    private AttributesMap attrs() {
        return new AttributesMap();
    }

    private class AttributesMap {

        HashMap<String, Object> attrs;

        public AttributesMap() {
            attrs = new HashMap<>();
        }

        public AttributesMap put(String key, Object value) {
            attrs.put(key, value);
            return this;
        }

        public HashMap<String, Object> build() {
            return attrs;
        }
    }
}
