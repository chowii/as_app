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

    public static final Map<Integer,Firmness> PRESSURE_TO_FIRMNESS;

    static  {

        Map<Integer,Firmness> _VALUES = new HashMap<>();

        for (int p=0;p<12;p++) {
            _VALUES.put(p,new Firmness("Plush"));
        }
        for (int p=12;p<24;p++) {
            _VALUES.put(p,new Firmness("Medium Plush"));
        }
        for (int p=24;p<36;p++) {
            _VALUES.put(p,new Firmness("Medium"));
        }
        for (int p=36;p<48;p++) {
            _VALUES.put(p,new Firmness("Medium Firm"));
        }
        for (int p=48;p<=60;p++) {
            _VALUES.put(p,new Firmness("Firm"));
        }

        PRESSURE_TO_FIRMNESS = MapUtils.unmodifiableMap(_VALUES);

    }

    private Firmness( String mLabel) {
        this.mLabel = mLabel;
    }

    public String getLabel() {
        return mLabel;
    }

    public static Firmness getFirmnessForPressure(int value) {

        if ( PRESSURE_TO_FIRMNESS.containsKey(value)) {
            return PRESSURE_TO_FIRMNESS.get(value);
        }

        return new Firmness("Unknown");

    }

    public static float getControlValueForPressure(int pressure) {
        return pressure/60f;
    }

    /**
     * Snap the control to useable values.
     *
     * @param controlValue
     * @return
     */
    public static float snapControlValue(float controlValue) {
        return Math.round(controlValue*10f)/10f;
    }

    /**
     *
     * @param controlValue
     * @return
     */
    public static int getDrawableLevelForControlValue(float controlValue) {
        return Math.round(controlValue*10f);
    }

    /**
     *
     * @param targetValue
     * @return
     */
    public static int getPressureForControlValue(float targetValue) {
        return (int)(targetValue*60);
    }
}
