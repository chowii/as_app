package au.com.ahbeard.sleepsense.bluetooth;

import android.bluetooth.BluetoothGatt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Created by neal on 9/03/2016.
 */
public class BluetoothOperation {

    private final Map<Subscriber<? super BluetoothOperation.Status>, Boolean> subscribers = new ConcurrentHashMap<>();

    private final Observable<BluetoothOperation.Status> mObservable = Observable.create(
            new Observable.OnSubscribe<BluetoothOperation.Status>() {
                @Override
                public void call(final Subscriber<? super BluetoothOperation.Status> subscriber) {
                    subscribers.put(subscriber, Boolean.TRUE);
                    subscriber.add(Subscriptions.create(new Action0() {
                        @Override
                        public void call() {
                            subscribers.remove(subscriber);
                        }
                    }));
                }
            });

    public Observable<BluetoothOperation.Status> observe() {
        return mObservable;
    }

    public enum Status {
        Queued,
        Running,
        Complete
    }

    private Status mStatus = Status.Queued;

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {

        this.mStatus = status;

        for (Subscriber<? super Status> subscriber : subscribers.keySet()) {
            subscriber.onNext(this.mStatus);
        }

    }

    public void perform(BluetoothGatt bluetoothGatt) {

    }
}
