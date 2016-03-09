package au.com.ahbeard.sleepsense.bluetooth;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * The purpose of this class it to abstract away the concept of queue that contains operations. It basically provides
 * the head of the queue to the observer, and will only provide another item when a callback is made indicating the
 * operation is complete or has failed.
 *
 * Created by neal on 9/03/2016.
 */
public class BluetoothOperationQueue {

    private final Map<Subscriber<? super BluetoothOperation>, Boolean> subscribers = new ConcurrentHashMap<>();

    private final Observable<BluetoothOperation> mObservable = Observable.create(
            new Observable.OnSubscribe<BluetoothOperation>() {
                @Override
                public void call(final Subscriber<? super BluetoothOperation> subscriber) {
                    subscribers.put(subscriber, Boolean.TRUE);
                    subscriber.add(Subscriptions.create(new Action0() {
                        @Override
                        public void call() {
                            subscribers.remove(subscriber);
                        }
                    }));
                }
            });

    public Observable<BluetoothOperation> observe() {
        return mObservable;
    }

    public Queue<BluetoothOperation> mBluetoothOperations = new ArrayBlockingQueue<BluetoothOperation>(1024);

    public void addOperation(BluetoothOperation operation) {

        mBluetoothOperations.add(operation);

        runQueue();

    }

    public void completeOperation(BluetoothOperation operation) {

    }

    /**
     *
     */
    private void runQueue() {
        if (mBluetoothOperations.peek().status == BluetoothOperation.Status.Queued) {
            mBluetoothOperations.peek().status = BluetoothOperation.Status.Running;
            for (Subscriber<? super BluetoothOperation> subscriber : subscribers.keySet() ) {
                subscriber.onNext(mBluetoothOperations.element());
            }
        }
    }


}
