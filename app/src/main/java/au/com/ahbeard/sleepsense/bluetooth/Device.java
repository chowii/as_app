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

import au.com.ahbeard.sleepsense.utils.ByteUtils;
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

    public static final String[] CONNECTION_STATE_LABELS =
            {"Unlinked", "Disconnected", "Connecting", "Connecting Discovering Services", "Connected", "Disconnecting"};

    private static final String TAG = "Device";

    // Constants for the Simblee chip.
    private static final UUID SERVICE_UUID = UUID.fromString("00002220-0000-1000-8000-00805f9b34fb");

    private static final UUID RECEIVE_CHARACTERISTIC_UUID = UUID.fromString("00002221-0000-1000-8000-00805f9b34fb");
    private static final UUID SEND_CHARACTERISTIC_UUID = UUID.fromString("00002222-0000-1000-8000-00805f9b34fb");

    private static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private PublishSubject<BluetoothCommand> mBluetoothCommandSubject = PublishSubject.create();
    private PublishSubject<DeviceEvent> mDeviceEventSubject = PublishSubject.create();

    // Per connection
    private PublishSubject<Integer> mCharacteristicWriteSubject;
    private PublishSubject<Integer> mDescriptorWriteSubject;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;

    private BluetoothGattCharacteristic mReceiveCharacteristic;
    private BluetoothGattCharacteristic mSendCharacteristic;

    private CompositeSubscription mBluetoothSubscriptions = new CompositeSubscription();

    private PublishSubject<Object> mChangeSubject = PublishSubject.create();

    private Context mContext;

    private int mConnectionState;

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

    public void sendCommand(BluetoothCommand command) {
        Log.d("DEVICE", String.format("sending command... %s", command.toString()));
        mBluetoothCommandSubject.onNext(command);
    }

    public void connect() {
        Log.d("DEVICE", "connecting...");
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

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

        Log.d(TAG,String.format("onConnectionStateChange: status=%d newState=%d",status,newState));

        switch (newState) {

            case BluetoothGatt.STATE_CONNECTED:

                mConnectionState = CONNECTION_STATE_CONNECTED_DISCOVERING_SERVICES;

                mBluetoothGatt = gatt;

                mCharacteristicWriteSubject = PublishSubject.create();
                mDescriptorWriteSubject = PublishSubject.create();

                mBluetoothGatt.discoverServices();

                break;

            case BluetoothGatt.STATE_CONNECTING:
                mConnectionState = CONNECTION_STATE_CONNECTING;

                break;
            case BluetoothGatt.STATE_DISCONNECTED:

                mConnectionState = CONNECTION_STATE_DISCONNECTED;

                mCharacteristicWriteSubject.onCompleted();
                mDescriptorWriteSubject.onCompleted();

                mBluetoothSubscriptions.clear();

                mBluetoothGatt.close();
                mBluetoothGatt = null;

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
        if (status == BluetoothGatt.GATT_SUCCESS) {

            BluetoothGattService service = gatt.getService(SERVICE_UUID);

            if (service != null) {

                mReceiveCharacteristic = service.getCharacteristic(RECEIVE_CHARACTERISTIC_UUID);
                mSendCharacteristic = service.getCharacteristic(SEND_CHARACTERISTIC_UUID);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        mBluetoothGatt.setCharacteristicNotification(mReceiveCharacteristic, true);

                        final BluetoothGattDescriptor descriptor =
                                mReceiveCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);

                        if (!ByteUtils.equals(descriptor.getValue(),
                                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) {
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
                                        new Observer<BluetoothCommand>() {
                                            @Override
                                            public void onCompleted() {

                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.d(TAG, "mBluetoothCommandSubject.onError", e);
                                            }

                                            @Override
                                            public void onNext(BluetoothCommand bluetoothCommand) {
                                                Log.d("DEVICE", String.format("executing command... %s",
                                                        bluetoothCommand.toString()));

                                                mSendCharacteristic.setWriteType(
                                                        BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                                                mSendCharacteristic.setValue(bluetoothCommand.getRequest());
                                                mBluetoothGatt.writeCharacteristic(mSendCharacteristic);

                                                try {
                                                    Thread.sleep(150);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }));

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

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        Log.d(TAG, String.format("characteristicChanged: %s", BluetoothCommand.getReadableStringFromByteArray(value)));

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.d(TAG, String.format("onCharacteristicWrite: %s",
                BluetoothCommand.getReadableStringFromByteArray(characteristic.getValue())));
        mCharacteristicWriteSubject.onNext(0);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.d(TAG, String.format("onDescriptorWrite: %s",
                BluetoothCommand.getReadableStringFromByteArray(descriptor.getValue())));
        mDescriptorWriteSubject.onNext(0);
    }

    public PublishSubject<Object> getChangeSubject() {
        return mChangeSubject;
    }

    public PublishSubject<DeviceEvent> getDeviceEventSubject() {
        return mDeviceEventSubject;
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
