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

class PumpHardware(context: Context, bluetoothDevice: BluetoothDevice): BedHardware(context, bluetoothDevice) {

    companion object {
        private val inflateServiceUUID: UUID = BluetoothUtils.uuidFrom16BitUuid(0xffe0)
        private val inflateWriteCharUUID: UUID = BluetoothUtils.uuidFrom16BitUuid(0xffe1)
    }

    fun connect(): Flowable<PumpHardware> = _connect()

    fun inflate(inflateLeftChamber: Boolean, pressure: Int) : Flowable<PumpHardware> {
        val valueToWrite = createSetPressureCommand(inflateLeftChamber, pressure)
        return connect()
                .flatMap {
                    val service = getService(inflateServiceUUID)
                    val characteristic = getCharacteristic(service, inflateWriteCharUUID)
                    writeValue<PumpHardware>(characteristic, valueToWrite)
                }
    }

    private fun createSetPressureCommand(inflateLeftChamber: Boolean, pressure: Int) : ByteArray{
        return ByteCommandBuilder()
                .write('S')
                .write(if (inflateLeftChamber) 'L' else 'R')
                .write(PumpCommandAction.SET_PRESSURE.value)
                .write(String.format("%03d", (pressure % 1000)).toByteArray())
                .write('\n')
                .build()
    }

    enum class PumpCommandAction(val value: Char) {
        CHECK_MEMORY_VALUE('1'),
        CHECK_PRESSURE('2'),
        INFLATE('3'),
        DEFLATE('4'),
        RUN_FOR_MEMORY_VALUE('5'),
        SELF_ADAPT('6'),
        SAVE_MEMORY_VALUE('7'),
        RE_INFLATE('8'),
        STOP('9'),
        ADJUST_ZERO('a'),
        SET_PRESSURE('b'),
        SET_MEMORY_VALUE('c')
    }


}

class ByteCommandBuilder {

    companion object {
        private val byteArraySize = 15
    }

    var mByteArray = ByteArray(byteArraySize)
    var mLength = 0

    fun write(value: Int) : ByteCommandBuilder {
        mByteArray[mLength++] = (value and 0xff).toByte()
        return this
    }

    fun write(value: ByteArray) : ByteCommandBuilder {
        for (byte in value) {
            mByteArray[mLength++] = byte
        }
        return this
    }

    fun write(char: Char) : ByteCommandBuilder {
        mByteArray[mLength++] = (char.toInt() and 0xff).toByte();
        return this
    }

    fun build() : ByteArray {
        return mByteArray
    }

    fun writeUInt32(value: Int) {
        mByteArray[mLength++] = (value and 0xff).toByte()
        mByteArray[mLength++] = ((value shr 8) and 0xff).toByte()
        mByteArray[mLength++] = ((value shr 16) and 0xff).toByte()
        mByteArray[mLength++] = ((value shr 24) and 0xff).toByte()
    }
}