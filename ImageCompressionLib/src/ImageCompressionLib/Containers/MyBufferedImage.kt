package ImageCompressionLib.Containers

class MyBufferedImage {
    fun getWidth():Int
    fun getHeight():Int
    fun setRGB(i:Int,j:Int,rgb:Int)
    fun getRGB(i: Int,j: Int):Int
    fun getDataBuffer():ByteArray
}