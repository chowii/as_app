package au.com.ahbeard.sleepsense.model;

import android.util.Log;

import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neal on 29/03/2016.
 */
public class Firmness {

    private String mLabel;
    private String mAnalyticsValue;

    public static int MAX_PRESSURE = 40;
    public static int SEGMENTS = 10;

    public static final Map<Integer,Firmness> PRESSURE_TO_FIRMNESS;

    static  {

        Map<Integer,Firmness> _VALUES = new HashMap<>();

        int range = MAX_PRESSURE / 5;

        for (int p=0;p<range;p++) {
            _VALUES.put(p,new Firmness("Plush"));
        }
        for (int p=range;p<range*2;p++) {
            _VALUES.put(p,new Firmness("Medium Plush"));
        }
        for (int p=range*2;p<range*3;p++) {
            _VALUES.put(p,new Firmness("Medium"));
        }
        for (int p=range*3;p<range*4;p++) {
            _VALUES.put(p,new Firmness("Medium Firm"));
        }
        for (int p=range*4;p<=range*5;p++) {
            _VALUES.put(p,new Firmness("Firm"));
        }

        PRESSURE_TO_FIRMNESS = MapUtils.unmodifiableMap(_VALUES);

    }

    private Firmness( String mLabel) {
        this.mLabel = mLabel;
        this.mAnalyticsValue = mLabel.replace(" ","");
    }

    public String getLabel() {
        return mLabel;
    }

    public String getAnalyticsValue() {
        return mAnalyticsValue;
    }

    public static Firmness getFirmnessForPressure(int value) {

        if ( PRESSURE_TO_FIRMNESS.containsKey(value)) {
            return PRESSURE_TO_FIRMNESS.get(value);
        }

        return new Firmness("Unknown");

    }

    public static float getControlValueForPressure(int pressure) {
        return pressure/(float)MAX_PRESSURE;
    }

    /**
     * Snap the control to useable values.
     *
     * @param controlValue
     * @return
     */
    public static float snapControlValue(float controlValue) {
        return Math.round(controlValue*(float)SEGMENTS)/(float)SEGMENTS;
    }

    /**
     *
     * @param targetValue
     * @return
     */
    public static int getPressureForControlValue(float targetValue) {
        return (int)(targetValue*MAX_PRESSURE);
    }

    /**
     *
     * @param targetValue
     * @return
     */
    public static String getAnalyticsValueForControlValue(float targetValue) {

        Firmness firmness = PRESSURE_TO_FIRMNESS.get((int)(targetValue*MAX_PRESSURE));

        if ( firmness != null ) {
            return firmness.getAnalyticsValue();
        } else {
            return "Unknown";
        }

    }
}
