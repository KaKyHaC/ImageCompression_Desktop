package ImageCompression.Containers

import java.math.BigInteger
import java.util.*

/**
 * Created by Димка on 27.09.2016.
 */
class DataOpc {
    var base: ShortArray
    var sign: Array<BooleanArray>
    var DC: Short = 0
    var N: BigInteger
    var vectorCode: Vector<Long>

    init {
        base = ShortArray(SIZEOFBLOCK)
        sign = Array(SIZEOFBLOCK) { BooleanArray(SIZEOFBLOCK) }
        N = BigInteger("0")
        vectorCode = Vector()
    }

    var Dc: ByteArray
        get() {
            val res = ByteArray(2)
            res[0] = (DC.toInt() shr SIZEOFBLOCK).toByte()
            res[1] = DC.toByte()
            return res
        }
        set(bytes) {
            this.DC = bytes[0].toShort()
            DC = (DC.toInt() shl SIZEOFBLOCK).toShort()
            DC = (DC.toInt() or bytes[1].toInt()).toShort()
        }

    fun FromBigIntToArray(): ByteArray {
        return N.toByteArray()
    }
    fun FromArrayToBigInt(code: ByteArray) {
        N = BigInteger(code)
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
                res[i] = (res[i].toInt() shl 1).toByte()
                if (sign[i][j]) {
                    res[i] = (res[i].toInt() or 1).toByte()
                }
            }
        }
        return res
    }
    fun FromArrayToSing(s: ByteArray) {
        for (i in SIZEOFBLOCK - 1 downTo 0) {
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                sign[i][j] = (s[i].toInt() and 1 == 1)
                s[i] = (s[i].toInt() shr 1).toByte()
            }
        }
    }

    fun FromSignToVector(vector: ByteVector) {
        for (i in 0 until SIZEOFBLOCK) {
            var res: Byte = 0
            for (j in 0 until SIZEOFBLOCK) {
                res = (res.toInt() shl 1).toByte()
                if (sign[i][j]) {
                    res = (res.toInt() or 1).toByte()
                }
            }
            vector.append(res)
        }
    }
    fun FromVectorToSign(vector: ByteVector) {
        for (i in 0 until SIZEOFBLOCK) {
            var s = vector.getNext()
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                sign[i][j] = (s.toInt() and 1 == 1)
                s = (s.toInt() shr 1).toByte()
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
//        assert(len < 0xf)
        vector.append(len.toByte())
        for (l in vectorCode) {
            vector.append(l)
        }
    }
    fun FromVectorToCode(vector: ByteVector) {
        val len = vector.getNext().toInt() and 0xFF
        for (i in 0 until len) {
            vectorCode.add(vector.getNextLong())
        }
    }

    fun toString(flag: Flag): String {
//        val offset = SIZEOFBLOCK / 2
        val sb = StringBuilder()

        if (flag.isChecked(Flag.Parameter.OneFile) && !flag.isChecked(Flag.Parameter.GlobalBase))
            for (b in FromBaseToArray())
                sb.append(b.toChar())

        val sign = FromSignToArray()
        for (i in sign.indices) {
            sb.append(sign[i].toChar())
        }

        if (flag.isChecked(Flag.Parameter.DC))
            sb.append(DC.toChar())

        if (!flag.isChecked(Flag.Parameter.LongCode)) {
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
    fun setFrom(s: String, flag: Flag): DataOpc {
        val offset = SIZEOFBLOCK
        var index = 0

        if (flag.isChecked(Flag.Parameter.OneFile)&& !flag.isChecked(Flag.Parameter.GlobalBase)) {
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

        if (flag.isChecked(Flag.Parameter.DC))
            DC = s[index++].toShort()

        if (!flag.isChecked(Flag.Parameter.LongCode)) {
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
        if (f.isChecked(Flag.Parameter.DC))
            FromDcToVector(vector)

        if (!f.isChecked(Flag.Parameter.GlobalBase) && f.isChecked(Flag.Parameter.OneFile))
            FromBaseToVector(vector)

        if (f.isChecked(Flag.Parameter.LongCode))
            FromCodeToVector(vector)
        else
            FromBigIntToVector(vector, base)

        FromSignToVector(vector)

        return vector
    }
    fun setFrom(vector: ByteVector, f: Flag): DataOpc {
        if (f.isChecked(Flag.Parameter.DC))
            FromVectorToDc(vector)

        if (!f.isChecked(Flag.Parameter.GlobalBase) && f.isChecked(Flag.Parameter.OneFile))
            FromVectorToBase(vector)

        if (f.isChecked(Flag.Parameter.LongCode))
            FromVectorToCode(vector)
        else
            FromVectorToBigInt(vector, base)

        FromVectorToSign(vector)

        return this
    }


    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other!!.javaClass != DataOpc::class.java)
            return false

        val d = other as DataOpc?
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
    override fun hashCode(): Int {
        var result = Arrays.hashCode(base)
        result = 31 * result + Arrays.hashCode(sign)
        result = 31 * result + DC
        result = 31 * result + N.hashCode()
        result = 31 * result + vectorCode.hashCode()
        return result
    }
    fun copy():DataOpc{
        val res=DataOpc()
        res.N= BigInteger(N.toByteArray())
        res.DC=DC
        for(i in 0..DataOpc.SIZEOFBLOCK -1) {
            res.base[i]=base[i]
            for(j in 0..DataOpc.SIZEOFBLOCK -1)
                res.sign[i][j]=sign[i][j]
        }
        for (i in 0..vectorCode.size-1)
            res.vectorCode.addElement(vectorCode[i])

        return res
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
            for (i in 0 until SIZEOFBLOCK) {
                for (j in 0 until SIZEOFBLOCK)
                    bi = bi.multiply(BigInteger.valueOf(base[i].toLong()))
            }
            //        if(bi.compareTo(N)<0)
            //            System.out.println("Alarm");
            return bi.toByteArray().size
        }

        @JvmStatic
        fun valueOf(byteVector: ByteVector, flag: Flag): DataOpc {
            val dataOpc=DataOpc()
            dataOpc.setFrom(byteVector,flag)
            return dataOpc
        }

        @JvmStatic
        fun valueOf(s:String,flag: Flag): DataOpc {
            val dataOpc=DataOpc()
            dataOpc.setFrom(s,flag)
            return dataOpc
        }
    }
}
