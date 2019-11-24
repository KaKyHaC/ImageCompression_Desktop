package ImageCompressionLib.Utils.Objects

import ImageCompressionLib.Data.Interfaces.IMatrix
import ImageCompressionLib.Data.Primitives.Pixel

object ImageDeviationUtils {

    fun calculateDeviation(first: IMatrix<Pixel>, second: IMatrix<Pixel>): Double {
        var sum = 0.0
        first.forEachIndexed{ i, j, value ->
            sum += second[i, j]?.let { value.calculateDeviation(it) } ?: 0.0
        }
        sum /= first.width * first.height
        return Math.sqrt(sum)
    }
}
