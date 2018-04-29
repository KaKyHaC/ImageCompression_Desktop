package ImageCompressionLib.Containers

import ImageCompressionLib.Constants.SIZEOFBLOCK
import java.util.*

class TripleDataOpcMatrix {
    var a: Array<Array<DataOpcOld>>?=null
    var b: Array<Array<DataOpcOld>>?=null
    var c: Array<Array<DataOpcOld>>?=null
    //TODO add flag

    constructor(){    }
    constructor(a:Array<Array<DataOpcOld>>, b:Array<Array<DataOpcOld>>, c:Array<Array<DataOpcOld>>){
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
        if(a==null) a=createMatrixFromVectorOne(vector, flag)
        else a!!.readDataFromVector(vector, flag)

        if(b==null) b=createMatrixFromVectorOne(vector, flag)
        else b!!.readDataFromVector(vector, flag)

        if(c==null) c=createMatrixFromVectorOne(vector, flag)
        else c!!.readDataFromVector(vector, flag)
//        a?.readDataFromVector(vector, flag)?:{a=createMatrixFromVectorOne(vector,flag)}
//        b= createMatrixFromVectorOne(vector,flag)
//        c= createMatrixFromVectorOne(vector,flag)
    }
    fun readBaseFromVector(vector: ByteVector, flag: Flag){
        if(a==null) a=createMatrixFromVectorBase(vector, flag)
        else a!!.readBaseFromVector(vector, flag)

        if(b==null) b=createMatrixFromVectorBase(vector, flag)
        else b!!.readBaseFromVector(vector, flag)

        if(c==null) c=createMatrixFromVectorBase(vector, flag)
        else c!!.readBaseFromVector(vector, flag)
//        a!!.readBaseFromVector(vector,flag)
//        b!!.readBaseFromVector(vector,flag)
//        c!!.readBaseFromVector(vector,flag)
    }

    private fun Array<Array<DataOpcOld>>.appendVectorOne(vector: ByteVector, flag: Flag){
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
    private fun createMatrixFromVectorOne(vector: ByteVector, flag: Flag):Array<Array<DataOpcOld>>{
        val w=vector.getNextShort()
        val h=vector.getNextShort()
        return Array(w.toInt(),{ x-> Array(h.toInt(),{y-> DataOpcOld.valueOf (vector,flag)}) })
    }
    private fun Array<Array<DataOpcOld>>.appendBaseToVector(vector: ByteVector, flag: Flag, baseW:Int, baseH:Int){
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
    private fun Array<Array<DataOpcOld>>.readBaseFromVector(vector: ByteVector, flag: Flag){
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

    private fun createMatrixFromVectorBase(vector: ByteVector,flag: Flag):Array<Array<DataOpcOld>>{
        val w=vector.getNextShort()
        val h=vector.getNextShort()
        val stepW:Int = if(flag.isChecked(Flag.Parameter.GlobalBase))vector.getNextShort().toInt() else 1
        val stepH:Int = if(flag.isChecked(Flag.Parameter.GlobalBase))vector.getNextShort().toInt() else 1


        val res= Array(w.toInt(),{ x-> Array(h.toInt()){y-> DataOpcOld() }})
        var base:ShortArray=kotlin.ShortArray(SIZEOFBLOCK)
        for(i in 0 until w){
            for(j in 0 until h){
                if(i%stepW==0&&j%stepH==0)
                    base=ShortArray(SIZEOFBLOCK,{ x->vector.getNextShort()})
                res[i][j].FromArrayToBase(base)
            }
        }
        return res
    }
    private fun Array<Array<DataOpcOld>>.readDataFromVector(vector: ByteVector, flag: Flag){
        val w=vector.getNextShort()
        val h=vector.getNextShort()
        for (s in this){
            for(d in s){
                d.setFrom(vector,flag)
            }
        }
    }


    fun Array<Array<DataOpcOld>>?.Equals(other:Any?):Boolean{
        if (this === other) return true
        val cl=other?.javaClass
        val jc=this?.javaClass
        if (this?.javaClass != other?.javaClass) return false

        val buf:Array<Array<DataOpcOld>> = other as Array<Array<DataOpcOld>>?:return false
        val buf1:Array<Array<DataOpcOld>> = this as Array<Array<DataOpcOld>>?:return false
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
    fun Array<Array<DataOpcOld>>?.copy():Array<Array<DataOpcOld>>?{
        val buf:Array<Array<DataOpcOld>> = this?:return null
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