package ImageCompression.Containers

import ImageCompression.Utils.Objects.ByteVector
import ImageCompression.Utils.Objects.DataOPC
import ImageCompression.Utils.Objects.Flag
import java.awt.image.DataBuffer
import java.io.*
import java.math.BigInteger
import java.util.*

class BoxOfOpc {
    var a: Array<Array<DataOPC>>?=null
    var b: Array<Array<DataOPC>>?=null
    var c: Array<Array<DataOPC>>?=null

    constructor(){    }
    constructor(a:Array<Array<DataOPC>>,b:Array<Array<DataOPC>>,c:Array<Array<DataOPC>>){
        this.a=a
        this.b=b
        this.c=c
    }

    //TODO global base

    fun writeToVector(vector: ByteVector,flag: Flag){
        a?.appendVectorOne(vector,flag)
        b?.appendVectorOne(vector,flag)
        c?.appendVectorOne(vector,flag)
    }
    fun writeBaseToVector(vector: ByteVector,flag: Flag){
        a?.appendVectorTwo(vector,flag)
        b?.appendVectorTwo(vector,flag)
        c?.appendVectorTwo(vector,flag)
    }
    fun readFromVector(vector: ByteVector,flag: Flag){
        a=MatrixFromVectorOne(vector,flag)
        b=MatrixFromVectorOne(vector,flag)
        c=MatrixFromVectorOne(vector,flag)
    }
    fun readBaseFromVector(vector: ByteVector,flag: Flag){
        a?.BaseFromVectorTwo(vector,flag)
        b?.BaseFromVectorTwo(vector,flag)
        c?.BaseFromVectorTwo(vector,flag)
    }

    private fun Array<Array<DataOPC>>.appendVectorOne(vector: ByteVector,flag: Flag){
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
    private fun MatrixFromVectorOne(vector: ByteVector,flag: Flag):Array<Array<DataOPC>>{
        val w=vector.getNextShort()
        val h=vector.getNextShort()
        return Array(w.toInt(),{ x-> Array(h.toInt(),{y->DataOPC().valueOf (vector,flag)}) })
    }
    private fun Array<Array<DataOPC>>.appendVectorTwo(vector: ByteVector,flag: Flag){
        if(!flag.isOneFile){
            for(arr in this){
                for(dopc in arr){
                    dopc.FromBaseToVector(vector,flag)
                }
            }
        }
    }
    private fun Array<Array<DataOPC>>.BaseFromVectorTwo(vector: ByteVector,flag: Flag){
        if(!flag.isOneFile){
            for(arr in this){
                for(dopc in arr){
                    dopc.FromVectorToBase(vector,flag)
                }
            }
        }
    }

    fun Array<Array<DataOPC>>?.Equals(other:Any?):Boolean{
        if (this === other) return true
        val cl=other?.javaClass
        val jc=this?.javaClass
        if (this?.javaClass != other?.javaClass) return false

        val buf:Array<Array<DataOPC>> = other as Array<Array<DataOPC>>?:return false
        val buf1:Array<Array<DataOPC>> = this as Array<Array<DataOPC>>?:return false
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

    fun copy():BoxOfOpc{
        var box=BoxOfOpc()
        box.a=a?.copy()
        box.b=b?.copy()
        box.c=c?.copy()
        return box
    }
    fun Array<Array<DataOPC>>?.copy():Array<Array<DataOPC>>?{
        val buf:Array<Array<DataOPC>> = this?:return null
        val w=buf.size?:0
        val h=buf.get(0).size?:0
        val arr=Array(w,{x->Array(h,{y-> buf.get(x).get(y).copy()}) })
        return arr
    }
    fun DataOPC.copy():DataOPC{
        var res=DataOPC()
        res.N= BigInteger(N.toByteArray())
        res.DC=DC
        for(i in 0..DataOPC.SIZEOFBLOCK -1) {
            res.base[i]=base[i]
            for(j in 0..DataOPC.SIZEOFBLOCK -1)
                res.sign[i][j]=sign[i][j]
        }
        for (i in 0..Code.size-1)
            res.Code.addElement(Code[i])

        return res
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoxOfOpc
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