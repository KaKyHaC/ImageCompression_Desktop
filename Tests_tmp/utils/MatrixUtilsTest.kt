package utils

import data_model.generics.matrix.IteratorDefaultMatrix
import data_model.generics.matrix.Matrix
import data_model.types.Size
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue


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

    @Test
    fun gatherMatrix1() {
        gatherMatrix(Size(11, 13), Size(5, 5))
        gatherMatrix(Size(100, 200), Size(7, 9))
    }


    @Test
    fun gatherMatrix2() {
        gatherMatrix(Size(10, 10), Size(5, 5))
        gatherMatrix(Size(100, 200), Size(5, 5))
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

    fun splitIterator(childSize: Size) {
        matrix
                ?.let { IteratorDefaultMatrix(it, 0, 0, it.size, 0) }
                ?.let {
                    it.applyEach { i, j, value ->
                        assertEquals(matrix!![i, j], value)
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

    fun gatherMatrix(size: Size, childSize: Size) {
        val rand = Random()
        val origin = Matrix.create(size) { i, j -> rand.nextInt() }
        val splitIterator = MatrixUtils.splitIterator(origin, childSize, 0)
        val gatherMatrix = MatrixUtils.gatherMatrix(splitIterator, size)
        assertEquals(origin, gatherMatrix)
        assertNotEquals(origin, splitIterator[0, 0])
        origin[0, 0] += 2
        assertNotEquals(origin, gatherMatrix)
    }

    companion object {
        fun <T : Number> assertMatrixInRange(a: Matrix<T>, b: Matrix<T>, range: IntRange) {
            assertEquals(a.size, b.size)
            a.applyEach { i, j, value ->
                val dif = value.toInt() - b[i, j].toInt()
                assertTrue("at [$i,$j]  $value != ${b[i, j]}") { dif.absoluteValue in range }
                null
            }
        }
    }
}