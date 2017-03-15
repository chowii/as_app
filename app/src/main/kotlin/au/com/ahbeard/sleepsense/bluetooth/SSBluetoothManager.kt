package au.com.ahbeard.sleepsense.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * Created by luisramos on 10/03/2017.
 */
object SSBluetoothManager {

    private val SCAN_TIMEOUT_SECS = 12L

    private val mAdapter by lazy {
        // This will only initialize the variable after we
        // check if it is supported
        BluetoothAdapter.getDefaultAdapter()!!
    }

    private var mContext: Context by Delegates.notNull()

    private var mStatePublishSubject = PublishSubject.create<BluetoothState>()
    private var mScanPublishSubject = PublishSubject.create<ScannedDevice>()

    @JvmStatic fun initialize(context: Context) {
        mContext = context.applicationContext

        mContext.registerReceiver(mStateBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    @JvmStatic fun monitorState() : Observable<BluetoothState> {
        return mStatePublishSubject.hide()
    }

    @JvmStatic fun waitForPowerOn() : Observable<Any> {
        return Observable.defer {
            if (mAdapter.isEnabled) {
                Observable.just(true)
            } else {
                monitorState()
                        .skipWhile { it != BluetoothState.ON }
                        .take(1)
            }
        }
    }

    @JvmStatic fun scanDevices() : Single<List<ScannedDevice>> {
        return Single.defer {
            mContext.registerReceiver(mDiscoveryBroadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
            mAdapter.startDiscovery()

            mScanPublishSubject.hide()
                    .take(SCAN_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .toList()
                    .map {
                        it.sortedWith(Comparator { o1, o2 ->
                            if (o1.rssi > o2.rssi) {
                                1
                            } else if (o1.rssi > o2.rssi) {
                                -1
                            } else {
                                0
                            }
                        })
                    }
                    .doFinally {
                        mAdapter.cancelDiscovery()
                        mContext.unregisterReceiver(mDiscoveryBroadcastReceiver)
                    }
        }
    }

    private val mStateBroadcastReceiver = object:BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val adapterState = intent.extras.getInt(BluetoothAdapter.EXTRA_STATE)
                    val newState = when (adapterState) {
                        BluetoothAdapter.STATE_ON -> BluetoothState.ON
                        BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.TURNING_ON
                        BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.TURNING_OFF
                        else -> BluetoothState.OFF
                    }
                    mStatePublishSubject.onNext(newState)
                }
            }
        }
    }

    private val mDiscoveryBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, -120)

                    mScanPublishSubject.onNext(ScannedDevice(device.name, rssi, device))
                }
            }
        }
    }

    class ScannedDevice(val name: String, val rssi: Short, val device: BluetoothDevice)

    enum class BluetoothState {
        ON, TURNING_ON, OFF, TURNING_OFF
    }
}