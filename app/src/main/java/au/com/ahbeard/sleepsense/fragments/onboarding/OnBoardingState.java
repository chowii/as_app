package au.com.ahbeard.sleepsense.fragments.onboarding;

/**
 * Created by neal on 30/05/2016.
 */
public class OnBoardingState {

    public enum State {
        Locating,
        ChoosingDevices,
        Acquiring,
        RequiredDevicesFound,
        DevicesMissingAllowRetry,
        DevicesMissingShowHelp,
        Inflating,
        InflationComplete,
        InflationError,
        FirmnessControls,
        PositionControls,
        MassageControls
    }

    public int failedAttempts;

    public State state;

    public String sideOfBed;

    public boolean requiredBase;
    public boolean requiredPump;
    public boolean requiredTracker;

    public int numberOfTrackersRequired = 2;

    public boolean foundBase;
    public boolean foundPump;
    public boolean foundTracker;
}
