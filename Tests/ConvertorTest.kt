import ImageCompression.Containers.Matrix
import ImageCompression.Containers.State
import ImageCompression.Objects.Flag
import ImageCompression.Objects.MyBufferedImage
import ImageCompression.Objects.MyBufferedImageTest
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.test.assertTrue

class ConvertorTest {
    val pathToBmp:String="/files/desk.bmp"
    val pathToBmpRes:String="/files/desktest.bmp"
    @Before
    fun setUp() {
    }
    @Test
    fun TestReadWrite(){
        val bmp = ImageIO.read(File(pathToBmp))
        File(pathToBmp).createNewFile()
        ImageIO.write(bmp,"bmp",File(pathToBmpRes))
    }
    @Test
    fun TestMyBufferedImage(){
        val bmp = ImageIO.read(File(pathToBmp))
        val flag=Flag("0")
        flag.isOneFile=true
        val bi= MyBufferedImage(bmp,flag)
    }
    @Test
    fun TestMyBufferedImageMatrix(){
        val size=200
        var matrix:Matrix= Matrix(size,size, Flag("0"))
        matrix.state=State.RGB
        val rand=Random()
        forEach(size,size,{x, y ->
            matrix.a[x][y]=rand.nextInt(255).toShort()
            matrix.c[x][y]=rand.nextInt(255).toShort()
            matrix.b[x][y]=rand.nextInt(255).toShort()
        })

        val mi=MyBufferedImage(matrix)
        val bi=mi.bufferedImage
        val mi1=MyBufferedImage(bi,matrix.f)
        var matrix1=mi1.rgbMatrix

        AssertMatrixInRange(matrix,matrix1,2)

        matrix=mi.yCbCrMatrix
        matrix1=mi1.yCbCrMatrix

        AssertMatrixInRange(matrix,matrix1,2)
    }


    fun AssertMatrixInRange(m:Matrix,m1:Matrix,delta:Int){
        assertEquals(m.Width,m1.Width)
        assertEquals(m.Height,m1.Height)

        AssertArrayArrayInRange(m.a,m1.a,delta)
        AssertArrayArrayInRange(m.b,m1.b,delta)
        AssertArrayArrayInRange(m.c,m1.c,delta)
    }
    fun AssertArrayArrayInRange(a:Array<ShortArray>,a1:Array<ShortArray>,delta:Int){
        assertEquals(a.size,a1.size)
        assertEquals(a[0].size,a1[0].size)

        forEach(a.size,a[0].size,{x,y->
            var isEqual=false
            for(i in -delta..delta)
            {
                if(a[x][y]==(a1[x][y]+i).toShort() )
                    isEqual=true
            }
            assertTrue("in [$x][$y] val ${a[x][y]}!=${a1[x][y]}"
                    ,isEqual)
        })
    }
    fun forEach(w:Int,h:Int,let:(x:Int,y:Int)->Unit){
        for(i in 0..w-1){
            for(j in 0..h-1){
                let(i,j)
            }
        }
    }

}