package au.com.ahbeard.sleepsense.bluetooth.pump;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothCommand;

/**
 * Created by neal on 7/03/2016.
 */
public class PumpResponse extends BluetoothCommand {

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

    public static final String CheckMemoryValue = "1";
    public static final String CheckPressure = "2";
    public static final String Inflate = "3";
    public static final String Deflate = "4";
    public static final String RunForMemoryValue = "5";
    public static final String SelfAdapt = "6";
    public static final String SaveMemoryValue = "7";
    public static final String ReInflate = "8";
    public static final String Stop = "9";
    public static final String AdjustZero = "a";








}
