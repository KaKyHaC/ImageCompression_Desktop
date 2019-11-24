package ImageCompressionLib.Utils.Convertors

import ImageCompressionLib.Constants.SIZEOFBLOCK
import ImageCompressionLib.Data.Enumerations.TypeQuantization
import ImageCompressionLib.Data.Matrix.ShortMatrix
import ImageCompressionLib.Data.Parameters
import ImageCompressionLib.Data.Type.Flag
import ImageCompressionLib.Data.Primitives.Size
import ImageCompressionLib.Utils.Functions.Dct.DctUniversalAlgorithm
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.util.*
import kotlin.test.assertFails

class DctConvertorDesktopTest {
    val flag= Flag()

    @Before
    fun init(){
        flag.setChecked(Flag.Parameter.OneFile,true)
        flag.setChecked(Flag.Parameter.DC,true)
        flag.setChecked(Flag.Parameter.OneFile,true)
    }
    @Test
    fun test16x16(){
        mainTotal(16,16,5, Size(8, 8))
    }
    @Test
    fun test32x32(){
        mainTotal(32,32,10, Size(8, 8))
    }
    @Test
    fun test800x800(){
        mainTotal(800,800,10, Size(10, 10))
    }
    @Test
    fun test4x4(){
        println("work only for x8 size")
        mainTotal(4,4,5, Size(4, 4))
    }
    @Test
    fun test7x6(){
        println("work only for square unitSize")
        mainTotal(7,6,5, Size(7, 6))
    }
    fun mainTotal(w: Int,h: Int,range: Int,unitSize: Size) {
        val data=ShortMatrix(w,h){ i, j->((Math.abs(Random().nextInt(255)))).toShort()}
        val cpy = data.copy()
        assertTrue(data.assertInRange(cpy,0))

        val convertor=DctConvertor(data,DctConvertor.State.ORIGIN,
            TypeQuantization.Luminosity
                , Parameters.createParametresForTest(data.size,flag = flag,unitSize = unitSize)
                , DctUniversalAlgorithm(unitSize))

        val dct=convertor.getMatrixDct()
        assertTrue(data.assertInRange(ShortMatrix.valueOf(dct),0))
        assertFails { (cpy.assertInRange(ShortMatrix.valueOf(dct),range))}

        val res=convertor.getMatrixOrigin()
        assertTrue(data.assertInRange(cpy,range))
        assertTrue(data.assertInRange(ShortMatrix.valueOf(res),0))

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
