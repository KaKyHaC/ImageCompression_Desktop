package ImageCompressionLib.utils.functions

import ImageCompressionLib.containers.type.MyBufferedImage

class ImageStandardDeviation private constructor () {
    companion object {
        @JvmStatic
        fun getDeviation(original: MyBufferedImage, destination: MyBufferedImage): Double {
            if(!validate(original,destination)) throw Exception("not valid")

            val r= getDeviation(original, destination,16)
            val g= getDeviation(original, destination,8)
            val b= getDeviation(original, destination,0)

            println("deviation r=$r, g=$g, b=$b")
            return (r+g+b)/3.0
        }
        private fun getDeviation(original: MyBufferedImage, destination: MyBufferedImage,shift:Int): Double {
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
        fun validate(original: MyBufferedImage, destination: MyBufferedImage):Boolean{
            return original.width==destination.width&&original.height==destination.height
        }
    }

}
