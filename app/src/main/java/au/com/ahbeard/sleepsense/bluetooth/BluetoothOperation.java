package au.com.ahbeard.sleepsense.bluetooth;

import android.bluetooth.BluetoothGatt;

/**
 * Created by neal on 9/03/2016.
 */
public class BluetoothOperation {

    enum Status {
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
    }

    public boolean perform(BluetoothGatt bluetoothGatt) {

        return false;
    }

    public boolean replacesOperation(BluetoothOperation bluetoothOperation) {
        return false;
    }


}
