package data_model.containers.matrix

import data_model.containers.types.Size

open class IteratorMatrix<T>(
        source: Matrix<T>,
        val widthOffset: Int,
        val heightOffset: Int,
        val iteratorSize: Size
) : Matrix<T>(source.matrix) {

    override fun get(i: Int, j: Int): T {
        return super.get(i + widthOffset, j + heightOffset)
    }

    override fun set(i: Int, j: Int, value: T) {
        super.set(i + widthOffset, j + heightOffset, value)
    }

    override val width: Int
        get() = iteratorSize.width
    override val height: Int
        get() = iteratorSize.height

}