package au.com.ahbeard.sleepsense.bluetooth.pump;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothUtils;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.NotifyEvent;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by neal on 4/03/2016.
 */
public class PumpDevice extends Device {

    private Subscription mPumpStatusSubscription;

    public enum Side {
        Left,
        Right
    }

    public class ChamberState {

        Side side;
        int currentPressure;
        int targetPressure;
        boolean active;

        public ChamberState(Side side) {
            this.side = side;
        }
    }

    private PublishSubject<PumpEvent> mPumpEventSubject = PublishSubject.create();

    private ChamberState mLeftChamberState = new ChamberState(Side.Left);
    private ChamberState mRightChamberState = new ChamberState(Side.Right);

    private Set<PumpEvent.PumpStatus> mPumpStatuses = new HashSet<>();

    private static final UUID[] ADVERTISED_UUIDs = {BluetoothUtils.uuidFrom16BitUuid(0xffe0)};

    public PumpDevice() {

        super();

        // Subscribe to our own pump event subject. We need to know this stuff too!
        mPumpEventSubject.subscribe(new Action1<PumpEvent>() {
            @Override
            public void call(PumpEvent pumpEvent) {

                mLeftChamberState.currentPressure = pumpEvent.getLeftPressure();
                mRightChamberState.currentPressure = pumpEvent.getRightPressure();
                mPumpStatuses = pumpEvent.getStatuses();
            }
        });
    }

    @Override
    public UUID[] getAdvertisedUUIDs() {
        return ADVERTISED_UUIDs;
    }

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

    @Override
    public UUID getNotifyCharacteristicUUID() {
        return BluetoothUtils.uuidFrom16BitUuid(0xffe1);
    }

    public Observable<PumpEvent> getPumpEventObservable() {
        return mPumpEventSubject;
    }

    public void onConnect() {

        mPumpStatusSubscription = getNotifyEventObservable().subscribeOn(
                Schedulers.io()).map(new Func1<NotifyEvent, byte[]>() {
            @Override
            public byte[] call(NotifyEvent notifyEvent) {
                return notifyEvent.getValue();
            }
        }).subscribe(new PumpStatusObserver(mPumpEventSubject));

    }

    public void onDisconnect() {
        if (mPumpStatusSubscription != null) {
            mPumpStatusSubscription.unsubscribe();
            mPumpStatusSubscription = null;
        }
        mPumpStatuses = new HashSet<>();
    }

    public ChamberState getChamberState(Side side) {
        if (side == Side.Left) {
            return mLeftChamberState;
        } else if (side == Side.Right) {
            return mRightChamberState;
        } else {
            throw new IllegalArgumentException("Pump side must be either left or right!");
        }
    }

    public void setTarget(Side side, int pressure) {

        if (pressure < 0 || pressure > 60) {
            throw new IllegalArgumentException("Pump pressure must be in the range 0-60!");
        }

        if (side == Side.Left) {
            mLeftChamberState.targetPressure = pressure;
        } else if (side == Side.Right) {
            mRightChamberState.targetPressure = pressure;
        } else {
            throw new IllegalArgumentException("Pump side must be either left or right!");
        }

        controlPump();

    }

    public void cancelTarget(Side side) {
        if (side == Side.Left) {
            mLeftChamberState.active = true;
        } else if (side == Side.Right) {
            mRightChamberState.active = true;
        } else {
            throw new IllegalArgumentException("Pump side must be either left or right!");
        }

        controlPump();
    }

    public void controlPump() {

        // Control the left chamber.
        if (mPumpStatuses.contains(PumpEvent.PumpStatus.LeftChamberActive)) {
            handleActiveChamber(mLeftChamberState);
        } else {
            handleInactiveChamber(mLeftChamberState);
        }

        if (mPumpStatuses.contains(PumpEvent.PumpStatus.RightChamberActive)) {
            handleActiveChamber(mRightChamberState);
        } else {
            handleInactiveChamber(mRightChamberState);
        }

    }

    private void handleInactiveChamber(ChamberState chamberState) {

        if (chamberState.active && isConnected()) {
            if (chamberState.currentPressure > chamberState.targetPressure) {
                deflate(chamberState.side);
            } else if (chamberState.currentPressure < chamberState.targetPressure) {
                inflate(chamberState.side);
            } else {
                chamberState.active = false;
            }

        }

    }

    private void handleActiveChamber(ChamberState chamberState) {

        if (chamberState.active && isConnected()) {

            // Check for over inflation (the 3 is added for "debounce").
            if (mPumpStatuses.contains(PumpEvent.PumpStatus.Inflating) &&
                    chamberState.currentPressure >= chamberState.targetPressure + 3) {
                deflate(chamberState.side);
            } else if (mPumpStatuses.contains(PumpEvent.PumpStatus.Inflating) &&
                    chamberState.currentPressure >= chamberState.targetPressure) {
                chamberState.active = false;
                stop(chamberState.side);
            // Check for under inflation (the 3 is added for "debounce").
            } else if (mPumpStatuses.contains(PumpEvent.PumpStatus.Deflating) &&
                    chamberState.currentPressure <= chamberState.targetPressure - 3) {
                inflate(chamberState.side);
            } else if (mPumpStatuses.contains(PumpEvent.PumpStatus.Deflating) &&
                    chamberState.currentPressure <= chamberState.targetPressure) {
                chamberState.active = false;
                stop(chamberState.side);
            }

        }
    }


    public void inflate(Side side) {
        sendCommand(PumpCommand.inflate(side == Side.Left ? PumpCommand.Chamber.Left : PumpCommand.Chamber.Right));
    }

    public void deflate(Side side) {
        sendCommand(PumpCommand.deflate(side == Side.Left ? PumpCommand.Chamber.Left : PumpCommand.Chamber.Right));
    }

    public void stop(Side side) {
        sendCommand(PumpCommand.stop(side == Side.Left ? PumpCommand.Chamber.Left : PumpCommand.Chamber.Right));
    }
}
