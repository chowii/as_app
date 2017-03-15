package au.com.ahbeard.sleepsense.hardware

import android.bluetooth.*
import android.content.Context
import android.os.Handler
import android.os.Looper
import au.com.ahbeard.sleepsense.bluetooth.BedHardwareBluetoothGathCallback
import au.com.ahbeard.sleepsense.bluetooth.BluetoothUtils
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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

    private val mBluetoothGattProcessor = BedHardwareBluetoothGathCallback()

    private var mLastConnectionTimestamp: Long = 0L

    @Suppress("UNCHECKED_CAST")
    open fun <T: BedHardware> _connect() : Flowable<T> {
        return Flowable.defer {
            updateConnectionTimestamp()

            //This needs to always happen on the uiThread
            Handler(Looper.getMainLooper()).post {
                mBluetoothGatt = bluetoothDevice.connectGatt(mContext, false, mBluetoothGattProcessor)
            }

            if (mBluetoothGatt == null) {
                mBluetoothGattProcessor.waitForConnection(firstConnectionTimeout)
                        .map { this@BedHardware as T }
            } else {
                Flowable.just(this@BedHardware as T)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: BedHardware> writeValue(characteristic: BluetoothGattCharacteristic?, value: ByteArray) : Flowable<T> {
        return Flowable.defer {
            if (characteristic != null) {
                _writeValue(characteristic, value)
                mBluetoothGattProcessor.waitForCharacteristicWrite(characteristic)
                        .map { this@BedHardware as T }
            } else {
                Flowable.error(WriteCharacteristicError())
            }
        }
    }

    protected fun getCharacteristic(service: BluetoothGattService?, characteristicUUID: UUID) : BluetoothGattCharacteristic? {
        return service?.getCharacteristic(characteristicUUID)
    }

    protected fun getService(serviceUUID: UUID) : BluetoothGattService? {
        return mBluetoothGatt?.getService(serviceUUID)
    }

    private fun updateConnectionTimestamp() {
        mLastConnectionTimestamp = Date().time
    }

    private fun _writeValue(characteristic: BluetoothGattCharacteristic, value: ByteArray) {
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        characteristic.value = value
        mBluetoothGatt?.writeCharacteristic(characteristic)
    }

    class WriteCharacteristicError: Throwable()
}