package au.com.ahbeard.sleepsense.bluetooth.base;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothUtils;
import au.com.ahbeard.sleepsense.bluetooth.CharacteristicWriteOperation;
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpCommand;

/**
 * Created by neal on 7/03/2016.
 */
public class BaseCommand extends CharacteristicWriteOperation {

    static Map<String, Integer> _COMMANDS = new HashMap<>();

    static {

        _COMMANDS.put("MotorStop", 0x00000000);               // This is often sent between commands to stop whatever was going last time
        _COMMANDS.put("HeadPositionUp", 0x00000001);
        _COMMANDS.put("HeadPositionDown", 0x00000002);
        _COMMANDS.put("FootPositionUp", 0x00000004);
        _COMMANDS.put("FootPositionDown", 0x00000008);
        _COMMANDS.put("PresetZeroG", 0x00001000);
        _COMMANDS.put("PresetLounge", 0x00002000);
        _COMMANDS.put("PresetTV", 0x00004000);
        _COMMANDS.put("PresetAntiSnore", 0x00008000);
        _COMMANDS.put("PresetFlat", 0x08000000);
        _COMMANDS.put("Reset", 0x08001000);
        _COMMANDS.put("HeadMassageIncrease", 0x00000800);
        _COMMANDS.put("HeadMassageDecrease", 0x00800000);
        _COMMANDS.put("FootMassageIncrease", 0x00000400);
        _COMMANDS.put("FootMassageDecrease", 0x01000000);
        _COMMANDS.put("MassageAllIntensityStep", 0x00000100); // Steps through intensity level of massager for all
        _COMMANDS.put("ToggleAux", 0x00020000);               // This one is used in the sample
        _COMMANDS.put("ToggleLamp", 0x00040000);
        _COMMANDS.put("MassageTimer", 0x00000200);
        _COMMANDS.put("MassageIntensity1", 0x00080000);
        _COMMANDS.put("MassageIntensity2", 0x00100000);
        _COMMANDS.put("MassageIntensity3", 0x00200000);

    }

    public BaseCommand(UUID serviceUUID, UUID characteristicUUID) {
        super(serviceUUID, characteristicUUID);
    }

    public static BaseCommand headPositionUp() {
        return createGeneralBaseCommand("HeadPositionUp");
    }

    public static BaseCommand headPositionDown() {
        return createGeneralBaseCommand("HeadPositionDown");
    }

    public static BaseCommand footPositionUp() {
        return createGeneralBaseCommand("FootPositionUp");
    }

    public static BaseCommand footPositionDown() {
        return createGeneralBaseCommand("FootPositionDown");
    }

    public static BaseCommand headMassageIncrease() {
        return createGeneralBaseCommand("HeadMassageIncrease");
    }

    public static BaseCommand headMassageDecrease() {
        return createGeneralBaseCommand("HeadMassageDecrease");
    }

    public static BaseCommand footMassageIncrease() {
        return createGeneralBaseCommand("FootMassageIncrease");
    }

    public static BaseCommand footMassageDecrease() {
        return createGeneralBaseCommand("FootMassageDecrease");
    }

    public static BaseCommand presetFlat() {
        return createGeneralBaseCommand("PresetFlat");
    }

    public static BaseCommand presetLounge() {
        return createGeneralBaseCommand("PresetLounge");
    }

    public static BaseCommand presetZeroG() {
        return createGeneralBaseCommand("PresetZeroG");
    }

    public static BaseCommand presetTV() {
        return createGeneralBaseCommand("PresetTV");
    }

    public static BaseCommand motorStop() {
        return createGeneralBaseCommand("MotorStop");
    }

    public static BaseCommand wholeBody() {
        return createGeneralBaseCommand("MassageAllIntensityStep");
    }

    public static BaseCommand timer() {
        return createGeneralBaseCommand("MassageTimer");
    }

    public static BaseCommand reset() {
        return createGeneralBaseCommand("Reset");
    }

    public static BaseCommand createGeneralBaseCommand(String identifier) {

        BaseCommand baseCommand = new BaseCommand(
                BluetoothUtils.uuidFrom16BitUuid(0xffe5),
                BluetoothUtils.uuidFrom16BitUuid(0xffe9));

        baseCommand.writeByte(0xe5);
        baseCommand.writeByte(0xfe);
        baseCommand.writeByte(0x16);
        baseCommand.writeUInt32(_COMMANDS.get(identifier));
        baseCommand.computeAndWriteChecksum();

        return baseCommand;

    }

    /*
        func computeChecksumForProper() -> Checksum {
        // Checksum is calculated by summing all bytes,
        // allowing overflow, then inverting the result
        var checksum: Checksum = 0x00
        for byte in proper {
            // add byte, allowing overflow
            checksum = checksum &+ byte
        }
        // return sum, inverted
        return ~checksum
    }
     */
    public void computeAndWriteChecksum() {

        int checksum = 0;
        byte[] value = getValue();

        // Add all the values.
        for (int i = 0; i < value.length; i++) {
            checksum = checksum + value[i];
        }

        // Invert the result.
        checksum = ~checksum;

        // Write only the low 8 bits.
        writeByte(checksum);
    }
}
