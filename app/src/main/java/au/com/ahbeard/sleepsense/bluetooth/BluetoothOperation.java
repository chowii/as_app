package au.com.ahbeard.sleepsense.bluetooth;

/**
 * Created by neal on 9/03/2016.
 */
public class BluetoothOperation {

    public enum Status {
        Queued,
        Running,
        Complete
    }

    protected Status status = Status.Queued;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
