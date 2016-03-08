package au.com.ahbeard.sleepsense.bluetooth.pump;

import android.support.annotation.NonNull;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothCommand;

/**
 * Created by neal on 7/03/2016.
 */
public class PumpCommand extends BluetoothCommand {

    public enum Chamber {
        Left,
        Right
    }

    private static final char LeftChamber = 'L';
    private static final char RightChamber = 'R';

    // StandardPumpCommand
    // Format: SAB\n
    // Where: S is the string literal "S" and \n is the string literal "\n"
    // A is the string literal "R" or "L" depending on which chamber the command is for (right, left)
    // B is a string literal of the value "1"-"9" or "a" (depending on command)

    // Extended Pump Command
    // Format: SABCCC\n
    // Where: S is the string literal "S" and \n is the string literal "\n"
    // A is the string literal "R" or "L" depending on which chamber the command is for (right, left)
    // B is a string literal of the value "b" or "c" (depending on command)
    // CCC is a three-character string literal expressing pressure in millibar e.g. "025"

    private static final char CheckMemoryValue = '1';
    private static final char CheckPressure = '2';
    private static final char Inflate = '3';
    private static final char Deflate = '4';
    private static final char RunForMemoryValue = '5';
    private static final char SelfAdapt = '6';
    private static final char SaveMemoryValue = '7';
    private static final char ReInflate = '8';
    private static final char Stop = '9';
    private static final char AdjustZero = 'a';

    @NonNull
    public static PumpCommand checkMemoryValue(Chamber chamber) {
        return createStandardPumpCommand(chamber, CheckMemoryValue);
    }

    @NonNull
    public static PumpCommand checkPressure(Chamber chamber) {
        return createStandardPumpCommand(chamber, CheckPressure);
    }

    @NonNull
    public static PumpCommand inflate(Chamber chamber) {
        return createStandardPumpCommand(chamber, Inflate);
    }

    @NonNull
    public static PumpCommand deflate(Chamber chamber) {
        return createStandardPumpCommand(chamber, Deflate);
    }

    @NonNull
    public static PumpCommand runForMemoryValue(Chamber chamber) {
        return createStandardPumpCommand(chamber, RunForMemoryValue);
    }

    @NonNull
    public static PumpCommand selfAdapt(Chamber chamber) {
        return createStandardPumpCommand(chamber, SelfAdapt);
    }

    @NonNull
    public static PumpCommand saveMemoryValue(Chamber chamber, int millibar) {
        return createAdvancedPumpCommand(chamber, SaveMemoryValue, millibar);
    }

    @NonNull
    public static PumpCommand reInflate(Chamber chamber) {
        return createStandardPumpCommand(chamber, ReInflate);
    }

    @NonNull
    public static PumpCommand stop(Chamber chamber) {
        return createStandardPumpCommand(chamber, Stop);
    }

    @NonNull
    public static PumpCommand adjustZero(Chamber chamber, int millibar) {
        return createAdvancedPumpCommand(chamber, AdjustZero, millibar);
    }

    private static PumpCommand createStandardPumpCommand(Chamber chamber, char command) {
        PumpCommand pumpCommand = new PumpCommand();
        pumpCommand.writeByte('S');
        pumpCommand.writeByte(chamber == Chamber.Left ? 'L' : 'R');
        pumpCommand.writeByte(command);
        return pumpCommand;
    }

    private static PumpCommand createAdvancedPumpCommand(Chamber chamber, char command, int value) {
        PumpCommand pumpCommand = new PumpCommand();
        pumpCommand.writeByte('S');
        pumpCommand.writeByte(chamber == Chamber.Left ? 'L' : 'R');
        pumpCommand.writeByte(command);
        pumpCommand.writeBytes(String.format("%03d",value%1000).getBytes());
        return pumpCommand;
    }

}
