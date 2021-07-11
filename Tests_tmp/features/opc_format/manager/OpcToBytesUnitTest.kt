package features.opc_format.manager

import data_model.generics.matrix.Matrix
import data_model.types.ByteVector
import data_model.types.DataOpc2
import data_model.types.Size
import features.opc2.managers.OpcProcessingManager2
import org.junit.Test
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class OpcToBytesUnitTest {

    val rand = Random()

    @Test
    fun test1() {
        test(Size(16))
    }

    @Test
    fun test12() {
        test(Size(128))
    }

    @Test
    fun test3() {
        test(Size(123, 13))
    }

    fun test(matrixSize: Size) {
        val matrix = Matrix.create(matrixSize) { i, j -> rand.nextInt(255).absoluteValue.toShort() }
        val opcProcessingManager2 = OpcProcessingManager2()
        val direct = opcProcessingManager2.direct(matrix)
        val byteVector = ByteVector()

        val opcToBytesUnit = OpcToBytesUnit()
        opcToBytesUnit.direct(byteVector, direct)
        val reader = byteVector.getReader()
        val reverse = opcToBytesUnit.reverse(reader, direct.map { i, j, value -> value.base })
        assertEquals(direct, reverse)
    }
}