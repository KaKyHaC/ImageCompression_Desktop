package ImageCompression.Utils.Objects

import java.math.BigInteger
import java.util.Vector

/**
 * Created by Димка on 27.09.2016.
 */
class DataOPC {

    var base: ShortArray
    var sign: Array<BooleanArray>
    var dc: Short = 0
    var N: BigInteger
    var vectorCode: Vector<Long>

    init {
        base = ShortArray(SIZEOFBLOCK)
        sign = Array(SIZEOFBLOCK) { BooleanArray(SIZEOFBLOCK) }
        N = BigInteger("0")
        vectorCode = Vector()
    }


    fun BinaryStringGet(): ByteArray {
        return N.toByteArray()
    }

    fun BinaryStringSet(code: ByteArray) {
        N = BigInteger(code)
    }

    fun SignToString(): ByteArray {
        val res = ByteArray(SIZEOFBLOCK)
        for (i in 0 until SIZEOFBLOCK) {
            for (j in 0 until SIZEOFBLOCK) {
                res[i] = res[i] shl 1
                if (sign[i][j]) {
                    res[i] = res[i] or 1
                }
            }
        }
        return res
    }

    fun SingFromString(s: ByteArray) {
        for (i in 0 until SIZEOFBLOCK) {
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                sign[i][j] = s[i] and 1 == 1
                s[i] = s[i] shr 1
            }
        }
    }

    fun FromBaseToString(): ShortArray {
        return base
    }

    fun FromStringToBase(b: ShortArray) {
        base = b
    }

    fun toString(flag: Flag): String {
        val sb = StringBuilder()
        for (b in FromBaseToString())
            sb.append(b.toInt())

        return sb.toString()
    }

    companion object {
        internal val SIZEOFBLOCK = 8
    }
}
