package features.opc2.managers

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc2
import data_model.types.Size
import org.junit.Test
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class OpcBasesProcessingManager2Test {

    val rand = Random()
    val manager = OpcBasesProcessingManager2()

    @Test
    fun test1() {
        testMaxBase(Size(16,32), 8)
    }

    @Test
    fun test2() {
        testMaxBase(Size(16,33), 8)
    }

    fun testMaxBase(matrixSize: Size, baseSize: Int) {
        val matrix = Matrix.create(matrixSize) { i, j ->
            DataOpc2.Base.Max(
                    ShortArray(baseSize) { rand.nextInt(255).absoluteValue.toShort() }
            ) as DataOpc2.Base
        }
        val direct = manager.direct(matrix)
        val reverse = manager.reverse(direct, Size(baseSize, 1))

        assertEquals(matrix, reverse)
        matrix[0,0].baseMax[0]++
        assertNotEquals(matrix, reverse)
    }
}