import ImageCompression.Objects.MyImage
import ImageCompression.Utils.AndroidBmpUtil
import javafx.scene.image.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class MyBMP(url: File):MyImage.Bitmap{
    var image:BufferedImage = BufferedImage(0,0,BufferedImage.TYPE_3BYTE_BGR)

    init {
        image=ImageIO.read(url)
    }
    constructor(url: String) : this(File(url)) {    }


    override fun getWidth(): Int {
        return image.width as Int
    }

    override fun getHeight(): Int {
        return image.height as Int
    }

    override fun getPixels(pixels: IntArray?, param1: Int, paramWidth: Int, param2: Int, param3: Int, width: Int, height: Int) {
        for(i in 0..width-1){
            for(j in 0..height-1){
                pixels?.set(i*height+j,image.getRGB(i,j))
            }
        }
    }

    override fun getPixel(x: Int, y: Int): Int {
        return image.getRGB(x,y)
    }

    override fun setPixel(i: Int, j: Int, newPixel: Int): Int {
        image.setRGB(i,j,newPixel)
        return 1;
    }

}