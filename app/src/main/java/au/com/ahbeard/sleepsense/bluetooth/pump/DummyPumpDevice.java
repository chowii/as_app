package au.com.ahbeard.sleepsense.bluetooth.pump;

import java.util.UUID;

/**
 * Created by neal on 4/03/2016.
 */
public class DummyPumpDevice extends PumpDevice {

    @Override
    public UUID getServiceUUID() {
        return UUID.fromString("713d0000-503e-4c75-ba94-3148f18d941e");
    }

    @Override
    public UUID getReadCharacteristicUUID() {
        return UUID.fromString("713d0002-503e-4c75-ba94-3148f18d941e");
    }

    @Override
    public UUID getWriteCharacteristicUUID() {
        return UUID.fromString("713d0003-503e-4c75-ba94-3148f18d941e");
    }
}
