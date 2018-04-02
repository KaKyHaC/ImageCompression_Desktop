package ImageCompression.Utils.Functions

import ImageCompression.Constants.State
import ImageCompression.Containers.TripleShortMatrix
import ImageCompression.ProcessingModules.MyBufferedImage
import ImageCompression.Containers.Flag
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage
import java.io.File
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
        val mbi=MyBufferedImage(m)
        val bi = mbi.bufferedImage
        ImageIO.write(bi,"bmp", File(pathToResBmp2))
    }
    companion object {
        fun createMatrix(w:Int,h:Int): TripleShortMatrix {
            val m= TripleShortMatrix(w,h, Flag(0),State.RGB)
            for(i in 0..w-1){
                for(j in 0..h-1){
                    m.a[i][j]=((i+j)%255).toShort()
                    m.b[i][j]=((i+j)%255).toShort()
                    m.c[i][j]=((i+j)%255).toShort()
                }
            }
            return m
        }
    }
}