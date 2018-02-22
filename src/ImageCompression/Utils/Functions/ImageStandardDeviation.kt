package ImageCompression.Utils.Functions

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ImageStandardDeviation private constructor () {
    companion object {
        @JvmStatic
        fun getDeviation(original: BufferedImage, destination: BufferedImage): Double {
            if(!validate(original,destination)) throw Exception("not valid")

            val r= getDeviation(original, destination,16)
            val g= getDeviation(original, destination,8)
            val b= getDeviation(original, destination,0)

            println("deviation r=$r, g=$g, b=$b")
            return (r+g+b)/3.0
        }
        private fun getDeviation(original: BufferedImage, destination: BufferedImage,shift:Int): Double {
            if(!validate(original,destination)) throw Exception("not valid")

            var sum=0.0;
            for(i in 0..original.width-1){
                for(j in 0..original.height-1){
                    val a=(original.getRGB(i,j) shr shift) and 0xff
                    val b=(destination.getRGB(i,j) shr shift) and 0xff
                    val r= Math.pow((a-b).toDouble(),2.0)
                    sum+=r;
                }
            }
            sum/=(original.width*original.height)
            sum=Math.sqrt(sum)
            return sum
        }
        @JvmStatic
        fun validate(original: BufferedImage, destination: BufferedImage):Boolean{
            return original.width==destination.width&&original.height==destination.height
        }
    }

}
fun main(arg:Array<String>){
    val i1:BufferedImage=ImageIO.read(File("D:\\files\\desk.bmp"))
//    val i2:BufferedImage=ImageIO.read(File("D:\\files\\deskres.bmp"))
//    val i2:BufferedImage=ImageIO.read(File("D:\\files\\desk.bmp"))
    println("jpeg")
    val i2:BufferedImage=ImageIO.read(File("D:\\Dima\\IDEA Projects\\ImageCompressionKotlin\\jpegEncoder.jpeg"))
    ImageStandardDeviation.getDeviation(i1,i2)

    println("br")
    val i3:BufferedImage=ImageIO.read(File("D:\\files\\deskres.bmp"))
    ImageStandardDeviation.getDeviation(i1,i3)
}