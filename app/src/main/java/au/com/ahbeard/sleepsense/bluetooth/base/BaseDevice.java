package au.com.ahbeard.sleepsense.bluetooth.base;

import android.util.Log;

import java.util.UUID;

import au.com.ahbeard.sleepsense.bluetooth.BluetoothUtils;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.ValueChangeEvent;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by neal on 4/03/2016.
 */
public class BaseDevice extends Device {

    private static UUID[] REQUIRED_SERVICES = {
            BluetoothUtils.uuidFrom16BitUuid(0xffb0),
            BluetoothUtils.uuidFrom16BitUuid(0xffe0),
            BluetoothUtils.uuidFrom16BitUuid(0xffe5),
            BluetoothUtils.uuidFrom16BitUuid(0xfff0),
    };

    private Subscription mBaseStatusSubscription;
    private PublishSubject<BaseStatusEvent> mBaseStatusEventPublishSubject = PublishSubject.create();

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

    @Override
    protected void onConnect() {
        super.onConnect();

        mBaseStatusSubscription = getNotifyEventObservable()
                .subscribeOn(Schedulers.io())
                .map(new Func1<ValueChangeEvent, byte[]>() {
                    @Override
                    public byte[] call(ValueChangeEvent valueChangeEvent) {
                        return valueChangeEvent.getValue();
                    }
                }).subscribe(new Action1<byte[]>() {
                    @Override
                    public void call(byte[] value) {
                        BaseStatusEvent baseStatusEvent = BaseStatusEvent.safeInstance(value);
                        Log.d("BaseDevice","baseStatusEvent: "+baseStatusEvent);
                        if (baseStatusEvent!=null){
                            mBaseStatusEventPublishSubject.onNext(baseStatusEvent);
                        }
                    }
                });

    }

    @Override
    protected void onDisconnect() {
        if ( mBaseStatusSubscription != null ) {
            mBaseStatusSubscription.unsubscribe();
            mBaseStatusSubscription = null;
        }

        super.onDisconnect();
    }
}
