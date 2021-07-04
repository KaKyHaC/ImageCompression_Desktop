package utils

import data_model.generics.matrix.IteratorDefaultMatrix
import data_model.generics.matrix.Matrix
import data_model.types.Size
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class MatrixUtilsTest {

    var matrix: Matrix<Int>? = null

    @Before
    fun setUp() {
        matrix = Matrix.create(Size(10, 10)) { i, j -> i + j * i }
    }

    @Test
    fun rectIterator1() {
        rectIterator(1, 1, Size(2, 2), null)
    }

    @Test
    fun rectIterator2() {
        rectIterator(2, 2, Size(2, 2), null)
    }

    fun rectIterator(w: Int, h: Int, size: Size, default: Int?) {
        matrix?.let {
            val rectIterator = MatrixUtils.rectIterator(it, w, h, size, default)
            assertEquals(it[w, h], rectIterator[0, 0])
            assertEquals(it[w + size.width - 1, h + size.height - 1], rectIterator[size.width - 1, size.height - 1])
            assertNotEquals(it[w + size.width - 1, h - 1], rectIterator[size.width - 1, size.height - 1])
            assertNotEquals(it[w - 1, h + size.height - 1], rectIterator[size.width - 1, size.height - 1])
        }
    }

    @Test
    fun splitIterator1() {
        splitIterator(Size(4, 3))
        splitIterator(Size(2, 3))
    }

    @Test
    fun splitIterator2() {
        splitIterator(Size(2, 2))
        splitIterator(Size(5, 5))
    }

    fun splitIterator(childSize: Size) {
        matrix
                ?.let { IteratorDefaultMatrix(it, 0, 0, it.size, 0) }
                ?.let {
                    it.applyEach{i, j, value ->
                        assertEquals(matrix!![i,j], value)
                        null
                    }
                    val splitIterator = MatrixUtils.splitIterator(it, childSize, 0)
                    splitIterator.applyEach { i, j, child ->
                        child.applyEach { x, y, value ->
                            assertEquals(value, it[x + i * childSize.width, y + j * childSize.height], "i=$i, j=$j, x=$x, y=$y")
                            null
                        }
                        null
                    }
                }
    }
}