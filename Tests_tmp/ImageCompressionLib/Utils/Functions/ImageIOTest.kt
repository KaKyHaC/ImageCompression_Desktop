package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Constants.State
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.TripleShortMatrix
import ImageCompressionLib.ProcessingModules.ModuleImage
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.Size
import Utils.BuffImConvertor
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

class ImageIOTest{
    val pathToBmp="/files/desk.bmp"
    val pathToResBmp1="/files/Tests/ImageIO1.bmp"
    val pathToResBmp2="/files/Tests/ImageIO2.bmp"

    @Before
    fun init(){
        val f=File(pathToResBmp1)
        f.parentFile.mkdir()
        f.createNewFile()
        val f1=File(pathToResBmp2)
        f1.createNewFile()
    }
    @Test
    fun ReadWriteTest(){
        val i=ImageIO.read(File(pathToBmp))
        ImageIO.write(i,"bmp",File(pathToResBmp1))
    }

    @Test
    fun RandomImageWriteTest(){
        val i =BufferedImage(134,543,BufferedImage.TYPE_3BYTE_BGR)
        ImageIO.write(i,"bmp",File(pathToResBmp2))
    }

    @Test
    fun MyBufferedImageWriteTest(){
        val m=createMatrix(255,255)
        val mbi= ModuleImage(m)
        val mybi = mbi.getBufferedImage()
        val bi=BuffImConvertor.instance.convert(mybi)
        ImageIO.write(bi,"bmp", File(pathToResBmp2))
    }
    companion object {
        fun createMatrix(w:Int,h:Int,unitSize: Size=Size(8,8)): TripleShortMatrix {
            val m= TripleShortMatrix(Parameters.createParametresForTest(Size(w,h),unitSize),State.RGB)
            m.a.forEach(){i, j, value ->  Math.abs(Random().nextInt(255)).toShort() }
            m.b.forEach(){i, j, value ->  Math.abs(Random().nextInt(255)).toShort() }
            m.c.forEach(){i, j, value ->  Math.abs(Random().nextInt(255)).toShort() }
//            val a=ShortMatrix(w,h){i, j -> ((i+j)%255).toShort() }
//            val a=ShortMatrix(w,h){i, j -> (Math.abs(Random().nextInt(255))).toShort() }
//            val b=ShortMatrix(w,h){i, j -> (Math.abs(Random().nextInt(255))).toShort() }
//            val c=ShortMatrix(w,h){i, j -> (Math.abs(Random().nextInt(255))).toShort() }

            return m
        }
    }
}