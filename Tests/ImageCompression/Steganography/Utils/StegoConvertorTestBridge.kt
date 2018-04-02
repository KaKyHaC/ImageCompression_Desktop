package ImageCompression.Steganography.Utils

import ImageCompression.Steganography.Containers.UnitContainer
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class StegoConvertorTestBridge{
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

        assertEquals(data.message,bmpRes.message)
    }

}