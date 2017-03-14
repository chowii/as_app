package au.com.ahbeard.sleepsense.hardware.util

/**
 * Created by luisramos on 13/03/2017.
 */
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