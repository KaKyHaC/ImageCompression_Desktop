package ImageCompressionLib.Utils.Objects

import ImageCompressionLib.Data.Generics.Matrix
import ImageCompressionLib.Data.Primitives.Size

object MatrixUtils {

    fun <T : Number> multiply(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        val res = Matrix(Size(a.width, b.height)) { _, _ -> 0 }
        for (i in 0 until a.height) {
            for (j in 0 until b.width) {
                for (k in 0 until a.width) {
                    res[i,j] = a[i,k] * b [k,j]
                }
            }
        }
        return res
    }
}