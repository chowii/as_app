package au.com.ahbeard.sleepsense.hardware.bedBase

import android.bluetooth.BluetoothDevice
import android.content.Context
import au.com.ahbeard.sleepsense.hardware.BedHardware
import io.reactivex.Flowable

/**
 * Created by luisramos on 13/03/2017.
 */
class BedBaseHardware(context: Context, bluetoothDevice: BluetoothDevice): BedHardware(context, bluetoothDevice) {

    fun connect(): Flowable<BedBaseHardware> = _connect()
}