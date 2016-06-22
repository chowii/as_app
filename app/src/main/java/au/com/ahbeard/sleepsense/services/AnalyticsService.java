package au.com.ahbeard.sleepsense.services;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import au.com.ahbeard.sleepsense.bluetooth.SleepSenseDeviceService;
import au.com.ahbeard.sleepsense.model.beddit.Sleep;
import au.com.ahbeard.sleepsense.utils.StringUtils;

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
    }

    public static AnalyticsService instance() {
        if (sAnalyticsService == null) {
            sAnalyticsService = new AnalyticsService(sContext);
        }

        return sAnalyticsService;
    }

    public void logEvent(String event, Object... properties) {

        Bundle bundle = new Bundle();

        if (properties.length % 2 != 0) {
            return;
        }

        for (int i = 0; i < properties.length; i = i + 2) {

            Object value = properties[i + 1];

            if (value instanceof String) {
                bundle.putString((String) properties[i], (String) value);
            } else if (value instanceof Boolean) {
                bundle.putBoolean((String) properties[i], (Boolean) value);
            } else if (value instanceof Float) {
                bundle.putFloat((String) properties[i], (Float)value);
            }

        }

        mFirebaseAnalytics.logEvent(event, bundle);
    }

    public void logSleep(String event, Sleep sleep) {

        Bundle bundle = new Bundle();

        bundle.putString(PROPERTY_SLEEP_DATE, SleepService.getYYYYMMDDD(SleepService.getCalendar((int) sleep.getSleepId())));
        bundle.putFloat(PROPERTY_SLEEP_SCORE, sleep.getTotalSleepScore());

        if (SleepSenseDeviceService.instance().hasPumpDevice()) {
            bundle.putFloat(PROPERTY_MATTRESS_FIRMNESS, sleep.getMattressFirmness());
        }

        bundle.putFloat(PROPERTY_TOTAL_HOURS_SLEPT, sleep.getSleepTotalTime());
        bundle.putInt(PROPERTY_TIMES_OUT_OF_BED, sleep.getTimesOutOfBed());

        mFirebaseAnalytics.logEvent(event, bundle);
    }

    public void logItemsSelected(boolean hasPump, boolean hasTracker, boolean hasBase) {

        Bundle bundle = new Bundle();

        if (hasBase) {
            bundle.putBoolean(PROPERTY_SELECTED_ADJUSTABLE_MATTRESS, hasPump);
            bundle.putBoolean(PROPERTY_SELECTED_BASE, hasBase);
            bundle.putBoolean(PROPERTY_SELECTED_SLEEP_TRACKER, hasTracker);
        }

        mFirebaseAnalytics.logEvent(EVENT_SETUP_ITEMS_SELECTED, bundle);
    }

    public static final String EVENT_SETUP_ITEMS_SELECTED = "setup_items_selected";
    public static final String EVENT_CLOSE_SETUP = "close_setup";
    public static final String EVENT_SETUP_ERROR_RESOLVING = "setup_error_resolving";
    public static final String EVENT_SETUP_SETUP_TIME_TO_SKIP_LAY_ON_BED = "setup_time_to_skip_lay_on_bed";

    public static final String PROPERTY_SELECTED_ADJUSTABLE_MATTRESS = "selected_adjustable_mattress";
    public static final String PROPERTY_SELECTED_BASE = "selected_base";
    public static final String PROPERTY_SELECTED_SLEEP_TRACKER = "selected_sleep_tracker";
    public static final String PROPERTY_TIME_TO_SKIP_SECONDS = "time_to_skip_seconds";

    public static final String PROPERTY_TRY_AGAIN_BUTTON = "try_again_button";
    public static final String PROPERTY_CALL_US_BUTTON = "call_us_button";

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

    public void setUserProperty(String property, String value) {
        mFirebaseAnalytics.setUserProperty(property,value);
    }

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

        mFirebaseAnalytics.setUserProperty(AnalyticsService.USER_PROPERTY_SLEEP_HOUR_GOAL,sleepHourGoal);

    }

    public void setProfile(String sex, String age, String emailAddress) {
        mFirebaseAnalytics.setUserProperty(AnalyticsService.USER_PROPERTY_GENDER,sex);
        mFirebaseAnalytics.setUserProperty(AnalyticsService.USER_PROPERTY_AGE,age);
        mFirebaseAnalytics.setUserProperty(AnalyticsService.USER_PROPERTY_SIX_WSC_SUBSCRIBER, Boolean.toString(StringUtils.isNotEmpty(emailAddress)));
    }


    /**
     * sleep_session_end

     sleep_date (yyyy-MM-dd)
     sleep_score
     mattress_firmness ( [0-1] )
     total_hours_slept
     times_out_of_bed
     error
     */


}
