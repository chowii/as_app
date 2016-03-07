package au.com.ahbeard.sleepsense.bluetooth.pump;

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
    
    public static PumpCommand inflate(Chamber chamber) {
        PumpCommand pumpCommand = new PumpCommand();
        pumpCommand.writeByte('S');
        pumpCommand.writeByte(chamber==Chamber.Left?'L':'R');
        pumpCommand.writeByte(Inflate);
        return pumpCommand;
    }

    public static PumpCommand deflate(Chamber chamber) {
        PumpCommand pumpCommand = new PumpCommand();
        pumpCommand.writeByte('S');
        pumpCommand.writeByte(chamber==Chamber.Left?'L':'R');
        pumpCommand.writeByte(Deflate);
        return pumpCommand;
    }

    public static PumpCommand stop(Chamber chamber) {
        PumpCommand pumpCommand = new PumpCommand();
        pumpCommand.writeByte('S');
        pumpCommand.writeByte(chamber==Chamber.Left?'L':'R');
        pumpCommand.writeByte(Stop);
        return pumpCommand;
    }

}
