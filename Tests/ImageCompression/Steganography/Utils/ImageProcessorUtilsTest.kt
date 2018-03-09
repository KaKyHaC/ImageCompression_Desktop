package ImageCompression.Steganography.Utils

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage
import java.util.*

class ImageProcessorUtilsTest{
    val ipu=ImageProcessorUtils()
    val rand=Random()

    fun testStego(){
        
    }

    @Test
    fun testTestMesasge(){
        test2(2,2)
        test2(200,200)
        test2(200,400)
        test2(200,3)
    }
    fun test2(w: Int,h: Int){
        val im=BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR)
        for(i in 0 ..w-1)
            for(j in 0..h-1){
                im.setRGB(i,j,i+j)
            }


        val units=ipu.imageToUnits(im,8,8)
        val len=ipu.getMessageMaxSize(units)
        val message=BooleanArray(len){rand.nextBoolean()}

        val index=ipu.setMessage(units,message)
        assertTrue(index==len)

        val resMes=ipu.getMessage(units)
        for(i in 0 until len)
                assertEquals(message[i],resMes[i])

    }

    @Test
    fun testImage(){
        testIm(200,300)
        testIm(400,300)
        testIm(200,1200)
    }
    fun testIm(w:Int,h:Int){
        val im=BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR)
        for(i in 0 ..w-1)
            for(j in 0..h-1){
                im.setRGB(i,j,i+j)
            }

        val units=ipu.imageToUnits(im,8,8)
        val im1=ipu.unitsToImage(units,w,h)
        for(i in 0 ..w-1)
            for(j in 0..h-1){
                assertTrue(im.getRGB(i,j)==im1.getRGB(i,j))
            }
    }
}