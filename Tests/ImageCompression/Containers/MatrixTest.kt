package ImageCompression.Containers

import ImageCompression.Constants.State
import ImageCompression.Utils.Objects.Flag
import org.junit.Assert.*
import org.junit.Test
import java.util.*
import kotlin.test.assertFails

class MatrixTest{
    companion object {
        @JvmStatic fun getRandomMatrix(w:Int,h:Int):Matrix{
            val rand=Random()
            var m=Matrix(w,h, Flag("0"),State.RGB)
            for(i in 0..w-1){
                for(j in 0..h-1){
                    m.a[i][j]=rand.nextInt(255).toShort()
                    m.b[i][j]=rand.nextInt(255).toShort()
                    m.c[i][j]=rand.nextInt(255).toShort()
                }
            }
            return m
        }
    }
    @Test
    fun TestEquals(){
        val m= getRandomMatrix(34,543)
        assertEquals(m,m)
    }
    @Test
    fun TestAllUtils(){
        val m= getRandomMatrix(342,12)
        val m1=m.copy()

        assertEquals(m,m1)
        m1.a[0][0]++
        assertFails { assertEquals(m,m1) }
        m.assertMatrixInRange(m1,1)
    }
}