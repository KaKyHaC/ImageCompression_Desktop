package ImageCompression.Containers

import ImageCompression.Utils.Objects.ByteVector
import ImageCompression.Utils.Objects.Flag

import java.math.BigInteger
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

    var dc: ByteArray
        get() {
            val res = ByteArray(2)
            res[0] = (DC shr SIZEOFBLOCK).toByte()
            res[1] = DC.toByte()
            return res
        }
        set(bytes) {
            this.DC = bytes[0].toShort()
            DC = DC shl SIZEOFBLOCK.toShort()
            DC = DC or bytes[1].toShort()
        }

    init {
        base = ShortArray(SIZEOFBLOCK)
        sign = Array(SIZEOFBLOCK) { BooleanArray(SIZEOFBLOCK) }
        N = BigInteger("0")
        vectorCode = Vector()
    }


    fun FromBigIntToArray(): ByteArray {
        return N.toByteArray()
    }

    fun FromBigIntToVector(vector: ByteVector, base: ShortArray) {

        val code = N.toByteArray()
        //        assert (code.length<Short.MAX_VALUE);
        //        vector.append((short)code.length);
        var length = getLengthOfCode(base)
        while (length-- > code.size)
            vector.append(0.toByte())

        for (b in code) {
            vector.append(b)
        }


        //        System.out.print(",dL="+(code.length-length));
        //        assert code.length<=length:"cL:"+code.length+">l:"+length;
    }

    fun FromArrayToBigInt(code: ByteArray) {
        N = BigInteger(code)
    }

    fun FromVectorToBigInt(vector: ByteVector, base: ShortArray) {
        //        int len=vector.getNextShort();
        val len = getLengthOfCode(base)
        val code = ByteArray(len)
        for (i in 0 until len) {
            code[i] = vector.getNext()
        }
        N = BigInteger(code)
    }

    fun FromSignToArray(): ByteArray {
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

    fun FromSignToVector(vector: ByteVector) {
        for (i in 0 until SIZEOFBLOCK) {
            var res: Byte = 0
            for (j in 0 until SIZEOFBLOCK) {
                res = res shl 1
                if (sign[i][j]) {
                    res = res or 1
                }
            }
            vector.append(res)
        }
    }

    fun FromArrayToSing(s: ByteArray) {
        for (i in SIZEOFBLOCK - 1 downTo 0) {
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                sign[i][j] = s[i] and 1 == 1
                s[i] = s[i] shr 1
            }
        }
    }

    fun FromVectorToSign(vector: ByteVector) {
        for (i in 0 until SIZEOFBLOCK) {
            var s = vector.getNext()
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                sign[i][j] = s and 1 == 1
                s = s shr 1
            }
        }
    }

    fun FromBaseToArray(): ShortArray {
        val res = ShortArray(SIZEOFBLOCK)
        System.arraycopy(base, 0, res, 0, res.size)
        return res
    }

    fun FromArrayToBase(b: ShortArray) {
        System.arraycopy(b, 0, base, 0, b.size)
    }

    fun FromBaseToVector(vector: ByteVector) {
        //        if(!flag.isDC()){
        //            vector.append(base[i++]);
        //        }
        //        while (i<SIZEOFBLOCK){
        //            assert (base[i]<0xff):"base["+i+"]="+base[i];
        //            vector.append((byte)base[i++]);
        //        }
        vector.append(base[0])
        vector.append(base[1])
        vector.append(base[2])
        vector.append(base[3])

        vector.append(base[4])
        vector.append(base[5])
        vector.append(base[6])
        vector.append(base[7])
    }

    fun FromVectorToBase(vector: ByteVector) {
        //        if(!flag.isDC()){
        //            base[i++]=vector.getNextShort();
        //        }
        //        while (i<SIZEOFBLOCK) {
        //            base[i++]=(short)((vector.getNext())&0xff);
        //        }
        base[0] = vector.getNextShort()
        base[1] = vector.getNextShort()
        base[2] = vector.getNextShort()
        base[3] = vector.getNextShort()

        base[4] = vector.getNextShort()
        base[5] = vector.getNextShort()
        base[6] = vector.getNextShort()
        base[7] = vector.getNextShort()
    }

    fun FromDcToVector(vector: ByteVector) {
        vector.append(DC)
    }

    fun FromVectorToDc(vector: ByteVector) {
        DC = vector.getNextShort()
    }

    fun FromCodeToVector(vector: ByteVector) {
        val len = vectorCode.size
        assert(len < 0xf)
        vector.append(len.toByte())
        for (l in vectorCode) {
            vector.append(l)
        }
    }

    fun FromVectorToCode(vector: ByteVector) {
        val len = vector.getNext() and 0xFF
        for (i in 0 until len) {
            vectorCode.add(vector.getNextLong())
        }
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

    fun valueOf(s: String, flag: Flag): DataOPC {
        val offset = SIZEOFBLOCK
        var index = 0

        if (flag.isOneFile && !flag.isGlobalBase) {
            val a = ShortArray(offset)
            for (i in 0 until offset) {
                a[i] = s[index++].toShort()
            }
            FromArrayToBase(a)
        }

        val sign = ByteArray(offset)
        for (i in 0 until offset) {
            sign[i] = s[index++].toByte()
        }
        FromArrayToSing(sign)

        if (flag.isDC)
            DC = s[index++].toShort()

        if (!flag.isLongCode) {
            val length = s[index++].toInt()
            val code = ByteArray(length)
            for (i in 0 until length) {
                code[i] = s[index++].toByte()
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
        return this
    }

    fun toByteVector(vector: ByteVector, f: Flag): ByteVector {
        if (f.isDC)
            FromDcToVector(vector)

        if (!f.isGlobalBase && f.isOneFile)
            FromBaseToVector(vector)

        if (f.isLongCode)
            FromCodeToVector(vector)
        else
            FromBigIntToVector(vector, base)

        FromSignToVector(vector)

        return vector
    }

    fun valueOf(vector: ByteVector, f: Flag): DataOPC {
        if (f.isDC)
            FromVectorToDc(vector)

        if (!f.isGlobalBase && f.isOneFile)
            FromVectorToBase(vector)

        if (f.isLongCode)
            FromVectorToCode(vector)
        else
            FromVectorToBigInt(vector, base)

        FromVectorToSign(vector)

        return this
    }


    override fun equals(obj: Any?): Boolean {
        if (this === obj)
            return true
        if (obj!!.javaClass != DataOPC::class.java)
            return false

        val d = obj as DataOPC?
        if (d!!.DC != DC)
            return false
        for (i in 0 until SIZEOFBLOCK) {
            if (d.base[i] != base[i])
                return false
            for (j in 0 until SIZEOFBLOCK) {
                if (d.sign[i][j] != sign[i][j])
                    return false
            }
        }
        if (d.vectorCode.size != vectorCode.size)
            return false

        for (i in vectorCode.indices) {
            if (d.vectorCode[i] != vectorCode[i])
                return false
        }
        return true
    }

    fun getByteSize(flag: Flag): Long {
        val vector = ByteVector(10)
        toByteVector(vector, flag)
        return vector.size.toLong()
    }

    companion object {
        val SIZEOFBLOCK = 8

        //support utils
        private fun getLengthOfCode(base: ShortArray): Int {//TODO optimize this fun
            var bi = BigInteger("1")
            for (i in 0..7) {
                for (j in 0..7)
                    bi = bi.multiply(BigInteger.valueOf(base[i].toLong()))
            }
            //        if(bi.compareTo(N)<0)
            //            System.out.println("Alarm");
            return bi.toByteArray().size
        }
    }
}
