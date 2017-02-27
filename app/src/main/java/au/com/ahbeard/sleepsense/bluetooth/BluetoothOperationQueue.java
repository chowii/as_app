package au.com.ahbeard.sleepsense.bluetooth;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * The purpose of this class it to abstract away the concept of queue that contains operations. It basically provides
 * the head of the queue to the observer, and will only provide another item when a callback is made indicating the
 * operation is complete or has failed.
 * <p/>
 * Created by neal on 9/03/2016.
 */
public class BluetoothOperationQueue {

    private final PublishSubject<BluetoothOperation> mPublishSubject = PublishSubject.create();
    private final ArrayList<BluetoothOperation> mBluetoothOperations = new ArrayList<>(2048);
    private boolean mIsRunning = false;

    public Observable<BluetoothOperation> observe() {
        return mPublishSubject.hide();
    }

    public void start() {
        mIsRunning = true;

        processQueue();
    }

    public boolean addOperation(BluetoothOperation operation) {

        synchronized (mBluetoothOperations) {

            if (operation instanceof EnableIndicationOperation ) {
                mBluetoothOperations.add(0,operation);
                processQueue();
                return true;
            } else {
                for ( int i = 0; i < mBluetoothOperations.size(); i++ ) {
                    if ( operation.replacesOperation(mBluetoothOperations.get(i))) {
                        //try to avoid out of bounds exceptions
                        int pos = Math.min(i, mBluetoothOperations.size() - 1);
                        mBluetoothOperations.set(pos, operation);
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

        if (mBluetoothOperations.get(0) instanceof EnableNotificationOperation||mBluetoothOperations.get(0) instanceof EnableIndicationOperation) {
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
                mPublishSubject.onNext(bluetoothOperation);
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
