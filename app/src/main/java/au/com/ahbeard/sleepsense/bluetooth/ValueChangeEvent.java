package au.com.ahbeard.sleepsense.bluetooth;

import java.util.UUID;

/**
 * Created by neal on 8/03/2016.
 */
public class ValueChangeEvent {

    private UUID uuid;
    private byte[] value;

    public ValueChangeEvent(UUID uuid, byte[] value) {
        this.uuid = uuid;
        this.value = value;
    }

    public UUID getUuid() {
        return uuid;
    }

    public byte[] getValue() {
        return value;
    }
}
