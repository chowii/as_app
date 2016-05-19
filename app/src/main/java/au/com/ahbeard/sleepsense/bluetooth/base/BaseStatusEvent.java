    package au.com.ahbeard.sleepsense.bluetooth.base;

import au.com.ahbeard.sleepsense.utils.ByteUtils;

/**
 * Created by neal on 4/04/2016.
 */
public class BaseStatusEvent {

    private int mHeadPosition;
    private int mFootPosition;
    private int mHeadMassageStatus;
    private int mFootMassageStatus;
    private int mMassageTimer;
    private int mMultiStatus;
    private int mTimerLightStatus;

    /*

- 83: Checksum
- 01: LED Status
- 00: Multi Status
- 8CE80000: Massage Timer (32-bit unsigned) (LSB looks like left side from tests)
- 03: Foot Massage Status
- 03: Head Massage Status
- 0000: Foot Position (ADC)
- 0000: Head Position (ADC)
- EDFE16: Static

     */

    // ED FE 16 98 0C CF 24 00 00 00 00 00 00 00 FF 68

    public BaseStatusEvent(byte[] value) {

        // Make an allowance for the terrible firmware.
        mHeadPosition = ByteUtils.readLEUint16(3, value);

        if (mHeadPosition > 0x8fff) {
            mHeadPosition = 0;
        }

        // Make an allowance for the terrible firmware.
        mFootPosition = ByteUtils.readLEUint16(5, value);

        if (mFootPosition > 0x8fff) {
            mFootPosition = 0;
        }

        mHeadMassageStatus = ByteUtils.readLEUint8(7, value);
        mFootMassageStatus = ByteUtils.readLEUint8(8, value);
        mMassageTimer = ByteUtils.readLEUint32(9, value);
        mMultiStatus = ByteUtils.readLEUint8(13, value);
        mTimerLightStatus = ByteUtils.readLEUint8(14, value);

    }

    public static BaseStatusEvent safeInstance(byte[] value) {
        if (value.length == 16) {
            return new BaseStatusEvent(value);
        } else {
            return null;
        }
    }

    public int getHeadPosition() {
        return mHeadPosition;
    }

    public int getFootPosition() {
        return mFootPosition;
    }

    public int getHeadMassageStatus() {
        return mHeadMassageStatus;
    }

    public int getFootMassageStatus() {
        return mFootMassageStatus;
    }

    public int getMassageTimer() {
        return mMassageTimer;
    }

    public int getMultiStatus() {
        return mMultiStatus;
    }

    public int getTimerLightStatus() {
        return mTimerLightStatus;
    }

    public boolean isHeadMotorRunning() {
        return (mMultiStatus & 0x01) == 0x01;
    }

    public boolean isFootMotorRunning() {
        return (mMultiStatus & 0x02) == 0x02;
    }

    public boolean isTimerLightActive() {
        return mTimerLightStatus != 0xff;
    }

    public boolean isTimerLightActive(int timerLightIndex) {
        return mTimerLightStatus - 1 == timerLightIndex;
    }


    @Override
    public String toString() {
        return "BaseStatusEvent{" +
                "mHeadPosition=" + mHeadPosition +
                ", mFootPosition=" + mFootPosition +
                ", mHeadMassageStatus=" + mHeadMassageStatus +
                ", mFootMassageStatus=" + mFootMassageStatus +
                ", mMassageTimer=" + mMassageTimer +
                ", mMultiStatus=" + mMultiStatus +
                ", mTimerLightStatus=" + mTimerLightStatus +
                '}';
    }


}
