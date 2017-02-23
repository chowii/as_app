package au.com.ahbeard.sleepsense.bluetooth.tracker;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.PowerManager;
import android.util.SparseArray;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

import au.com.ahbeard.sleepsense.bluetooth.CharacteristicWriteOperation;
import au.com.ahbeard.sleepsense.bluetooth.Device;
import au.com.ahbeard.sleepsense.bluetooth.EnableNotificationOperation;
import au.com.ahbeard.sleepsense.bluetooth.ValueChangeEvent;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by neal on 4/03/2016.
 */
public class LiveFeedbackTrackerDevice extends Device {

    private PublishSubject<TrackerState> mTrackerStateSubject = PublishSubject.create();
    private PublishSubject<Integer> mPacketCountSubject = PublishSubject.create();

    private int mPacketCount = 0;
    private PowerManager.WakeLock mWakeLock;

    private TrackerState mTrackerState;

    private static final UUID BEDDIT_SERVICE_UUID = UUID.fromString("f82fd8a8-329d-4c44-a178-e82f91ec9fe6");
    private static final UUID SAMPLE_RATE_CHARACTERISTIC_UUID = UUID.fromString("f82fd8aa-329d-4c44-a178-e82f91ec9fe6");
    private static final UUID SIGNAL_DATA_CHARACTERISTIC_UUID = UUID.fromString("f82fd8a9-329d-4c44-a178-e82f91ec9fe6");

    private static final UUID DEVICE_INFORMATION_SERVICE_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    private static final UUID DEVICE_INFORMATION_SERIAL_NUMBER_UUID = UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb");
    private static final UUID DEVICE_INFORMATION_HARDWARE_REVISION_UUID = UUID.fromString("00002A27-0000-1000-8000-00805f9b34fb");
    private static final UUID DEVICE_INFORMATION_FIRMWARE_REVISION_UUID = UUID.fromString("00002A26-0000-1000-8000-00805f9b34fb");
    private static final UUID DEVICE_INFORMATION_SOFTWARE_REVISION_UUID = UUID.fromString("00002A28-0000-1000-8000-00805f9b34fb");


    private static final byte CONTROL_POINT_COMMAND_START = 0x01;

    private static final int PACKET_TYPE_DEFAULT = 0x80;
    private static final int PACKET_TYPE_NO_MOVEMENT = 0x90;

    private static final String TRACK_NAME = "force";

    private PublishProcessor<byte[]> mSessionPublishSubject = null;
    private Disposable mNotifySubscription;

    @Override
    public UUID getServiceUUID() {
        return BEDDIT_SERVICE_UUID;
    }

    @Override
    public UUID getWriteCharacteristicUUID() {
        return SIGNAL_DATA_CHARACTERISTIC_UUID;
    }

    @Override
    public UUID getReadCharacteristicUUID() {
        return SIGNAL_DATA_CHARACTERISTIC_UUID;
    }

    @Override
    public UUID getNotifyCharacteristicUUID() {
        return SIGNAL_DATA_CHARACTERISTIC_UUID;
    }

    // Get the tracker state.
    public TrackerState getTrackerState() {
        return mTrackerState;
    }

    public enum TrackerState {
        Disconnected,
        Connecting,
        Connected,
        Tracking,
        Disconnecting
    }

    @Override
    protected boolean getSetupNotifications() {
        return false;
    }

    public Flowable<byte[]> startSession() {
        if (mSessionPublishSubject == null ) {
            createSessionPublishSubject();
        }

        return mSessionPublishSubject.hide().onBackpressureDrop();
    }

    private void createSessionPublishSubject() {
        mSessionPublishSubject = PublishProcessor.create();

        // Acquire a wake lock.
        PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "SleepTrackingWakeLock");
        mWakeLock.acquire();

        // Load the JNI analysis library.
        try {
            // Hack to load the library here.  It was the ONLY way I could get JNI to load the included
            // .so library for the Beddit jar file. The .so file probably needs to be contained in the
            // JAR file to get linked properly.
            Runtime runtime = Runtime.getRuntime();
            runtime.loadLibrary("mobile-analysis-jni");
        } catch (Exception e) {
            e.printStackTrace();
        }

        getDeviceEventObservable()
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<DeviceEvent>() {
                    @Override
                    public void accept(DeviceEvent deviceEvent) {
                        if ( deviceEvent instanceof DeviceDisconnectedEvent ) {
                            if (mNotifySubscription != null ) {
                                mNotifySubscription.dispose();
                                mNotifySubscription = null;
                            }
                            if ( mSessionPublishSubject != null ) {
                                mSessionPublishSubject.onComplete();
                                mSessionPublishSubject = null;
                            }
                        }
                    }
                });

        if ( mNotifySubscription != null) {
            mNotifySubscription.dispose();
            mNotifySubscription = null;
        }

        mNotifySubscription = getNotifyEventObservable()
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<ValueChangeEvent>() {

                    int currentPacketNumber = 0;
//            int currentSampleNumber = 0;

                    @Override
                    public void accept(ValueChangeEvent valueChangeEvent) {

                        byte[] data = valueChangeEvent.getValue();

                        if (data.length < 4 || data.length % 2 != 0) {
                            // closeWithError(new SensorProtocolViolationException("Invalid packet length"));
                            return;
                        }

                        int packetTypeCode = data[0] & 0xFF;
                        int packetNumber = data[1] & 0xFF;

                        byte[] sampleData = Arrays.copyOfRange(data, 2, data.length);
                        int samplesInCurrentPacket = sampleData.length / 2;

                        if (!(packetTypeCode == PACKET_TYPE_DEFAULT || packetTypeCode == PACKET_TYPE_NO_MOVEMENT)) {
                            // closeWithError(new SensorProtocolViolationException("Invalid packet type code", String.valueOf(packetTypeCode)));
                            return;
                        }

                        if (packetNumber != this.currentPacketNumber) {
                            int packetsLost = packetNumber - this.currentPacketNumber;
                            if (packetsLost < 0) {
                                packetsLost = packetsLost + 256;
                            }

                            // We assume that the lost packets have the same length as this one.
                            // It may or may not be like so, but tests have shown that all packets
                            // always have the same length.
                            final int samplesToRepeat = packetsLost * samplesInCurrentPacket;

                            byte[] firstSample = Arrays.copyOfRange(sampleData, 2, 4);

                            sampleData = padSampleDataPacket(sampleData, firstSample, samplesToRepeat);

                            this.currentPacketNumber = packetNumber;

                        }

//                if (sessionListener != null) {
//                    sessionListener.onSensorSessionReceivedData(LESensorSession.this,
//                            sampleData, TRACK_NAME, this.currentSampleNumber);
//                }

                        mSessionPublishSubject.onNext(sampleData);

//                int numberOfSamples = sampleData.length / 2;
//
//                this.currentSampleNumber += numberOfSamples;
//                this.currentPacketNumber += 1;
//                this.currentPacketNumber &= 0xFF;
                    }
                });

        EnableNotificationOperation enableNotificationCommand = new EnableNotificationOperation(BEDDIT_SERVICE_UUID, SIGNAL_DATA_CHARACTERISTIC_UUID);
        sendCommand(enableNotificationCommand);

        // new TrackingSessionAnalyser(this)
//        sendCommand(new CharacteristicReadOperation(DEVICE_INFORMATION_SERVICE_UUID,DEVICE_INFORMATION_SERIAL_NUMBER_UUID));
//        sendCommand(new CharacteristicReadOperation(DEVICE_INFORMATION_SERVICE_UUID,DEVICE_INFORMATION_HARDWARE_REVISION_UUID));
//        sendCommand(new CharacteristicReadOperation(DEVICE_INFORMATION_SERVICE_UUID,DEVICE_INFORMATION_FIRMWARE_REVISION_UUID));
//        sendCommand(new CharacteristicReadOperation(DEVICE_INFORMATION_SERVICE_UUID,DEVICE_INFORMATION_SOFTWARE_REVISION_UUID));
//
//        sendCommand(new CharacteristicReadOperation(BEDDIT_SERVICE_UUID,SAMPLE_RATE_CHARACTERISTIC_UUID));

        // This will connect and start streaming shit.
        CharacteristicWriteOperation startCommand = new CharacteristicWriteOperation(BEDDIT_SERVICE_UUID, SIGNAL_DATA_CHARACTERISTIC_UUID);
        startCommand.writeByte(CONTROL_POINT_COMMAND_START);
        sendCommand(startCommand);
    }

    public void stopSession() {

        disconnect();

    }

    public boolean isTracking() {
        return mSessionPublishSubject != null;
    }

    public Observable<TrackerState> getTrackingStateObservable() {
        return mTrackerStateSubject;
    }

    public Observable<Integer> getPacketCountObservable() {
        return mPacketCountSubject;
    }

    public void logPacket() {
        mPacketCountSubject.onNext(++mPacketCount);
    }

    public void setTrackerState(TrackerState trackerState) {
        mTrackerState = trackerState;
        mTrackerStateSubject.onNext(mTrackerState);
    }


    private byte[] padSampleDataPacket(byte[] originalSampleData,
                                       byte[] sampleToRepeat, int numberOfSamplesToRepeat) {

        int sampleSize = 2;

        int samplesInOriginalPacket = originalSampleData.length / sampleSize;

        int samplesInPaddedPacket = numberOfSamplesToRepeat + samplesInOriginalPacket;
        int bytesInPaddedPacket = samplesInPaddedPacket * sampleSize;

        ByteBuffer paddingBuffer = ByteBuffer.allocateDirect(bytesInPaddedPacket);

        for (int i = 0; i < numberOfSamplesToRepeat; i++) {
            paddingBuffer.put(sampleToRepeat);
        }

        paddingBuffer.put(originalSampleData);

        byte[] newSampleData = new byte[bytesInPaddedPacket];
        paddingBuffer.rewind();
        int bytesRead = paddingBuffer.get(newSampleData).position();
        if (bytesRead != bytesInPaddedPacket) {
            throw new IllegalStateException("ByteBuffer did not return the nuber of bytes we asked");
        }

        return newSampleData;
    }

    private static String getGattErrorDescription(int status) {
        String message = GATT_ERROR_MAP.get(status);

        if (message == null) {
            message = Integer.toString(status);
        }

        return message;
    }

    private static final SparseArray<String> GATT_ERROR_MAP = new SparseArray<String>();

    static {
        GATT_ERROR_MAP.put(BluetoothGatt.GATT_READ_NOT_PERMITTED, "GATT_READ_NOT_PERMITTED");
        GATT_ERROR_MAP.put(BluetoothGatt.GATT_WRITE_NOT_PERMITTED, "GATT_WRITE_NOT_PERMITTED");
        GATT_ERROR_MAP.put(BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION, "GATT_INSUFFICIENT_AUTHENTICATION");
        GATT_ERROR_MAP.put(BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, "GATT_REQUEST_NOT_SUPPORTED");
        GATT_ERROR_MAP.put(BluetoothGatt.GATT_INVALID_OFFSET, "GATT_INVALID_OFFSET");
        GATT_ERROR_MAP.put(BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH, "GATT_INVALID_ATTRIBUTE_LENGTH");
    }





}
