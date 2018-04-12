package ImageCompression.Containers

import ImageCompression.Constants.SIZEOFBLOCK
import java.math.BigInteger
import java.util.*

class TripleDataOpcMatrix {
    var a: Array<Array<DataOpc>>?=null
    var b: Array<Array<DataOpc>>?=null
    var c: Array<Array<DataOpc>>?=null
    //TODO add flag

    constructor(){    }
    constructor(a:Array<Array<DataOpc>>,b:Array<Array<DataOpc>>,c:Array<Array<DataOpc>>){
        this.a=a
        this.b=b
        this.c=c
    }

    //TODO global base

    fun writeToVector(vector: ByteVector, flag: Flag){
        a!!.appendVectorOne(vector,flag)
        b!!.appendVectorOne(vector,flag)
        c!!.appendVectorOne(vector,flag)
    }
    fun writeBaseToVector(vector: ByteVector, flag: Flag, stepW:Int, stepH:Int){
        a!!.appendBaseToVector(vector,flag,stepW,stepH)
        b!!.appendBaseToVector(vector,flag,stepW,stepH)
        c!!.appendBaseToVector(vector,flag,stepW,stepH)
    }
    fun readFromVector(vector: ByteVector, flag: Flag){
        a=MatrixFromVectorOne(vector,flag)
        b=MatrixFromVectorOne(vector,flag)
        c=MatrixFromVectorOne(vector,flag)
    }
    fun readBaseFromVector(vector: ByteVector, flag: Flag){
        a!!.readBaseFromVector(vector,flag)
        b!!.readBaseFromVector(vector,flag)
        c!!.readBaseFromVector(vector,flag)
    }

    private fun Array<Array<DataOpc>>.appendVectorOne(vector: ByteVector, flag: Flag){
        val w=this.size
        val h=this[0].size
        vector.append(w.toShort())
        vector.append(h.toShort())
        for(arr in this){
            for(dopc in arr){
                dopc.toByteVector(vector,flag)
            }
        }
    }
    private fun MatrixFromVectorOne(vector: ByteVector, flag: Flag):Array<Array<DataOpc>>{
        val w=vector.getNextShort()
        val h=vector.getNextShort()
        return Array(w.toInt(),{ x-> Array(h.toInt(),{y->DataOpc.valueOf (vector,flag)}) })
    }
    private fun Array<Array<DataOpc>>.appendBaseToVector(vector: ByteVector, flag: Flag, baseW:Int, baseH:Int){
        val w=this.size
        val h=this[0].size
        val stepW:Int = if(flag.isChecked(Flag.Parameter.GlobalBase))baseW else 1
        val stepH:Int = if(flag.isChecked(Flag.Parameter.GlobalBase))baseH else 1

        vector.append(w.toShort())
        vector.append(h.toShort())

        if(flag.isChecked(Flag.Parameter.GlobalBase)) {
            vector.append(stepW.toShort())
            vector.append(stepH.toShort())
        }

        for(i in 0 until w step stepW){
            for(j in 0 until h step stepH){
                for(base in this[i][j].FromBaseToArray()){
                    vector.append(base)
                }
            }
        }

    }
    private fun Array<Array<DataOpc>>.readBaseFromVector(vector: ByteVector, flag: Flag){
        val w=vector.getNextShort()
        val h=vector.getNextShort()
        val stepW:Int = if(flag.isChecked(Flag.Parameter.GlobalBase))vector.getNextShort().toInt() else 1
        val stepH:Int = if(flag.isChecked(Flag.Parameter.GlobalBase))vector.getNextShort().toInt() else 1

        var base:ShortArray=kotlin.ShortArray(SIZEOFBLOCK)
        for(i in 0 until w){
            for(j in 0 until h){
                if(i%stepW==0&&j%stepH==0)
                    base=ShortArray(SIZEOFBLOCK,{ x->vector.getNextShort()})
                this[i][j].FromArrayToBase(base)
            }
        }
    }


    fun Array<Array<DataOpc>>?.Equals(other:Any?):Boolean{
        if (this === other) return true
        val cl=other?.javaClass
        val jc=this?.javaClass
        if (this?.javaClass != other?.javaClass) return false

        val buf:Array<Array<DataOpc>> = other as Array<Array<DataOpc>>?:return false
        val buf1:Array<Array<DataOpc>> = this as Array<Array<DataOpc>>?:return false
        if(buf1.size!= buf.size)
            return false
        if(buf1[0].size!= buf.get(0).size)
            return false

        val w=buf1.size
        val h=buf1[0].size
        for(i in 0..w-1){
            for(j in 0..h-1){
                if(buf1[i][j]!=buf[i][j])
                    return false
            }
        }
        return true
    }

    fun copy(): TripleDataOpcMatrix {
        val box= TripleDataOpcMatrix()
        box.a=a?.copy()
        box.b=b?.copy()
        box.c=c?.copy()
        return box
    }
    fun Array<Array<DataOpc>>?.copy():Array<Array<DataOpc>>?{
        val buf:Array<Array<DataOpc>> = this?:return null
        val w=buf.size?:0
        val h=buf.get(0).size?:0
        val arr=Array(w,{x->Array(h,{y-> buf.get(x).get(y).copy()}) })
        return arr
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TripleDataOpcMatrix
        if(!(this.a?.Equals(other.a)?:true))
            return false
        if(!(this.b?.Equals(other.b)?:true))
            return false
        if(!(this.c?.Equals(other.c)?:true))
            return false

        return true
    }
    override fun hashCode(): Int {
        var result = a?.let { Arrays.hashCode(it) } ?: 0
        result = 31 * result + (b?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (c?.let { Arrays.hashCode(it) } ?: 0)
        return result
    }
}