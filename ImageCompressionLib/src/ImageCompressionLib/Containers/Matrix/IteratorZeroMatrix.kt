package ImageCompressionLib.Containers.Matrix

import ImageCompressionLib.Containers.Type.Size

class IteratorZeroMatrix<T : Any>(
    matrix: Array<Array<Any>>,
    wStart: Int,
    hStart: Int,
    sizeBuffer: Size,
    val defaultValue: T
) : IteratorMatrix<T>(matrix, wStart, hStart, sizeBuffer) {
    override fun get(i: Int, j: Int): T {
        if (i + wStart >= size.width || j + hStart >= size.height)
            return defaultValue
        return super.get(i, j)
    }

    override fun set(i: Int, j: Int, value: T) {
        if (i + wStart >= size.width || j + hStart >= size.height)
            return
        super.set(i, j, value)
    }
}