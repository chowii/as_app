package au.com.ahbeard.sleepsense.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

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

    private PublishSubject<BluetoothEvent> mBluetoothEventSubject = PublishSubject.create();

    protected BluetoothService(Context context) {
        mContext = context;
    }

    private Map<String,Integer> mMaxRSSIs = Collections.synchronizedMap(new HashMap<String,Integer>());

    /**
     * Bluetooth LE scan callback for service discovery.
     */
    private BluetoothAdapter.LeScanCallback mScanCallback =

            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {

                    if (StringUtils.isNotEmpty(device.getName())) {
                        mBluetoothEventSubject.onNext(
                                new BluetoothEvent.PacketEvent(device, scanRecord, rssi));
                    }
                }
            };

    private boolean mScanning;

    public boolean isScanning() {
        return mScanning;
    }

    public Observable<BluetoothEvent> startScanning() {
        return mBluetoothEventSubject.doOnSubscribe(new Action0() {
            @Override
            public void call() {
                _scan(true);
            }
        });
    }

    public Observable<BluetoothEvent> getBluetoothEventObservable() {
        return mBluetoothEventSubject;
    }

    public void stopScanning() {
        _scan(false);
    }

    public boolean isBluetoothEnabled() {
        return isBluetoothEnabled(true);
    }

    /**
     * Checks if Bluetooth is enabled.
     * @param sendUseEvent Sends a "UseWhileDisabledEvent" if true to the bluetoothEventSubject
     * @return
     */
    public boolean isBluetoothEnabled(boolean sendUseEvent) {
        if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() ) {
            return true;
        } else if(sendUseEvent) {
            mBluetoothEventSubject.onNext(new BluetoothEvent.BluetoothUseWhileDisabledEvent());
        }
        return false;
    }

    public boolean isBluetoothDisabled() {
        return ! isBluetoothEnabled();
    }

    public Observable<BluetoothEvent> waitForPowerOn() {
        if (mBluetoothAdapter == null)
            initializeBluetoothAdapter();
        return Observable.create(new Observable.OnSubscribe<BluetoothEvent>() {
            @Override
            public void call(Subscriber<? super BluetoothEvent> subscriber) {
                if (isBluetoothEnabled(false)) {
                    subscriber.onNext(new BluetoothEvent.BluetoothEnabledEvent());
                } else {
                    mBluetoothEventSubject
                            .skipWhile(new Func1<BluetoothEvent, Boolean>() {
                                @Override
                                public Boolean call(BluetoothEvent bluetoothEvent) {
                                    return !(bluetoothEvent instanceof BluetoothEvent.BluetoothEnabledEvent);
                                }
                            })
                            .take(1) //we only want the next enabled event
                            .subscribe(subscriber);
                }
            }
        });
    }

    public Single<List<BluetoothEvent.PacketEvent>> scanForBLEDevices(long timeoutMilis, Predicate<BluetoothEvent> predicate) {
        return BluetoothService.instance()
                .startScanning()
                .observeOn(Schedulers.computation())
                .filter(predicate)
                .cast(BluetoothEvent.PacketEvent.class)
                .take(timeoutMilis, TimeUnit.MILLISECONDS)
                .toList();
    }

    private void _scan(final boolean enable) {

        initializeBluetoothAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            mBluetoothEventSubject.onNext(new BluetoothEvent.BluetoothUseWhileDisabledEvent());
        } else {
            if (enable) {

                mScanning = true;
                mScanning = mBluetoothAdapter.startLeScan(mScanCallback);
                mBluetoothEventSubject.onNext(new BluetoothEvent.ScanningStartedEvent());

            } else {

                mScanning = false;
                mBluetoothAdapter.stopLeScan(mScanCallback);
                mBluetoothEventSubject.onNext(new BluetoothEvent.ScanningStoppedEvent());

            }
        }

    }

    private final BroadcastReceiver mAdapterStateChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            // It means the user has changed his bluetooth state.
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    // The user bluetooth is turning off yet, but it is not disabled yet.
                    mBluetoothEventSubject.onNext(new BluetoothEvent.BluetoothEnabledEvent());
                }

                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    // The user bluetooth is already disabled.
                    mBluetoothEventSubject.onNext(new BluetoothEvent.BluetoothDisabledEvent());
                }

            }
        }
    };

    public BluetoothDevice createDeviceFromAddress(String address) {
        initializeBluetoothAdapter();
        if ( mBluetoothAdapter != null && address != null) {
            return mBluetoothAdapter.getRemoteDevice(address);
        } else {
            return null;
        }
    }

    private void initializeBluetoothAdapter() {
        // Get the Bluetooth adapter.
        if (mBluetoothAdapter == null) {
            // Initializes Bluetooth adapter.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);

            mBluetoothAdapter = bluetoothManager.getAdapter();

            mContext.registerReceiver(mAdapterStateChangeReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        }

    }
}
