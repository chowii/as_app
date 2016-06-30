package au.com.ahbeard.sleepsense.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import au.com.ahbeard.sleepsense.services.LogService;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by neal on 29/12/2015.
 */
public class Device extends BluetoothGattCallback {

    public static final int CONNECTION_STATE_UNLINKED = 0;
    public static final int CONNECTION_STATE_DISCONNECTED = 1;
    public static final int CONNECTION_STATE_CONNECTING = 2;
    public static final int CONNECTION_STATE_CONNECTED_DISCOVERING_SERVICES = 3;
    public static final int CONNECTION_STATE_CONNECTED = 4;
    public static final int CONNECTION_STATE_DISCONNECTING = 5;

    protected static final UUID DEVICE_INFORMATION_SERVICE_UUID =
            UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    protected static final UUID DEVICE_INFORMATION_MANUFACTURER_NAME_UUID =
            UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb");
    protected static final UUID DEVICE_INFORMATION_MODEL_NUMBER_UUID =
            UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb");
    protected static final UUID DEVICE_INFORMATION_SERIAL_NUMBER_UUID =
            UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb");
    protected static final UUID DEVICE_INFORMATION_HARDWARE_REVISION_UUID =
            UUID.fromString("00002A27-0000-1000-8000-00805f9b34fb");
    protected static final UUID DEVICE_INFORMATION_FIRMWARE_REVISION_UUID =
            UUID.fromString("00002A26-0000-1000-8000-00805f9b34fb");
    protected static final UUID DEVICE_INFORMATION_SOFTWARE_REVISION_UUID =
            UUID.fromString("00002A28-0000-1000-8000-00805f9b34fb");

    public static final String[] CONNECTION_STATE_LABELS =
            {"Unlinked", "Disconnected", "Connecting", "Connecting Discovering Services", "Connected", "Disconnecting"};

    private static final String TAG = "Device";

    private BluetoothOperationQueue mBluetoothOperationQueue = new BluetoothOperationQueue();

    private int mLastConnectionStatus = 0;

    // Get the list of services
    public UUID[] getRequiredServiceUUIDs() {
        return new UUID[0];
    }

    // Service UUIDs to read and write to the device. May refactor.
    public UUID getServiceUUID() {
        return null;
    }

    // The
    public UUID getWriteCharacteristicUUID() {
        return null;
    }

    public UUID getReadCharacteristicUUID() {
        return null;
    }

    public UUID getNotifyCharacteristicUUID() {
        return null;
    }

    protected boolean getSetupNotifications() {
        return false;
    }

    public void readDeviceInformation() {

    }

    public Context getContext() {
        return mContext;
    }

    private PublishSubject<CharacteristicWriteOperation> mBluetoothCommandSubject = PublishSubject.create();
    private PublishSubject<DeviceEvent> mDeviceEventSubject = PublishSubject.create();
    private PublishSubject<ValueChangeEvent> mNotifyEventSubject = PublishSubject.create();

    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;

    private CompositeSubscription mBluetoothOperationQueueSubscription = new CompositeSubscription();

    private PublishSubject<Device> mChangeSubject = PublishSubject.create();

    private Context mContext;

    private int XYZ;

    private int mConnectionState;

    private PublishSubject<String> mLogPublishSubject = PublishSubject.create();

    public Observable<String> getLogObservable() {
        return mLogPublishSubject;
    }

    public Device(Context context, BluetoothDevice bluetoothDevice) {
        mContext = context.getApplicationContext();
        mBluetoothDevice = bluetoothDevice;
        mConnectionState = CONNECTION_STATE_DISCONNECTED;
    }

    public Device(Context context, String address) {
        mContext = context.getApplicationContext();
        mBluetoothDevice = BluetoothService.instance().createDeviceFromAddress(address);
        mConnectionState = CONNECTION_STATE_DISCONNECTED;
    }

    public Device() {
        mConnectionState = CONNECTION_STATE_UNLINKED;
    }

    public void link(Context context, BluetoothDevice bluetoothDevice) {
        mContext = context.getApplicationContext();
        mBluetoothDevice = bluetoothDevice;
        mConnectionState = CONNECTION_STATE_DISCONNECTED;
    }

    public boolean isLinked() {
        return mBluetoothDevice != null;
    }

    public void sendCommand(BluetoothOperation command) {
        LogService.d("SleepSenseDeviceService",String.format("sending command... %s", command.toString()));
        if (mBluetoothOperationQueue.addOperation(command) && mConnectionState == CONNECTION_STATE_DISCONNECTED) {
            connect();
        }
    }

    private Timer mConnectionTimer;
    private long mConnectionTimeout = 10000;
    private long mConnectIssuedAt;

    public void connect() {

        if ( BluetoothService.instance().isBluetoothEnabled() ) {
            LogService.d("Device","connecting...");
            if (mBluetoothDevice != null && mBluetoothGatt == null) {

                mConnectIssuedAt = System.currentTimeMillis();

                // Set up a timer to
                mConnectionTimer = new Timer();
                mConnectionTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if ( mConnectionState == CONNECTION_STATE_CONNECTING ) {
                            mChangeSubject.onNext(Device.this);
                        }
                    }
                }, 100, 100);

                mConnectionState = CONNECTION_STATE_CONNECTING;
                mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false, this);
                mChangeSubject.onNext(this);
            }
        }

    }

    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
    }

    public long getElapsedConnectingTime() {
        return System.currentTimeMillis() - mConnectIssuedAt;
    }

    public int getConnectionState() {
        return mConnectionState;
    }

    public String getConnectionStateLabel() {
        return CONNECTION_STATE_LABELS[mConnectionState];
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

        LogService.i("Device",String.format("onConnectionStateChange: mStatus=%d newState=%d", status, newState));

        mLastConnectionStatus = status;

        switch (newState) {

            case BluetoothGatt.STATE_CONNECTED:

                if ( mConnectionTimer != null ) {
                    mConnectionTimer.cancel();
                    mConnectionTimer = null;
                }

                mConnectionState = CONNECTION_STATE_CONNECTED_DISCOVERING_SERVICES;

                mBluetoothGatt = gatt;

                mBluetoothGatt.discoverServices();

                break;

            case BluetoothGatt.STATE_CONNECTING:
                mConnectionState = CONNECTION_STATE_CONNECTING;

                break;
            case BluetoothGatt.STATE_DISCONNECTED:

                onDisconnect();

                mConnectionState = CONNECTION_STATE_DISCONNECTED;

                mBluetoothOperationQueue.purge();
                mBluetoothOperationQueueSubscription.clear();

                mBluetoothGatt.close();
                mBluetoothGatt = null;

                mDeviceEventSubject.onNext(new DeviceDisconnectedEvent());

                break;
            case BluetoothGatt.STATE_DISCONNECTING:
                mConnectionState = CONNECTION_STATE_DISCONNECTING;

                break;
        }

        mChangeSubject.onNext(this);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {

        LogService.i("Device","onServicesDiscovered...");

        if (status == BluetoothGatt.GATT_SUCCESS) {

            BluetoothGattService service = gatt.getService(getServiceUUID());

            if (service != null) {

                mBluetoothOperationQueueSubscription.add(
                        mBluetoothOperationQueue.observe().observeOn(Schedulers.io()).subscribe(
                                new Observer<BluetoothOperation>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(BluetoothOperation bluetoothOperation) {

                                        if (bluetoothOperation.perform(mBluetoothGatt) ) {
                                            mBluetoothOperationQueue.completeOperation(bluetoothOperation);
                                        }

                                    }
                                }

                        ));

                if (getSetupNotifications()) {
                    EnableIndicationOperation enableNotificationOperation =
                            new EnableIndicationOperation(getServiceUUID(), getNotifyCharacteristicUUID());
                    mBluetoothOperationQueue.addOperation(enableNotificationOperation);
                }

                mConnectionState = CONNECTION_STATE_CONNECTED;

                onConnect();

                mBluetoothOperationQueue.start();

                mChangeSubject.onNext(this);
                mDeviceEventSubject.onNext(new DeviceConnectedEvent());

            } else {
                disconnect();
            }
        } else {
            disconnect();
        }

    }

    protected void onConnect() {

    }

    protected void onSlowConnect() {

    }

    protected void onDisconnect() {

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
//        LogService.d("Device",String.format("onCharacteristicChanged: %s",
//                CharacteristicWriteOperation.getReadableStringFromByteArray(value)));
        mNotifyEventSubject.onNext(new ValueChangeEvent(characteristic.getUuid(), value));
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        byte[] value = characteristic.getValue();
        LogService.d("SleepSenseDeviceService",String.format("onCharacteristicRead: %s",
                CharacteristicWriteOperation.getReadableStringFromByteArray(value)));

        mBluetoothOperationQueue.completeReadOperation(characteristic.getUuid(), value);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        LogService.d("Device",String.format("onCharacteristicWrite: %s",
                CharacteristicWriteOperation.getReadableStringFromByteArray(characteristic.getValue())));
        mBluetoothOperationQueue.completeWriteOperation();
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        LogService.d("Device",String.format("onDescriptorRead: %s",
                CharacteristicWriteOperation.getReadableStringFromByteArray(descriptor.getValue())));
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        mBluetoothOperationQueue.completeDescriptorWriteOperation();
        LogService.d("Device",String.format("onDescriptorWrite: %s",
                CharacteristicWriteOperation.getReadableStringFromByteArray(descriptor.getValue())));
    }

    public Observable<DeviceEvent> getDeviceEventObservable() {
        return mDeviceEventSubject;
    }

    public Observable<Device> getChangeObservable() {
        return mChangeSubject;
    }

    public Observable<ValueChangeEvent> getNotifyEventObservable() {
        return mNotifyEventSubject;
    }

    /**
     * True if we're connected and only if we're connected (not disconnecting etc.).
     *
     * @return
     */
    public boolean isConnected() {
        return mConnectionState == CONNECTION_STATE_CONNECTED;
    }

    /**
     * True if we're disconnected and only if we're disconnected (not connecting etc.).
     *
     * @return
     */
    public boolean isDisconnected() {
        return mConnectionState == CONNECTION_STATE_DISCONNECTED;
    }

    public String getAddress() {
        return mBluetoothDevice != null ? mBluetoothDevice.getAddress() : null;
    }

    public int getLastConnectionStatus() {
        return mLastConnectionStatus;
    }

    public String getName() {
        if ( mBluetoothDevice!=null) {
            return mBluetoothDevice.getName();
        } else {
            return "NO BLUETOOTH DEVICE";
        }
    }


    /**
     *
     */
    public static class DeviceEvent {

    }

    public static class DeviceConnectedEvent extends DeviceEvent {

    }

    public static class DeviceDisconnectedEvent extends DeviceEvent {

    }



}
