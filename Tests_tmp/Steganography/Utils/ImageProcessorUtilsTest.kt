package Steganography.Utils

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*

class ImageProcessorUtilsTest{
    val ipu= ImageProcessorUtils()
    val rand=Random()


    @Test
    fun testDirectReverce1(){
        testStego(200,200,8,8,true,1)
//        testStego(200,200,8,8,false,1)
        testStego(327,402,8,8,true,1)
        testStego(327,402,2,15,true,1)
    }
    @Test
    fun testDirectReverceNotMulti(){
        println("not work")
        testStego(200,200,8,8,false,2)
    }
    @Test
    fun testEqual(){
        println("not work")
        testStego(200,200,8,8,true,0)
    }
    fun testStego(w: Int,h: Int,unit_W:Int,unit_H:Int,isMultiTWO:Boolean,range: Int){
        val im=BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR)
        for(i in 0 ..w-1)
            for(j in 0..h-1){
                im.setRGB(i,j,i+j)
            }


        val units=ipu.imageToUnits(im,unit_W,unit_H)
        val opcs=ipu.directStego(units,true)
        val resUnits=ipu.reverceStego(opcs,unit_W,unit_H,isMultiTWO)
        val im1=ipu.unitsToImage(resUnits,w,h)


        for(i in 0 ..w-1)
            for(j in 0..h-1){
                var inRangeR=false
                var inRangeB=false
                var inRangeG=false
                val rgb=im.getRGB(i,j)
                val r=Color(rgb).red
                val g=Color(rgb).green
                val b=Color(rgb).blue
                for(v in -range..range){
                    if(r+v==Color(im1.getRGB(i,j)).red)inRangeR=true
                    if(g+v==Color(im1.getRGB(i,j)).green)inRangeG=true
                    if(b+v==Color(im1.getRGB(i,j)).blue)inRangeB=true
                }
                if(!inRangeR||!inRangeB||!inRangeG)throw Exception("not in range[$i,$j] = $rgb")
            }

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