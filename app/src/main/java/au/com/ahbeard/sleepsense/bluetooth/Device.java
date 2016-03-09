package au.com.ahbeard.sleepsense.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import java.util.UUID;

import rx.Observable;
import rx.Observer;
import rx.functions.Action0;
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

    private static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    protected static final UUID DEVICE_INFORMATION_SERVICE_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");

    protected static final UUID DEVICE_INFORMATION_MANUFACTURER_NAME_UUID = UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb");
    protected static final UUID DEVICE_INFORMATION_MODEL_NUMBER_UUID = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb");
    protected static final UUID DEVICE_INFORMATION_SERIAL_NUMBER_UUID = UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb");
    protected static final UUID DEVICE_INFORMATION_HARDWARE_REVISION_UUID = UUID.fromString("00002A27-0000-1000-8000-00805f9b34fb");
    protected static final UUID DEVICE_INFORMATION_FIRMWARE_REVISION_UUID = UUID.fromString("00002A26-0000-1000-8000-00805f9b34fb");
    protected static final UUID DEVICE_INFORMATION_SOFTWARE_REVISION_UUID = UUID.fromString("00002A28-0000-1000-8000-00805f9b34fb");

    public static final String[] CONNECTION_STATE_LABELS =
            {"Unlinked", "Disconnected", "Connecting", "Connecting Discovering Services", "Connected", "Disconnecting"};

    private static final String TAG = "Device";

    // Used to match the device initially.
    public UUID[] getAdvertisedUUIDs() {
        return new UUID[0];
    }

    // Service UUIDs to read and write to the device. May refactor.
    public UUID getServiceUUID() {
        return null;
    }

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

    private PublishSubject<BluetoothWriteOperation> mBluetoothCommandSubject = PublishSubject.create();
    private PublishSubject<DeviceEvent> mDeviceEventSubject = PublishSubject.create();
    private PublishSubject<ValueChangeEvent> mNotifyEventSubject = PublishSubject.create();

    // Per connection callbacks to indicate that something has been written
    private PublishSubject<Integer> mCharacteristicReadSubject;
    private PublishSubject<Integer> mCharacteristicWriteSubject;

    private PublishSubject<Integer> mDescriptorReadSubject;
    private PublishSubject<Integer> mDescriptorWriteSubject;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;

    private BluetoothGattCharacteristic mReadCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private CompositeSubscription mBluetoothSubscriptions = new CompositeSubscription();

    private PublishSubject<Object> mChangeSubject = PublishSubject.create();

    private Context mContext;

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

    public void sendCommand(BluetoothWriteOperation command) {
        log(Log.DEBUG, String.format("sending command... %s", command.toString()));
        mBluetoothCommandSubject.onNext(command);
    }

    public void connect() {
        log(Log.DEBUG, "connecting...");
        if (mBluetoothDevice != null && mBluetoothGatt == null) {
            mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false, this);
        }
    }

    public void disconnect() {
        mBluetoothGatt.disconnect();
    }

    public int getConnectionState() {
        return mConnectionState;
    }

    public String getConnectionStateLabel() {
        return CONNECTION_STATE_LABELS[mConnectionState];
    }

    private void log(int level, String message) {
        Log.println(level, TAG, message);
        mLogPublishSubject.onNext(message);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

        log(Log.DEBUG, String.format("onConnectionStateChange: status=%d newState=%d", status, newState));

        switch (newState) {

            case BluetoothGatt.STATE_CONNECTED:

                mConnectionState = CONNECTION_STATE_CONNECTED_DISCOVERING_SERVICES;

                mBluetoothGatt = gatt;

                mCharacteristicReadSubject = PublishSubject.create();
                mCharacteristicWriteSubject = PublishSubject.create();
                mDescriptorWriteSubject = PublishSubject.create();

                mBluetoothGatt.discoverServices();

                break;

            case BluetoothGatt.STATE_CONNECTING:
                mConnectionState = CONNECTION_STATE_CONNECTING;

                break;
            case BluetoothGatt.STATE_DISCONNECTED:

                onDisconnect();

                mConnectionState = CONNECTION_STATE_DISCONNECTED;

                mCharacteristicReadSubject.onCompleted();
                mCharacteristicWriteSubject.onCompleted();
                mDescriptorReadSubject.onCompleted();
                mDescriptorWriteSubject.onCompleted();

                mBluetoothSubscriptions.clear();

                mBluetoothGatt.close();
                mBluetoothGatt = null;

                mCharacteristicReadSubject = null;
                mCharacteristicWriteSubject = null;
                mDescriptorWriteSubject = null;

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

        log(Log.ERROR,"onServicesDiscovered...");

        if (status == BluetoothGatt.GATT_SUCCESS) {

            BluetoothGattService service = gatt.getService(getServiceUUID());

            if (service != null) {

                mReadCharacteristic = service.getCharacteristic(getReadCharacteristicUUID());
                mNotifyCharacteristic = service.getCharacteristic(getNotifyCharacteristicUUID());
                mWriteCharacteristic = service.getCharacteristic(getWriteCharacteristicUUID());

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        mBluetoothGatt.setCharacteristicNotification(mNotifyCharacteristic, true);

                        if (getSetupNotifications()) {

                            final BluetoothGattDescriptor descriptor =
                                    mReadCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);

                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                            Schedulers.io().createWorker().schedule(new Action0() {
                                @Override
                                public void call() {
                                    mBluetoothGatt.writeDescriptor(descriptor);
                                }
                            });

                            mDescriptorWriteSubject.toBlocking().next().iterator().next();
                        }

                        mConnectionState = CONNECTION_STATE_CONNECTED;

                        mBluetoothSubscriptions.add(mBluetoothCommandSubject
                                .observeOn(Schedulers.io())
                                .subscribe(
                                        new Observer<BluetoothWriteOperation>() {
                                            @Override
                                            public void onCompleted() {

                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                log(Log.DEBUG, "mBluetoothCommandSubject.onError: " + e.getMessage());
                                            }

                                            @Override
                                            public void onNext(BluetoothWriteOperation bluetoothWriteOperation) {
                                                log(Log.DEBUG, String.format("executing command... %s",
                                                        bluetoothWriteOperation.toString()));

                                                mNotifyCharacteristic.setWriteType(
                                                        BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                                                mNotifyCharacteristic.setValue(bluetoothWriteOperation.getRequest());
                                                mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);

                                                try {
                                                    Thread.sleep(150);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }));

                        onConnect();

                        mChangeSubject.onNext(this);
                        mDeviceEventSubject.onNext(new DeviceConnectedEvent());


                    }
                }).start();

            } else {
                disconnect();
            }
        } else {
            disconnect();
        }
    }

    protected void onConnect() {

    }

    protected void onDisconnect() {

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        log(Log.DEBUG, String.format("characteristicChanged: %s", BluetoothWriteOperation.getReadableStringFromByteArray(value)));
        mNotifyEventSubject.onNext(new ValueChangeEvent(characteristic.getUuid(),value));
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        byte[] value = characteristic.getValue();
        log(Log.DEBUG, String.format("characteristicRead: %s", BluetoothWriteOperation.getReadableStringFromByteArray(value)));
        mNotifyEventSubject.onNext(new ValueChangeEvent(characteristic.getUuid(),value));
        mCharacteristicReadSubject.onNext(0);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        log(Log.DEBUG, String.format("onCharacteristicWrite: %s",
                BluetoothWriteOperation.getReadableStringFromByteArray(characteristic.getValue())));
        mCharacteristicWriteSubject.onNext(0);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        log(Log.DEBUG, String.format("onDescriptorWrite: %s",
                BluetoothWriteOperation.getReadableStringFromByteArray(descriptor.getValue())));
        mDescriptorWriteSubject.onNext(0);
    }

    public Observable<DeviceEvent> getDeviceEventObservable() {
        return mDeviceEventSubject;
    }

    public Observable<Object> getChangeObservable() {
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
