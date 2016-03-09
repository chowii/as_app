package au.com.ahbeard.sleepsense.bluetooth.base;

import java.util.HashMap;
import java.util.Map;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothWriteOperation;

/**
 * Created by neal on 7/03/2016.
 */
public class BaseCommand extends BluetoothWriteOperation {


   public static Map<String,Integer> COMMANDS = new HashMap<>();
   static Map<String,Integer> _COMMANDS = new HashMap<>();
    
   static {

       _COMMANDS.put("MotorStop",0x00000000);               // This is often sent between commands to stop whatever was going last time
       _COMMANDS.put("HeadPositionUp",0x00000001);
       _COMMANDS.put("HeadPositionDown",0x00000002);
       _COMMANDS.put("FootPositionUp",0x00000004);
       _COMMANDS.put("FootPositionDown",0x00000008);
       _COMMANDS.put("PresetZeroG",0x00001000);
       _COMMANDS.put("PresetLounge",0x00002000);
       _COMMANDS.put("PresetTV",0x00004000);
       _COMMANDS.put("PresetAntiSnore",0x00008000);
       _COMMANDS.put("PresetFlat",0x08000000);
       _COMMANDS.put("Reset",0x08001000);
       _COMMANDS.put("HeadMassageIncrease",0x00000800);
       _COMMANDS.put("HeadMassageDecrease",0x00800000);
       _COMMANDS.put("FootMassageIncrease",0x00000400);
       _COMMANDS.put("FootMassageDecrease",0x01000000);
       _COMMANDS.put("MassageAllIntensityStep",0x00000100); // Steps through intensity level of massager for all
       _COMMANDS.put("ToggleAux",0x00020000);               // This one is used in the sample
       _COMMANDS.put("ToggleLamp",0x00040000);
       _COMMANDS.put("MassageTimer",0x00000200);
       _COMMANDS.put("MassageIntensity1",0x00080000);
       _COMMANDS.put("MassageIntensity2",0x00100000);
       _COMMANDS.put("MassageIntensity3",0x00200000);

   }





    
}
