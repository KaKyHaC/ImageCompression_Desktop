package ImageCompression.Steganography.Utils

import ImageCompression.Steganography.Containers.UnitContainer
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class StegoConvertorDesktopTestBridge {
    val convertor=StegoConvertor.instance
    @Test
    fun test8() {
        stegoBridge(8, 8, true, { it -> it.toShort() })
        stegoBridge(8, 8, false, { it -> it.toShort() })
    }
    @Test
    fun test18() {
        stegoBridge(18, 18, true, { it -> it.toShort() })
        stegoBridge(18, 18, false, { it -> it.toShort() })
    }
    @Test
    fun test8Rand() {
        for(i in 0..100){
            stegoBridge(8, 8, true, { it -> Random().nextInt(255).toShort() })
            stegoBridge(8, 8, false, { it -> Random().nextInt(255).toShort() })
        }
    }
    @Test
    fun test2Rand() {
        for(i in 0..100){
            stegoBridge(2, 1, true, { it -> Random().nextInt(255).toShort() })
            stegoBridge(2, 1, false, { it -> Random().nextInt(255).toShort() })
        }
    }
    @Test
    fun test1Rand() {
        for(i in 0..100){
            stegoBridge(1, 1, true, { it -> Random().nextInt(255).toShort() })
            stegoBridge(1, 1, false, { it -> Random().nextInt(255).toShort() })
        }
    }
    @Test
    fun test18rand(){
        for(i in 0..100) {
            stegoBridge(18, 18, true, { it -> Random().nextInt(255).toShort() })
            stegoBridge(18, 18, false, { it -> Random().nextInt(255).toShort() })
        }
    }

    fun stegoBridge(w:Int,h:Int,message: Boolean,geter:(Int)->Short){
        val arr=Array<Array<Short>>(w){ShortArray(h){geter(it)}.toTypedArray()}
        val data= UnitContainer<Short>(arr, message)

        val mbar=convertor.direct(data,true)
        val bmpNoMul=convertor.reverce(mbar,w,h,false)
        val barEmpty=convertor.direct(bmpNoMul,false)
        val bmpRes=convertor.reverce(barEmpty,w,h,true)

        if(data.message!=bmpRes.message) {
            println(data)
            println()
            println(bmpRes)
        }

        assertEquals(data.message,bmpRes.message)
    }


    @Test
    fun testArr8(){
        val arrInt= arrayOf(
                arrayOf(196,122,232,50,177,170,4,81),
                arrayOf(30,105,156,169,29,233,79,164),
                arrayOf(162,39,129,146,0,148,53,99),
                arrayOf(137,94,235,127,126,44,232,122),
                arrayOf(89,201,238,154,31,161,204,70),
                arrayOf(245,43,97,119,71,32,81,193),
                arrayOf(28,253,43,120,250,152,64,122),
                arrayOf(189,195,8,70,75,189,213,148))
        testArr(arrInt,false)
    }
    @Test
    fun testArr2(){
        val arrInt= arrayOf(
                arrayOf(160),
                arrayOf(79))
        testArr(arrInt,false)
    }
    @Test
    fun testArr1(){
        val arrInt= arrayOf(
                arrayOf(124))
        testArr(arrInt,false)
    }
    fun testArr(array: Array<Array<Int>>,message: Boolean){

        val w=array.size
        val h=array[0].size

        val arr=Array<Array<Short>>(w){ShortArray(h){0}.toTypedArray()}
        for(i in 0..w-1){
            for(j in 0..h-1){
                arr[i][j]=array[i][j].toShort()
            }
        }
        val data= UnitContainer<Short>(arr, message)

        val mbar=convertor.direct(data,true)
        val bmpNoMul=convertor.reverce(mbar,w,h,false)
        val barEmpty=convertor.direct(bmpNoMul,false)
        val bmpRes=convertor.reverce(barEmpty,w,h,true)

        if(data.message!=bmpRes.message) {
            println(data)
            println()
            println(bmpRes)
        }

        assertEquals(data.message,bmpRes.message)
    }

}