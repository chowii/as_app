package au.com.ahbeard.sleepsense.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.support.annotation.Nullable;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by luisramos on 23/02/2017.
 */

public class BedHardwareBluetoothGathCallback extends BluetoothGattCallback {

    private PublishProcessor<BaseEvent> mProcessor = PublishProcessor.create();

    public static class BaseEvent {}
    public static class ConnectionStateChangeEvent extends BaseEvent {
        int status;
        int newState;
        ConnectionStateChangeEvent(int status, int newState) {
            this.status = status;
            this.newState = newState;
        }
    }
    public static class CharacteristicWriteEvent extends BaseEvent {
        BluetoothGattCharacteristic characteristic;
        int status;
        CharacteristicWriteEvent(@Nullable BluetoothGattCharacteristic characteristic, int status) {
            this.characteristic = characteristic;
            this.status = status;
        }
    }

    public BedHardwareBluetoothGathCallback() {
        super();
    }

    public Flowable<ConnectionStateChangeEvent> waitForConnection(long connectionTimeoutSeconds) {
        return getBluetoothGattEventFlowable()
                .filter(new Predicate<BaseEvent>() {
                    @Override
                    public boolean test(@NonNull BaseEvent baseEvent) throws Exception {
                        return baseEvent instanceof ConnectionStateChangeEvent
                                && ((ConnectionStateChangeEvent) baseEvent).newState == BluetoothGatt.STATE_CONNECTED;
                    }
                })
                .cast(ConnectionStateChangeEvent.class)
                .take(1)
                .timeout(connectionTimeoutSeconds, TimeUnit.SECONDS)
                .onErrorResumeNext(new Function<Throwable, Publisher<? extends ConnectionStateChangeEvent>>() {
                    @Override
                    public Publisher<? extends ConnectionStateChangeEvent> apply(@NonNull Throwable throwable) throws Exception {
                        if (throwable instanceof TimeoutException) {
                            return Flowable.error(new ConnectionTimeoutError());
                        } else {
                            return Flowable.error(throwable);
                        }
                    }
                });
    }

    public Flowable<CharacteristicWriteEvent> waitForCharacteristicWrite(final BluetoothGattCharacteristic characteristic) {
        return getBluetoothGattEventFlowable()
                .filter(new Predicate<BaseEvent>() {
                    @Override
                    public boolean test(@NonNull BaseEvent baseEvent) throws Exception {
                        return baseEvent instanceof CharacteristicWriteEvent;
                    }
                })
                .cast(CharacteristicWriteEvent.class)
                .flatMap(new Function<CharacteristicWriteEvent, Publisher<CharacteristicWriteEvent>>() {
                    @Override
                    public Publisher<CharacteristicWriteEvent> apply(@NonNull CharacteristicWriteEvent characteristicWriteEvent) throws Exception {
                        if (characteristicWriteEvent == null
                                || characteristicWriteEvent.characteristic == null
                                || characteristicWriteEvent.characteristic.getValue() != characteristic.getValue()) {
                            return Flowable.error(new CharacteristicWriteError());
                        } else {
                            return Flowable.just(characteristicWriteEvent);
                        }
                    }
                })
                .take(1);
    }

    private Flowable<BaseEvent> getBluetoothGattEventFlowable() {
        return mProcessor.hide();
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        mProcessor.onNext(new ConnectionStateChangeEvent(status, newState));
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        super.onReliableWriteCompleted(gatt, status);
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
    }

    static class BaseError extends Throwable {}
    static class ConnectionTimeoutError extends Throwable {}
    static class CharacteristicWriteError extends Throwable {}
}