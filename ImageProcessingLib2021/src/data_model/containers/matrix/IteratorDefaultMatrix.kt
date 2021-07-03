package data_model.containers.matrix

import data_model.types.Size

class IteratorDefaultMatrix<T>(
        source: Matrix<T>,
        widthOffset: Int,
        heightOffset: Int,
        iteratorSize: Size,
        val defaultValue: T
) : IteratorMatrix<T>(source, widthOffset, heightOffset, iteratorSize) {
    override fun get(i: Int, j: Int): T {
        if (i + widthOffset >= width || j + heightOffset >= height)
            return defaultValue
        return super.get(i, j)
    }

    override fun set(i: Int, j: Int, value: T) {
        if (i + widthOffset >= width || j + heightOffset >= height)
            return
        super.set(i, j, value)
    }
}