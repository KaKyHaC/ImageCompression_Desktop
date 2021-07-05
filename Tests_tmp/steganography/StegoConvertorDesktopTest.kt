package steganography

import steganography.containers.UnitContainer
import steganography.utils.StegoConvertor
import org.junit.Test

import org.junit.Assert.*

class StegoConvertorDesktopTest {
    val convertor= StegoConvertor.instance

    @Test
    fun testTrue1() {
        mainTest(4,4,1)
        mainTest(8,8,1)
    }
    @Test
    fun testFalse1() {
        mainTest(4,4,1,false)
        mainTest(8,8,1,false)
    }
    @Test
    fun testLine1(){
        mainTest(4,1,1,false)
        mainTest(4,1,1)
    }
    @Test
    fun testColumn1(){
        mainTest(1,4,1)
        mainTest(1,4,1,false)
    }
    @Test
    fun testEqual() {
        println("not work")
        mainTest(4,4,0)
        mainTest(8,8,0)
    }
    @Test
    fun testZero() {
        println("not work")
        mainTest(4,4,1){0}
        mainTest(8,8,1,false){0}
    }
    @Test
    fun testWithoutMultiTWO() {
        val arr=Array<Array<Short>>(8){ShortArray(8){(it+1).toShort()}.toTypedArray()}
        val data= UnitContainer<Short>(arr, true)
        val convertor= StegoConvertor.instance
        val comp=convertor.direct(data,true)
        val res=convertor.reverce(comp,8,8,false)

        assertTrue(data.getMatrix().inRange(res.getMatrix(),1))
    }
    fun mainTest(width:Int,height:Int,range: Int,message:Boolean=true,init:(i:Int)->Short={(it+1).toShort()}){
        val arr=Array<Array<Short>>(width){ShortArray(height){init(it)}.toTypedArray()}
        val data= UnitContainer<Short>(arr, message)
        val convertor= StegoConvertor.instance
        val comp=convertor.direct(data,true)
        val res=convertor.reverce(comp,width, height,true)

        assertTrue(data.inRange(res,range))
    }


    fun Array<Array<Short>>.inRange(other:Array<Array<Short>>,range: Int):Boolean{
        for((i,e) in this.withIndex()){
            for((j) in e.withIndex()){
                var isInRange=false
                for(r in -range..range) {
                    if((this[i][j].toInt() + r).toShort() ==other[i][j])isInRange=true
                }
                if(!isInRange)throw Exception("data[$i][$j]${this[i][j]}!=${other[i][j]}")
            }
        }
        return true
    }
    fun UnitContainer<Short>.inRange(other: UnitContainer<Short>, range:Int):Boolean{
        if(message!=other.message)throw Exception("message: $message!=${other.message}")
        return  this.getMatrix().inRange(other.getMatrix(),range)
    }

}