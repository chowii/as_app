package au.com.ahbeard.sleepsense.hardware

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.Context
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by luisramos on 22/02/2017.
 */
abstract class BedHardware(
        context: Context,
        val bluetoothDevice: BluetoothDevice
) {

    companion object {
        val firstConnectionTimeout : Long = 10L
    }

    private val mContext: Context = context.applicationContext
    private var mBluetoothGatt: BluetoothGatt? = null

    private val mBluetoothGattProcessor = BluetoothGattProcessor()

    private var mLastConnectionTimestamp: Long = 0L

    fun connect() : Flowable<BedHardware> {
        return Flowable.defer {
            updateConnectionTimestamp()
            if (mBluetoothGatt == null) {
                mBluetoothGatt = bluetoothDevice.connectGatt(mContext, false, mBluetoothGattProcessor)

                mBluetoothGattProcessor.waitForConnection(firstConnectionTimeout)
                        .map { this@BedHardware }
            } else {
                Flowable.just(this@BedHardware)
            }
        }
    }

    private fun updateConnectionTimestamp() {
        mLastConnectionTimestamp = Date().time
    }
}

class PumpHardware(context: Context, bluetoothDevice: BluetoothDevice): BedHardware(context, bluetoothDevice)

open class BluetoothGattEvent

class BluetoothGattEventConnectionStateChange(val status: Int, val newState: Int) : BluetoothGattEvent()

open class BluetoothGattEventError : Throwable()

class BluetoothGattEventConnectionTimeoutError : BluetoothGattEventError()

class BluetoothGattProcessor: BluetoothGattCallback() {

    private val mProcessor = PublishProcessor.create<BluetoothGattEvent>()

    fun waitForConnection(connectionTimeoutSeconds: Long) : Flowable<BluetoothGattEventConnectionStateChange> {
        return getBluetoothGattEventFlowable()
                .filter { it is BluetoothGattEventConnectionStateChange }
                .cast(BluetoothGattEventConnectionStateChange::class.java)
                .filter { it.newState == BluetoothGatt.STATE_CONNECTED }
                .timeout(connectionTimeoutSeconds, TimeUnit.SECONDS)
                .take(1)
    }

    fun getBluetoothGattEventFlowable() : Flowable<BluetoothGattEvent> {
        return mProcessor.hide()
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        mProcessor.onNext(BluetoothGattEventConnectionStateChange(status, newState))
    }
}