package utils

import ImageCompressionLib.containers.type.MyBufferedImage
import java.awt.image.BufferedImage

class BuffImConvertor private constructor(){
    val alfa=(0xff) shl MyBufferedImage.ROLL_ALFA
    companion object {
        @JvmStatic val instance = BuffImConvertor()
    }
    fun convert(im: MyBufferedImage):BufferedImage{
        val res=BufferedImage(im.width,im.height,BufferedImage.TYPE_3BYTE_BGR)
        for(i in 0 until im.width)
            for(j in 0 until im.height){
                val v=im.getRGB(i,j) or alfa
                res.setRGB(i,j,v)
//                val d=res.getRGB(i,j)
//                val l=d
            }
        return res
    }
    fun convert(im:BufferedImage): MyBufferedImage {
        val res= MyBufferedImage(im.width, im.height)
        for(i in 0 until im.width)
            for(j in 0 until im.height){
                val v =im.getRGB(i,j) and (alfa.inv())
                res.setRGB(i,j,v)
            }
        return res
    }
}