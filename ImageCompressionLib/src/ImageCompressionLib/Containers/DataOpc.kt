package ImageCompressionLib.Containers

import ImageCompressionLib.Constants.BITS_PER_BYTE
import java.math.BigInteger
import java.util.*

class DataOpc {
    var base: ShortArray
    var sign: Array<BooleanArray>
    var DC: Short
    var N: BigInteger
    var vectorCode: Vector<Long>

    constructor(parameters: Parameters){
        base = ShortArray(parameters.unitSize.height)
        sign = Array(parameters.unitSize.width) { BooleanArray(parameters.unitSize.height) }
        DC = 0
        N = BigInteger("0")
        vectorCode = Vector()
    }

    fun FromBigIntToVector(vector: ByteVector, base: ShortArray) {
        val code = N.toByteArray()
//        vector.append((short)code.length);
        var length = getLengthOfCode(base)
        while (length-- > code.size)
            vector.append(0.toByte())

        for (b in code) {
            vector.append(b)
        }
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


    fun toByteVector(vector: ByteVector, f: Flag): ByteVector {
        if (f.isChecked(Flag.Parameter.DC))
            FromDcToVector(vector)

        if (!f.isChecked(Flag.Parameter.GlobalBase) && f.isChecked(Flag.Parameter.OneFile))
            FromBaseToVector(vector)

        if (f.isChecked(Flag.Parameter.LongCode))
            FromCodeToVector(vector)
        else
            FromBigIntToVector(vector, base)

        if(f.isChecked(Flag.Parameter.DCT))
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

        if(f.isChecked(Flag.Parameter.DCT))
            FromVectorToSign(vector)

        return this
    }


    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other!!.javaClass != DataOpcOld::class.java)
            return false

        val d = other as DataOpcOld?
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
    fun copy(): DataOpcOld {
        val res= DataOpcOld()
        res.N= BigInteger(N.toByteArray())
        res.DC=DC
        for(i in 0..DataOpcOld.SIZEOFBLOCK -1) {
            res.base[i]=base[i]
            for(j in 0..DataOpcOld.SIZEOFBLOCK -1)
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
        fun valueOf(byteVector: ByteVector, flag: Flag): DataOpcOld {
            val dataOpc= DataOpcOld()
            dataOpc.setFrom(byteVector,flag)
            return dataOpc
        }

        @JvmStatic
        fun valueOf(s:String,flag: Flag): DataOpcOld {
            val dataOpc= DataOpcOld()
            dataOpc.setFrom(s,flag)
            return dataOpc
        }
    }
}