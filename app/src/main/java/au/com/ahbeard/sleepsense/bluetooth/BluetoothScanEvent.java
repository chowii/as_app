package au.com.ahbeard.sleepsense.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by neal on 16/12/2015.
 */
public class BluetoothScanEvent {

    public static class ScanningStartedEvent extends BluetoothScanEvent {

    }

    public static class ScanningStoppedEvent extends BluetoothScanEvent {

    }

    public static class ScanPacketEvent extends BluetoothScanEvent {

        private BluetoothDevice mDevice;
        private byte[] mAdvertisingData;
        private int mRssi;
        private Set<Integer> mReceivedRssis;

        public ScanPacketEvent(BluetoothDevice device, byte[] advertisingData, int rssi) {
            mDevice = device;
            mAdvertisingData = advertisingData;
            mRssi = rssi;
            mReceivedRssis = new HashSet<>();
            mReceivedRssis.add(rssi);
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

        public Set<Integer> getReceivedRssis() {
            return mReceivedRssis;
        }

        public void addRssi(int rssi) {
            if ( rssi > mRssi ) {
                mRssi = rssi;
            }
            mReceivedRssis.add(rssi);
        }
    }

    public static class BluetoothDisabledEvent extends BluetoothScanEvent {

    }



}
