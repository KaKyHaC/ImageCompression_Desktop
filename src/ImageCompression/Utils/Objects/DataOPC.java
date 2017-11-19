package ImageCompression.Utils.Objects

import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Vector

/**
 * Created by Димка on 27.09.2016.
 */
class DataOPC {

    var base: ShortArray
    var sign: Array<BooleanArray>
    var DC: Short = 0
    var N: BigInteger
    var vectorCode: Vector<Long>

    val dc: ByteArray
        get() = DC

    init {
        base = ShortArray(SIZEOFBLOCK)
        sign = Array(SIZEOFBLOCK) { BooleanArray(SIZEOFBLOCK) }
        N = BigInteger("0")
        vectorCode = Vector()
    }


    fun FromBigIntToArray(): ByteArray {
        return N.toByteArray()
    }

    fun FromArrayToBigInt(code: ByteArray) {
        N = BigInteger(code)
    }

    fun FromSignToArray(): ByteArray {
        val res = ByteArray(SIZEOFBLOCK / 2)
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

    fun FromArrayToSing(s: ByteArray) {
        for (i in SIZEOFBLOCK - 1 downTo 0) {
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                sign[i][j] = s[i] and 1 == 1
                s[i] = s[i] shr 1
            }
        }
    }

    fun FromBaseToArray(): ByteArray {
        val ofset = SIZEOFBLOCK / 2

        val res = ByteArray(SIZEOFBLOCK / 2)
        for (i in res.indices) {
            res[i] = base[i].toByte()
        }
        return res
    }

    fun FromArrayToBase(b: ByteArray) {
        for (i in b.indices) {
            base[i] = (b[i] and 0xff).toShort()
        }
    }

    fun setDC(DC: Short) {
        this.DC = DC
    }

    fun toString(flag: Flag): String {
        val offset = SIZEOFBLOCK / 2
        val sb = StringBuilder()

        if (flag.isOneFile && !flag.isGlobalBase)
            for (b in FromBaseToArray())
                sb.append(b.toChar())

        val sign = FromSignToArray()
        for (i in sign.indices) {
            sb.append(sign[i].toChar())
        }

        if (flag.isDC)
            sb.append(DC.toChar())

        if (!flag.isLongCode) {
            val n = FromBigIntToArray()
            sb.append(n.size.toChar())
            for (c in n) {
                sb.append(c.toShort().toInt())
            }
        } else {
            sb.append(vectorCode.size.toChar())
            for (i in vectorCode.indices) {
                var v = vectorCode.elementAt(i)
                sb.append(v.toChar())
                v = v shr 16
                sb.append(v.toChar())
                v = v shr 16
                sb.append(v.toChar())
                v = v shr 16
                sb.append(v.toChar())
            }
        }

        return sb.toString()
    }

    fun valueOf(s: String, flag: Flag) {
        val offset = SIZEOFBLOCK / 2
        var index = 0

        if (flag.isOneFile && !flag.isGlobalBase) {
            val a = ShortArray(offset)
            for (i in 0 until offset) {
                a[i] = s[index++].toShort()
            }
            FromArrayToBase(a)
        }

        val sign = ShortArray(offset)
        for (i in 0 until offset) {
            sign[i] = s[index++].toShort()
        }
        FromArrayToSing(sign)

        if (flag.isDC)
            DC = s[index++].toShort()

        if (!flag.isLongCode) {
            val length = s[index++].toInt()
            val code = ShortArray(length)
            for (i in 0 until length) {
                code[i] = s[index++].toShort()
            }
            FromArrayToBigInt(code)
        } else {
            val length = s[index++].toInt()
            for (i in 0 until length) {
                var v = s[index++].toLong()
                v = v or (s[index++].toLong() shl 16)
                v = v or (s[index++].toLong() shl 32)
                v = v or (s[index++].toLong() shl 48)
                vectorCode.add(v)
            }
        }

    }

    companion object {
        internal val SIZEOFBLOCK = 8
    }
}
