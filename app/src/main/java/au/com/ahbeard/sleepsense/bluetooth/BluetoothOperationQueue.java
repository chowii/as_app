package au.com.ahbeard.sleepsense.bluetooth;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import au.com.ahbeard.sleepsense.bluetooth.tracker.EnableNotificationOperation;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * The purpose of this class it to abstract away the concept of queue that contains operations. It basically provides
 * the head of the queue to the observer, and will only provide another item when a callback is made indicating the
 * operation is complete or has failed.
 * <p/>
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

    public ArrayList<BluetoothOperation> mBluetoothOperations = new ArrayList<>(1024);

    private boolean mIsRunning = false;

    public void start() {
        mIsRunning = true;

        processQueue();
    }

    public boolean addOperation(BluetoothOperation operation) {

        synchronized (mBluetoothOperations) {

            if (operation instanceof EnableNotificationOperation ) {
                mBluetoothOperations.add(0,operation);
                processQueue();
                return true;
            } else {
                for ( int i=0; i < mBluetoothOperations.size(); i++ ) {
                    if ( operation.replacesOperation(mBluetoothOperations.get(i))) {
                        mBluetoothOperations.set(i,operation);
                        processQueue();
                        return true;
                    }
                }
            }

            mBluetoothOperations.add(operation);
            processQueue();
            return true;

        }


    }

    public void completeOperation(BluetoothOperation operation) {

        operation.setStatus(BluetoothOperation.Status.Complete);

        processQueue();

    }

    public void completeWriteOperation() {

        if (mBluetoothOperations.get(0) instanceof CharacteristicWriteOperation) {
            completeOperation(mBluetoothOperations.get(0));
        }

    }

    public void completeDescriptorWriteOperation() {

        if (mBluetoothOperations.get(0) instanceof EnableNotificationOperation) {
            completeOperation(mBluetoothOperations.get(0));
        }

    }

    public void completeReadOperation(UUID characteristic, byte[] value) {

        if (mBluetoothOperations.get(0) instanceof CharacteristicReadOperation) {
            completeOperation(mBluetoothOperations.get(0));
        }

    }

    public void abortOperation(BluetoothOperation operation, String error) {
        operation.setStatus(BluetoothOperation.Status.Complete);
        processQueue();
    }

    /**
     *
     */
    private void processQueue() {

        if (!mIsRunning) {
            return;
        }

        while (mBluetoothOperations.size() > 0) {
            if (mBluetoothOperations.get(0).getStatus() == BluetoothOperation.Status.Queued) {
                mBluetoothOperations.get(0).setStatus(BluetoothOperation.Status.Running);
                BluetoothOperation bluetoothOperation = mBluetoothOperations.get(0);
                for (Subscriber<? super BluetoothOperation> subscriber : subscribers.keySet()) {
                    subscriber.onNext(bluetoothOperation);
                }
                break;
            } else if (mBluetoothOperations.get(0).getStatus() == BluetoothOperation.Status.Running) {
                break;
            } else if (mBluetoothOperations.get(0).getStatus() == BluetoothOperation.Status.Complete) {
                mBluetoothOperations.remove(0);
            }
        }
    }


    public void purge() {
        mIsRunning = false;
        mBluetoothOperations.clear();

    }
}
