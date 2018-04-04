package ImageCompression.Utils.Objects

import ImageCompression.Constants.TypeQuantization
import ImageCompression.Containers.Flag
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import kotlin.test.assertFails

class DctConvertorTest {
    val flag=Flag()

    @Before
    fun init(){
        flag.setChecked(Flag.Parameter.OneFile,true)
        flag.setChecked(Flag.Parameter.DC,true)
        flag.setChecked(Flag.Parameter.OneFile,true)
    }
    @Test
    fun test1(){
        mainTotal(16,16,5)
    }
    @Test
    fun test2(){
        mainTotal(32,32,10)
    }
    @Test
    fun test3(){
        mainTotal(800,800,10)
    }
    @Test
    fun test4(){
        mainTotal(4,4,5)
    }
    fun mainTotal(w: Int,h: Int,range: Int) {
        val data=createData(w,h){i,j->((i+j)%255).toShort()}
        val cpy = data.copy()
        assertTrue(data.inRannge(cpy,0))

        val convertor=DctConvertor(data,DctConvertor.State.ORIGIN,TypeQuantization.luminosity,flag)

        val dct=convertor.matrixDct
        assertTrue(data.inRannge(dct,0))
        assertFails { (cpy.inRannge(dct,range))}

        val res=convertor.matrixOrigin
        assertTrue(data.inRannge(cpy,range))
        assertTrue(data.inRannge(res,0))

    }

    private fun createData(w: Int, h: Int,init:(i:Int,j:Int)->Short): Array<ShortArray> {
        return Array<ShortArray>(w){i->ShortArray(h){j->init(i,j)}}
    }

    fun Array<ShortArray>.copy():Array<ShortArray>{
        return createData(this.size,this[0].size){i, j -> this@copy[i][j]}
    }
    fun Array<ShortArray>.inRannge(a:Array<ShortArray>,range:Int):Boolean{
        if(size!=a.size)return false
        if(this[0].size!=a[0].size)return false

        for(i in 0..size-1){
            for(j in 0..this[0].size-1){
                var isEqual=false
                for(d in -range..range)
                    if(this[i][j]==(a[i][j]+d).toShort())
                        isEqual=true
                if(!isEqual) {
                    throw Exception("d[$i][$j](${this[i][j]})!=other[$i][$j](${a[i][j]}) in range=$range")
//                    return false
                }
            }
        }
        return true
    }
}