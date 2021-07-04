package data_model.generics.matrix

import data_model.types.Size

class IteratorDefaultMatrix<T : Any>(
        val source: Matrix<T>,
        widthOffset: Int,
        heightOffset: Int,
        iteratorSize: Size,
        val defaultValue: T
) : IteratorMatrix<T>(source, widthOffset, heightOffset, iteratorSize) {

    override fun get(i: Int, j: Int): T {
        return if (isSafe(i, j)) super.get(i, j) else defaultValue
    }

    override fun set(i: Int, j: Int, value: T) {
        if (isSafe(i, j)) super.set(i, j, value)
    }

    private fun isSafe(i: Int, j: Int): Boolean {
        return i >= 0 && j >= 0 &&
                i < iteratorSize.width && j < iteratorSize.height &&
                i + widthOffset < source.width && j + heightOffset < source.height
    }
}