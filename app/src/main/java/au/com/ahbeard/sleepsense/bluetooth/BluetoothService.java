package au.com.ahbeard.sleepsense.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import au.com.ahbeard.sleepsense.services.LogService;
import rx.functions.Action0;
import rx.subjects.PublishSubject;

/**
 * Created by neal on 16/12/2015.
 */
public class BluetoothService extends BluetoothGattCallback {

    private static final String TAG = "BluetoothService";

    private static BluetoothService sInstance;

    public static void initialize(Context context) {
        sInstance = new BluetoothService(context);
    }

    public static BluetoothService instance() {
        return sInstance;
    }

    private Context mContext;

    private BluetoothAdapter mBluetoothAdapter;

    private PublishSubject<BluetoothScanEvent> mBluetoothScanningSubject = PublishSubject.create();

    protected BluetoothService(Context context) {
        mContext = context;
    }

    /**
     * Bluetooth LE scan callback for service discovery.
     */
    private BluetoothAdapter.LeScanCallback mScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {

                    LogService.e(TAG, String.format("device: %s rssi: %d name: '%s'", device.getAddress(), rssi, device.getName()));

                    // Check the address of the device.
                    if (StringUtils.isNotEmpty(device.getName())) {
                        mBluetoothScanningSubject.onNext(
                                new BluetoothScanEvent.DeviceFoundEvent(device, scanRecord, rssi));
                    }
                }
            };

    private boolean mScanning;

    public boolean isScanning() {
        return mScanning;
    }

    // TODO: There is a chance this could start scanning before the subscription takes effect.
    public PublishSubject<BluetoothScanEvent> startScanning() {
        _scan(true);
        return mBluetoothScanningSubject;
    }

    public void stopScanning() {
        _scan(false);
    }

    private void _scan(final boolean enable) {

        initializeBluetoothAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            mBluetoothScanningSubject.onNext(new BluetoothScanEvent.BluetoothDisabledEvent());
        } else {
            if (enable) {

                mScanning = true;
                mScanning = mBluetoothAdapter.startLeScan(mScanCallback);
                mBluetoothScanningSubject.onNext(new BluetoothScanEvent.ScanningStartedEvent());

            } else {

                mScanning = false;
                mBluetoothAdapter.stopLeScan(mScanCallback);
                mBluetoothScanningSubject.onNext(new BluetoothScanEvent.ScanningStoppedEvent());

            }
        }


    }

    private void initializeBluetoothAdapter() {
        // Get the Bluetooth adapter.
        if (mBluetoothAdapter == null) {
            // Initializes Bluetooth adapter.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);

            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
    }


    public BluetoothDevice createDeviceFromAddress(String address) {
        initializeBluetoothAdapter();
        if ( mBluetoothAdapter != null ) {
            return mBluetoothAdapter.getRemoteDevice(address);
        } else {
            return null;
        }
    }
}
