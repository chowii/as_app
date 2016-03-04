package au.com.ahbeard.sleepsense.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by neal on 16/12/2015.
 */
public class BluetoothScanEvent {

    public static class ScanningStartedEvent extends BluetoothScanEvent {

    }

    public static class ScanningStoppedEvent extends BluetoothScanEvent {

    }

    public static class DeviceFoundEvent extends BluetoothScanEvent {

        private BluetoothDevice mDevice;
        private byte[] mAdvertisingData;
        private int mRssi;

        public DeviceFoundEvent(BluetoothDevice device, byte[] advertisingData, int rssi) {
            mDevice = device;
            mAdvertisingData = advertisingData;
            mRssi = rssi;
        }

        public BluetoothDevice getDevice() {
            return mDevice;
        }

        public byte[] getAdvertisingData() {
            return mAdvertisingData;
        }

        public int getRssi() {
            return mRssi;
        }
    }

    public static class BluetoothDisabledEvent extends BluetoothScanEvent {

    }



}
