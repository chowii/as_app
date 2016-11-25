package au.com.ahbeard.sleepsense.utils;

/**
 * Created by vimal on 11/15/2016.
 */

public class GlobalVars {

    public static final String SHARED_PREFERENCE = "SHARED_PREFERENCE";
    public static final String SHARED_PREFERENCE_MATTRESS_TYPE = "SHARED_PREFERENCE_MATTRESS_TYPE";
    public static final String SHARED_PREFERENCE_CONNECTING_PUMP_SIDE = "SHARED_PREFERENCE_CONNECTING_PUMP_SIDE";
    public static final String SHARED_PREFERENCE_USER_HEIGHT = "SHARED_PREFERENCE_USER_HEIGHT";
    public static final String SHARED_PREFERENCE_USER_WEIGHT = "SHARED_PREFERENCE_USER_WEIGHT";
    public static final String SHARED_PREFERENCE_USER_GENDER = "SHARED_PREFERENCE_USER_GENDER";
    public static final String SHARED_PREFERENCE_SLEEP_SCENARIO = "SHARED_PREFERENCE_SLEEP_SCENARIO";

    public static final String SELECTED_MATTRESS_TYPE = "SELECTED_MATTRESS_TYPE";
    public static final String QUESTIONNAIRE_HEADER = "QUESTIONNAIRE_HEADER";
    public static final String QUESTIONNAIRE_OPTION1 = "QUESTIONNAIRE_OPTION1";
    public static final String QUESTIONNAIRE_OPTION2 = "QUESTIONNAIRE_OPTION2";
    public static final String QUESTIONNAIRE_TITLE = "QUESTIONNAIRE_TITLE";
    public static final String QUESTIONNAIRE_OTHER_TEXT = "QUESTIONNAIRE_OTHER_TEXT";
    public static final String QUESTIONNAIRE_BUTTON = "QUESTIONNAIRE_BUTTON";
    public static final String QUESTIONNAIRE_PART = "QUESTIONNAIRE_PART";
    public static final String CONNECTING_HEADER = "CONNECTING_HEADER";
    public static final String IMAGE_QUESTIONNAIRE_HEADER = "IMAGE_QUESTIONNAIRE_HEADER";
    public static final String IMAGE_QUESTIONNAIRE_LABEL1 = "IMAGE_QUESTIONNAIRE_LABEL1";
    public static final String IMAGE_QUESTIONNAIRE_LABEL2 = "IMAGE_QUESTIONNAIRE_LABEL2";
    public static final String VERTICAL_SCROLL_VALUES_MAP = "VERTICAL_SCROLL_VALUES_MAP";

    public static final String SEARCHING_PUMP_DEVICE = "Connecting with your \nmattress pump";
    public static final String SEARCHING_SLEEP_TRACKERS = "Searching for \nsleep trackers";
    public static final String SEARCHING_ADJUSTABLE_BASE = "Connecting with \nyour base";

    public static final String MATTRESS_PUMP_SIDE_QUESTION = "What side of the mattress pump did you plug in?";
    public static final String LEFT_STRING = "Left";
    public static final String RIGHT_STRING = "Right";
    public static final String SLEEP_TRACKING_QUESTION = "Did you purchase built in sleep tracking?";
    public static final String YES_STRING = "Yes";
    public static final String NO_STRING = "No";
    public static final String TRACKER_PART1_TITLE = "Don't just track your sleep \nAct on it";
    public static final String TRACKER_PART1_OTHER = "Learn from your sleep score and \nadjust your mattress to improve \nyour sleep one night at a time";
    public static final String TRACKER_PART1_BUTTON = "Continue";
    public static final String TRACKER_PART2_TITLE = "Let's get to \nknow each other";
    public static final String TRACKER_PART2_OTHER = "This will make sure we get better \nand personalised results";
    public static final String TRACKER_PART2_BUTTON = "Let's do it";
    public static final String GENDER_HEADER = "What's your gender?";
    public static final String GENDER_MALE = "Male";
    public static final String GENDER_FEMALE = "Female";
    public static final String SLEEP_SCENARIO_ALONE = "Alone";
    public static final String SLEEP_SCENARIO_WITH_PARTNER = "With a partner";
    public static final String SLEEP_SCENARIO_HEADER = "How do you usually sleep?";
    public static final String ADJUSTABLE_BASE_HEADER = "Did you purchase an \nadjustable base?";
    public static final String FRAGMENT_DIALOG_ID = "FRAGMENT_DIALOG_ID";
    public static final String HOURS = "Hours";

    public enum MattressType{
        SINGLE, QUEEENKING, SPLITKING
    }

    public enum ButtonActioned {
        SKIP, CONTINUE
    }

    public enum SleepSide {
        LEFT, RIGHT
    }

    public enum SleepScenario {
        ALONE, WITHPARTNER
    }
}
