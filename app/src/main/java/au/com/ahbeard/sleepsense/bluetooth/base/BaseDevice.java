package au.com.ahbeard.sleepsense.bluetooth.base;

import java.util.UUID;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothOperation;
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
public class BaseDevice extends Device {

    public static final int DISCONNECT_AFTER = 15000;

    private static UUID[] REQUIRED_SERVICES = {
            BluetoothUtils.uuidFrom16BitUuid(0xfff0), //fff0 and ffb0 are an hack to properly connect to the base
            BluetoothUtils.uuidFrom16BitUuid(0xffb0), //they are not actually setup has services, but without them we can't connect
            BluetoothUtils.uuidFrom16BitUuid(0xffe5),
            BluetoothUtils.uuidFrom16BitUuid(0xffe0),
    };

    private Subscription mBaseStatusSubscription;
    private PublishSubject<BaseStatusEvent> mBaseEventPublishSubject = PublishSubject.create();

    private boolean mMassageAlterState;
    private int mMassageDesiredState;
    private int mMassagePreviousState;

    public UUID[] getRequiredServiceUUIDs() {
        return REQUIRED_SERVICES;
    }

    @Override
    public UUID getServiceUUID() {
        return BluetoothUtils.uuidFrom16BitUuid(0xffe0);
    }

    @Override
    public UUID getNotifyCharacteristicUUID() {
        return BluetoothUtils.uuidFrom16BitUuid(0xffe4);
    }

    @Override
    protected boolean getSetupNotifications() {
        return true;
    }

    private long mLastActiveTime;

    public BaseDevice() {
        super();

        mBaseEventPublishSubject.subscribe(new Action1<BaseStatusEvent>() {
            @Override
            public void call(BaseStatusEvent baseStatusEvent) {
                if (baseStatusEvent.isHeadMotorRunning() || baseStatusEvent.isFootMotorRunning()) {
                    mLastActiveTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - mLastActiveTime > DISCONNECT_AFTER) {
                    disconnect();
                }
            }
        });
    }

    public Observable<BaseStatusEvent> getBaseEventObservable() {
        return mBaseEventPublishSubject;
    }

    @Override
    protected void onConnect() {

        super.onConnect();

        mLastActiveTime = System.currentTimeMillis();

        mBaseStatusSubscription = getNotifyEventObservable()
                .subscribeOn(Schedulers.computation())
                .onBackpressureBuffer()
                .map(new Func1<ValueChangeEvent, byte[]>() {
                    @Override
                    public byte[] call(ValueChangeEvent valueChangeEvent) {
                        return valueChangeEvent.getValue();
                    }
                })
                .subscribe(new Action1<byte[]>() {
                    @Override
                    public void call(byte[] value) {
                        BaseStatusEvent baseStatusEvent = BaseStatusEvent.safeInstance(value);
                        if (baseStatusEvent != null) {
                            mBaseEventPublishSubject.onNext(baseStatusEvent);
                        }
                    }
                });
    }

    @Override
    protected void onDisconnect() {
        if (mBaseStatusSubscription != null) {
            mBaseStatusSubscription.unsubscribe();
            mBaseStatusSubscription = null;
        }

        super.onDisconnect();
    }

    @Override
    public void sendCommand(BluetoothOperation command) {
        super.sendCommand(command);
        mLastActiveTime = System.currentTimeMillis();
    }

    public void massageStop() {
        mMassageAlterState=true;
        mMassageDesiredState = 0;
    }
}
