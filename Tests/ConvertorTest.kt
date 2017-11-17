import ImageCompression.Containers.Matrix
import ImageCompression.Containers.State
import ImageCompression.Objects.BoxOfDUM
import ImageCompression.Utils.Objects.Flag
import ImageCompression.Objects.MyBufferedImage
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

    val size=200
    var matrix:Matrix= Matrix(size,size, Flag("0"))

    @Before
    fun setUp() {
        matrix.state=State.RGB
        val rand=Random()
        forEach(size,size,{x, y ->
            matrix.a[x][y]=rand.nextInt(255).toShort()
            matrix.c[x][y]=rand.nextInt(255).toShort()
            matrix.b[x][y]=rand.nextInt(255).toShort()
        })

    }
    @Test
    fun TestReadWrite(){
        val bmp = ImageIO.read(File(pathToBmp))
        File(pathToBmp).createNewFile()
        ImageIO.write(bmp,"bmp",File(pathToBmpRes))
    }
    @Test
    fun TestMyBufferedImageMatrix(){
        val mi=MyBufferedImage(matrix)
        val bi=mi.bufferedImage
        val mi1=MyBufferedImage(bi,matrix.f)


        var matrix1=mi1.rgbMatrix
        matrix=mi.rgbMatrix
        AssertMatrixInRange(matrix,matrix1,2)

        matrix=mi.yCbCrMatrix
        matrix1=mi1.yCbCrMatrix
        AssertMatrixInRange(matrix,matrix1,2)

        matrix1=mi1.rgbMatrix
        matrix=mi.yCbCrMatrix
        AssertMatrixInRange(matrix1,matrix,2,false)
    }
    @Test
    fun TestBoxOfDUM(){
        matrix=MyBufferedImage(matrix).yCbCrMatrix
        val cpy=matrix.copy()
        val bo=BoxOfDUM(matrix)

        bo.dataProcessingInThreads()
        AssertMatrixInRange(matrix,cpy,3,false)

        bo.dataProcessingInThreads()
        AssertMatrixInRange(matrix,cpy,3)
    }
    @Test
    fun TestTimeBoxofDum(){
        matrix=MyBufferedImage(matrix).yCbCrMatrix
        val bo=BoxOfDUM(matrix)

        var t1:Date=Date()
        bo.dataProcessing()
        bo.dataProcessing()
        val d1=(Date().time-t1.time)
        System.out.println("in one thread time: $d1")

        t1=Date()
        bo.dataProcessingInThreads()
        bo.dataProcessingInThreads()
        val d2=(Date().time-t1.time)
        System.out.println("in multi threads time: $d2")

        assertTrue(d2<d1)
    }



    fun Matrix.copy():Matrix{
        var res=Matrix(this.Width,this.Height, Flag(this.f.flag))
        forEach(Width,Height,{x,y->
            res.a[x][y]=a[x][y]
            res.b[x][y]=b[x][y]
            res.c[x][y]=c[x][y]
        })
        return res
    }
    fun AssertMatrixInRange(m:Matrix,m1:Matrix,delta:Int,inRange:Boolean=true){
        assertEquals(m.Width,m1.Width)
        assertEquals(m.Height,m1.Height)

        AssertArrayArrayInRange(m.a,m1.a,delta,inRange)
        AssertArrayArrayInRange(m.b,m1.b,delta,inRange)
        AssertArrayArrayInRange(m.c,m1.c,delta,inRange)
    }
    fun AssertArrayArrayInRange(a:Array<ShortArray>,a1:Array<ShortArray>,delta:Int,inRange:Boolean=true){
        assertEquals(a.size,a1.size)
        assertEquals(a[0].size,a1[0].size)

        var totalEqual=true
        forEach(a.size,a[0].size,{x,y->
            var isEqual=false
            for(i in -delta..delta) {
                if (a[x][y] == (a1[x][y] + i).toShort())
                    isEqual = true
            }
            if(inRange)
                assertTrue("in [$x][$y] val ${a[x][y]}!=${a1[x][y]}"
                        , isEqual)

            totalEqual = totalEqual && isEqual


        })
        if(inRange){
            assertTrue("total not equal",totalEqual)
        }else{
            assertFalse("total is equal",totalEqual)
        }
    }

    fun forEach(w:Int,h:Int,let:(x:Int,y:Int)->Unit){
        for(i in 0..w-1){
            for(j in 0..h-1){
                let(i,j)
            }
        }
    }

}