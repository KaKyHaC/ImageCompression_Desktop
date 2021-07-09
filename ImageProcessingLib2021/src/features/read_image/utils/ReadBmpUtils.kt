package features.read_image.utils

import java.awt.image.BufferedImage
import data_model.generics.Triple
import data_model.generics.matrix.Matrix
import data_model.types.Size

object ReadBmpUtils {

    const val ROLL_ALFA = 24
    const val ROLL_RED = 16
    const val ROLL_GREEN = 8
    const val ROLL_BLUE = 0

    fun map(bufferedImage: BufferedImage): Triple<Matrix<Short>> {
        val size = Size(bufferedImage.width, bufferedImage.height)
        val r = Matrix.create<Short>(size) { i, j -> 0 }
        val g = Matrix.create<Short>(size) { i, j -> 0 }
        val b = Matrix.create<Short>(size) { i, j -> 0 }
        for (i in 0 until size.width) {
            for (j in 0 until size.height) {
                val rgb = bufferedImage.getRGB(i, j)
                r[i, j] = (rgb shr ROLL_RED and 0xff).toShort()
                g[i, j] = (rgb shr ROLL_GREEN and 0xff).toShort()
                b[i, j] = (rgb shr ROLL_BLUE and 0xff).toShort()
            }
        }
        return Triple(r, g, b)
    }


    fun map(bmp: Triple<Matrix<Short>>): BufferedImage {
        val size = bmp.first.size
        val bufferedImage = BufferedImage(size.width, size.height, BufferedImage.TYPE_3BYTE_BGR)
        for (i in 0 until size.width) {
            for (j in 0 until size.height) {
                val r = bmp.first[i, j].toInt() shl ROLL_RED
                val g = bmp.second[i, j].toInt() shl ROLL_GREEN
                val b = bmp.third[i, j].toInt() shl ROLL_BLUE
                bufferedImage.setRGB(i, j, r or g or b)
            }
        }
        return bufferedImage
    }
}