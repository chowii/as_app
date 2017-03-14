package au.com.ahbeard.sleepsense.hardware.pump

import android.bluetooth.BluetoothDevice
import android.content.Context
import au.com.ahbeard.sleepsense.bluetooth.BluetoothUtils
import au.com.ahbeard.sleepsense.hardware.BedHardware
import au.com.ahbeard.sleepsense.hardware.util.ByteCommandBuilder
import io.reactivex.Flowable
import java.util.*

/**
 * Created by luisramos on 13/03/2017.
 */
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