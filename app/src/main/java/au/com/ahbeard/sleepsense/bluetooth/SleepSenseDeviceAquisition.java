package au.com.ahbeard.sleepsense.bluetooth;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.bluetooth.base.BaseDevice;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice;
import au.com.ahbeard.sleepsense.bluetooth.tracker.TrackerDevice;

/**
 * Created by neal on 30/05/2016.
 */
public class SleepSenseDeviceAquisition {

    private List<BaseDevice> mBaseDevices = new ArrayList<>();
    private List<PumpDevice> mPumpDevices = new ArrayList<>();
    private List<TrackerDevice> mTrackerDevices = new ArrayList<>();

    public List<BaseDevice> getBaseDevices() {
        return mBaseDevices;
    }

    public List<PumpDevice> getPumpDevices() {
        return mPumpDevices;
    }

    public List<TrackerDevice> getTrackerDevices() {
        return mTrackerDevices;
    }
}
