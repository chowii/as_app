package au.com.ahbeard.sleepsense.bluetooth.pump;

import android.bluetooth.BluetoothGatt;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothUtils;
import au.com.ahbeard.sleepsense.bluetooth.CharacteristicWriteOperation;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.ValueChangeEvent;
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

    private long mLastActiveTime;

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

        public int getCurrentPressure() {
            return currentPressure;
        }
    }

    private PublishSubject<PumpEvent> mPumpEventSubject = PublishSubject.create();

    private ChamberState mLeftChamberState = new ChamberState(Side.Left);
    private ChamberState mRightChamberState = new ChamberState(Side.Right);

    private Set<PumpEvent.PumpStatus> mPumpStatuses = new HashSet<>();

    private static final UUID[] ADVERTISED_UUIDs = {BluetoothUtils.uuidFrom16BitUuid(0xffe0)};

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
    }

    public PumpDevice() {

        super();

        // Subscribe to our own pump event subject. We need to know this stuff too!
        mPumpEventSubject.subscribe(new Action1<PumpEvent>() {
            @Override
            public void call(PumpEvent pumpEvent) {

                Log.d("PumpEvent",pumpEvent.toString());

                mLeftChamberState.currentPressure = pumpEvent.getLeftPressure();
                mRightChamberState.currentPressure = pumpEvent.getRightPressure();
                mPumpStatuses = pumpEvent.getStatuses();

                // Disconnect if we're not doing anything.
                if (pumpEvent.isAdjusting()) {
                    mLastActiveTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - mLastActiveTime > 3000 ) {
                    disconnect();
                }

            }
        });
    }

    @Override
    public void sendCommand(CharacteristicWriteOperation command) {
        mLastActiveTime = System.currentTimeMillis();
        super.sendCommand(command);
    }

    @Override
    protected boolean getSetupNotifications() {
        return true;
    }

    @Override
    public UUID[] getRequiredServiceUUIDs() {
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
                Schedulers.io()).map(new Func1<ValueChangeEvent, byte[]>() {
            @Override
            public byte[] call(ValueChangeEvent valueChangeEvent) {
                return valueChangeEvent.getValue();
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

    public void inflateToTarget(Side side, int pressure) {

        if (mPumpStatuses.contains(PumpEvent.PumpStatus.Inflating) || mPumpStatuses.contains(PumpEvent.PumpStatus.Deflating)) {
            sendCommand(PumpCommand.stop(convertSideToChamber(side)));
        }

        sendCommand(PumpCommand.setPressure(convertSideToChamber(side), pressure));
    }

    public void inflate(Side side) {
        sendCommand(PumpCommand.inflate(convertSideToChamber(side)));
    }

    public void deflate(Side side) {
        sendCommand(PumpCommand.deflate(convertSideToChamber(side)));
    }

    public void stop(Side side) {
        sendCommand(PumpCommand.stop(convertSideToChamber(side)));
    }

    @NonNull
    private PumpCommand.Chamber convertSideToChamber(Side side) {
        return side == Side.Left ? PumpCommand.Chamber.Left : PumpCommand.Chamber.Right;
    }

    public void fetchStatus() {
        connect();
    }


}
