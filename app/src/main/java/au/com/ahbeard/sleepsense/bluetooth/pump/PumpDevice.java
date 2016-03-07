package au.com.ahbeard.sleepsense.bluetooth.pump;

import java.util.UUID;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothUtils;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by neal on 4/03/2016.
 */
public class PumpDevice extends Device {

    private Observable<PumpEvent> mPumpEventObservable = PublishSubject.create();

    @Override
    public UUID getServiceUUID() {
        return BluetoothUtils.uuidFrom16BitUuid(0xffe0);
    }

    @Override
    public UUID getWriteCharacteristicUUID() {
        return BluetoothUtils.uuidFrom16BitUuid(0xffe1);
    }

    @Override
    public UUID getReadCharacteristicUUID() {
        return BluetoothUtils.uuidFrom16BitUuid(0xffe1);
    }

    public Observable<PumpEvent> getPumpEventObservable() {
        return mPumpEventObservable;
    }

    public void inflate() {
        sendCommand(PumpCommand.inflate(PumpCommand.Chamber.Right));
    }

    public void deflate() {
        sendCommand(PumpCommand.deflate(PumpCommand.Chamber.Right));
    }

    public void stop() {
        sendCommand(PumpCommand.stop(PumpCommand.Chamber.Right));
    }
}
