package au.com.ahbeard.sleepsense.bluetooth.pump;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by neal on 7/03/2016.
 */
public class PumpEvent {

    /*

    /// An example pump mStatus string (new format): "EX0250360250280200"

    EX{left memory 3 digits}{left pressure 3 digits}{right memory 3 digits}{right pressure 3 digits}{4 hex mStatus}

    we don't have the EX (don't need it).

    static let LeftChamberActive        = PumpMainframeStateOptionSet(rawValue: 0x0100)
    static let RightChamberActive       = PumpMainframeStateOptionSet(rawValue: 0x0200)
    static let Reinflating              = PumpMainframeStateOptionSet(rawValue: 0x0040)
    static let SelfAdapting             = PumpMainframeStateOptionSet(rawValue: 0x0010)
    static let SettingMemoryPressure    = PumpMainframeStateOptionSet(rawValue: 0x0008)
    static let CheckingPressure         = PumpMainframeStateOptionSet(rawValue: 0x0004)
    static let Deflating                = PumpMainframeStateOptionSet(rawValue: 0x0002)
    static let Inflating                = PumpMainframeStateOptionSet(rawValue: 0x0001)

    */

    public enum PumpStatus {
        LeftChamberActive,
        RightChamberActive,
        ReInflating,
        SelfAdapting,
        SettingMemoryPressure,
        CheckingPressure,
        Deflating,
        Inflating
    }

    private String mRawData;
    private int mLeftMemory;
    private int mLeftPressure;
    private int mRightMemory;
    private int mRightPressure;
    private Set<PumpStatus> mStatus;

    public PumpEvent(String rawData) {

        mRawData = rawData;

        mLeftMemory = Integer.parseInt(mRawData.substring(2, 5));
        mLeftPressure = Integer.parseInt(mRawData.substring(5, 8));
        mRightMemory = Integer.parseInt(mRawData.substring(8, 11));
        mRightPressure = Integer.parseInt(mRawData.substring(11, 14));
        mStatus = new HashSet<>();

        int rawStatus = Integer.parseInt(mRawData.substring(15, 18), 16);

        if ((rawStatus & 0x0100) == 0x0100) {
            mStatus.add(PumpStatus.LeftChamberActive);
        }

        if ((rawStatus & 0x0200) == 0x0200) {
            mStatus.add(PumpStatus.RightChamberActive);
        }

        if ((rawStatus & 0x0040) == 0x0040) {
            mStatus.add(PumpStatus.ReInflating);
        }

        if ((rawStatus & 0x0010) == 0x0010) {
            mStatus.add(PumpStatus.SelfAdapting);
        }

        if ((rawStatus & 0x0008) == 0x0008) {
            mStatus.add(PumpStatus.SettingMemoryPressure);
        }

        if ((rawStatus & 0x0004) == 0x0004) {
            mStatus.add(PumpStatus.CheckingPressure);
        }

        if ((rawStatus & 0x0002) == 0x0002) {
            mStatus.add(PumpStatus.Deflating);
        }

        if ((rawStatus & 0x0001) == 0x0001) {
            mStatus.add(PumpStatus.Inflating);
        }

    }

    public int getLeftMemory() {
        return mLeftMemory;
    }

    public int getRightMemory() {
        return mRightMemory;
    }

    public int getLeftPressure() {
        return mLeftPressure;
    }

    public int getRightPressure() {
        return mRightPressure;
    }

    public Set<PumpStatus> getStatuses() {
        return mStatus;
    }

    public boolean isAdjusting()  {
        return mStatus.contains(PumpStatus.Inflating)||mStatus.contains(PumpStatus.Deflating);
    }

    public boolean isAdjustingOrCheckingPressure()  {
        return mStatus.contains(PumpStatus.Inflating)||mStatus.contains(PumpStatus.Deflating)||mStatus.contains(PumpStatus.CheckingPressure);
    }

    public int getPressure(String sideOfBed) {
        if ( "left".equalsIgnoreCase(sideOfBed) ) {
            return getLeftPressure();
        } else {
            return getRightPressure();
        }
    }


    @Override
    public String toString() {
        return "PumpEvent{" +
                "mRawData='" + mRawData + '\'' +
                ", mLeftMemory=" + mLeftMemory +
                ", mLeftPressure=" + mLeftPressure +
                ", mRightMemory=" + mRightMemory +
                ", mRightPressure=" + mRightPressure +
                ", mStatus=" + mStatus +
                '}';
    }

}
