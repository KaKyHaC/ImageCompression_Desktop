package ImageCompressionLib.Data.Type

import ImageCompressionLib.Data.Interfaces.ICopyable
import ImageCompressionLib.Data.Parameters
import ImageCompressionLib.Data.Primitives.Size
import java.math.BigInteger
import java.util.*
import kotlin.experimental.and

class DataOpc : ICopyable {
    var base: ShortArray
    var sign: Array<BooleanArray>
    var DC: Short
    var N: BigInteger
    var vectorCode: Vector<Long>
    private val size: Size
    constructor(parameters: Parameters){
        base = ShortArray(parameters.unitSize.height){1.toShort()}
        sign = Array(parameters.unitSize.width) { BooleanArray(parameters.unitSize.height) }
        DC = 0
        N = BigInteger("0")
        vectorCode = Vector()
        size=parameters.unitSize
    }
    constructor(unitSize: Size){
        base = ShortArray(unitSize.height){1.toShort()}
        sign = Array(unitSize.width) { BooleanArray(unitSize.height) }
        DC = 0
        N = BigInteger("0")
        vectorCode = Vector()
        size=unitSize
    }
    constructor(DC: Short, N: BigInteger, vectorCode: Vector<Long>,base: ShortArray, sign: Array<BooleanArray>) {
        this.base = base
        this.sign = sign
        this.DC = DC
        this.N = N
        this.vectorCode = vectorCode
        size= Size(sign.size, sign[0].size)
    }


    fun FromBigIntToVector(vector: ByteVector, length:Int) {
        val code = N.toByteArray()
//        vector.append((short)code.length);
//        var length = getLengthOfCode(base)
        var len=length
        if(len==code.size)
            println("len==code")
        else if(len<code.size)
            throw Exception("len < code")
        else
            println("count ${count++}")
        while (len-- > code.size)
            vector.append(0.toByte())

        for (b in code) {
            vector.append(b)
        }
    }
    fun FromVectorToBigInt(vector: ByteVector, length: Int) {
//        int len=vector.getNextShort();
//        val len = getLengthOfCode(base)
        val len =length
        val code = ByteArray(len)
        for (i in 0 until len) {
            code[i] = vector.getNext()
        }
        N = BigInteger(code)
    }


    fun FromSignToVector(vector: ByteVector) {
        for (i in 0 until size.width) {
            for (j in 0 until size.height) {
                vector.append(sign[i][j])
            }
        }
    }
    fun FromVectorToSign(vector: ByteVector) {
        for (i in 0 until size.width) {
            for (j in 0 until size.height) {
                sign[i][j] = vector.getNextBoolean()
            }
        }
    }


    fun FromBaseToVector(vector: ByteVector, flag: Flag) {
        if(!flag.isChecked(Flag.Parameter.ByteBase)) {//if (!DC)
            for(i in 0 until size.height)
                vector.append(base[i])
        }else{
            for(i in 0 until size.height) {
                vector.append(base[i].toByte())
//                if(base[i]>=0xff)
//                    throw Exception("base[$i]=${base[i]}")
            }
        }
    }
    fun FromVectorToBase(vector: ByteVector, flag: Flag) {
        if(!flag.isChecked(Flag.Parameter.ByteBase)) {//if (!DC)
            for(i in 0 until size.height)
                base[i]=vector.getNextShort();
        }else{
            for(i in 0 until size.height) {
                base[i] = vector.getNext().toShort() and 0xff;
                if(base[i]<=0)
                    base[i]=256
            }
        }
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

    fun toByteVector(vector: ByteVector, parameters: Parameters): ByteVector {
        val f=parameters.flag
        if (f.isChecked(Flag.Parameter.DC))
            FromDcToVector(vector)

        if (!f.isChecked(Flag.Parameter.GlobalBase) && f.isChecked(Flag.Parameter.OneFile))
            FromBaseToVector(vector,f)

        if (f.isChecked(Flag.Parameter.LongCode))
            FromCodeToVector(vector)
        else
            FromBigIntToVector(vector, getLengthOfCode(base, parameters.unitSize))

        if(f.isChecked(Flag.Parameter.DCT))
            FromSignToVector(vector)

        return vector
    }
    fun setFrom(vector: ByteVector, parameters: Parameters): DataOpc {
        val f = parameters.flag
        if (f.isChecked(Flag.Parameter.DC))
            FromVectorToDc(vector)

        if (!f.isChecked(Flag.Parameter.GlobalBase) && f.isChecked(Flag.Parameter.OneFile))
            FromVectorToBase(vector,f)

        if (f.isChecked(Flag.Parameter.LongCode))
            FromVectorToCode(vector)
        else
            FromVectorToBigInt(vector, getLengthOfCode(base, parameters.unitSize))

        if(f.isChecked(Flag.Parameter.DCT))
            FromVectorToSign(vector)

        return this
    }


    fun assertEquals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other!!.javaClass != DataOpc::class.java)
            throw Exception("other class ${other!!.javaClass}")//return false

        val d = other as DataOpc?
        if (d!!.DC != DC)
            throw Exception("DC: ${DC}!=${d.DC}")//return false
        for (i in 0 until base.size) {
            if (d.base[i] != base[i])
                throw Exception("base[$i]: ${base[i]}!=${d.base[i]}")//return false
            for (j in 0 until sign[0].size) {
                if (d.sign[i][j] != sign[i][j])
                    throw Exception("sign[$i][$j]: ${sign[i][j]}!=${d.sign[i][j]}")//return false
            }
        }
        if (d.vectorCode.size != vectorCode.size)
            throw Exception("vectorCode.size not equals")//return false

        for (i in vectorCode.indices) {
            if (d.vectorCode[i] != vectorCode[i])
                throw Exception("code[$i]: ${vectorCode[i]}!=${d.vectorCode[i]}")//return false
        }

        if(N.compareTo(d.N)!=0)
            throw Exception("BI: $N!=${d.N}")

        return true
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        val d=other as DataOpc

        if (d!!.DC != DC)
            return false
        for (i in 0 until base.size) {
            if (d.base[i] != base[i])
                return false
            for (j in 0 until sign.size) {
                if (d.sign[j][i] != sign[j][i])
                    return false
            }
        }
        if (d.vectorCode.size != vectorCode.size)
            return false

        for (i in vectorCode.indices) {
            if (d.vectorCode[i] != vectorCode[i])
                return false
        }
        if(N.compareTo(d.N)!=0)
            throw Exception("BI: $N!=${d.N}")


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
    override fun copy(): DataOpc {
        val rN= BigInteger(N.toByteArray())
        val rDC=DC
        val rbase=ShortArray(base.size){base[it]}
        val rsign=Array(sign.size){i->BooleanArray(sign[0].size){j->sign[i][j]}}
        val rcode=Vector<Long>()
        for(el in this.vectorCode)
            rcode.addElement(el)

        return DataOpc(rDC, rN, rcode, rbase, rsign)
    }

    fun getByteSize(parameters: Parameters): Int {
        val vector = ByteVector(10)
        toByteVector(vector, parameters)
        return vector.size
    }

    override fun toString(): String {
        return "DataOpc(base=${Arrays.toString(base)}, DC=$DC, N=$N, sign=${Arrays.toString(sign)}, vectorCode=$vectorCode)"
    }


    companion object {
        @JvmStatic var count = 0

        //support utils
        fun getLengthOfCode(base: ShortArray,unitSize: Size): Int {//TODO optimize this fun
            var bi = BigInteger("1")
            for (i in 0 until unitSize.width) {
                for (j in 0 until unitSize.height)
                    bi = bi.multiply(BigInteger.valueOf(base[j].toLong()))
            }
            return bi.toByteArray().size
        }

        @JvmStatic
        fun valueOf(byteVector: ByteVector, parameters: Parameters): DataOpc {
            val dataOpc = DataOpc(parameters)
            dataOpc.setFrom(byteVector,parameters)
            return dataOpc
        }
    }
}