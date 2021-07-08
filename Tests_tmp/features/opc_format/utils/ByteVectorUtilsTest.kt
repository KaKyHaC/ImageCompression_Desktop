package features.opc_format.utils

import data_model.generics.matrix.Matrix
import data_model.types.ByteVector
import data_model.types.DataOpc2
import data_model.types.Size
import features.opc2.managers.OpcProcessUnit2
import org.junit.Test
import java.math.BigInteger
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue


internal class ByteVectorUtilsTest {

    val rand = Random()

    @Test
    fun testCode3() {
        testCode(Size(3))
    }

    @Test
    fun testCode8() {
        testCode(Size(8))
    }

    @Test
    fun testCode16() {
        testCode(Size(16))
    }

    @Test
    fun testBits() {
        testBits(Size(16))
        testBits(Size(8))
    }

    @Test
    fun testBits1() {
        testBits(Size(16, 3))
        testBits(Size(8,9))
    }

    @Test
    fun testBI() {
        val bi = BigInteger.valueOf(4543)
        val toByteArray = bi.toByteArray()
        val toMutableList = toByteArray.toMutableList()
        toMutableList.add(0, 0)
        toMutableList.add(0, 0)
        val toByteArray1 = toMutableList.toByteArray()
        val bi2 = BigInteger(toByteArray1)
        assertEquals(bi, bi2)

    }

    fun testCode(size: Size) {
        val matrix = Matrix.create(size) { i, j -> rand.nextInt(255).absoluteValue.toShort() }
        val opcProcessUnit2 = OpcProcessUnit2(OpcProcessUnit2.Parameters())
        val opc = opcProcessUnit2.direct(matrix)
        val byteVector = ByteVector()

        ByteVectorUtils.Code.direct(byteVector, opc.code as DataOpc2.Code.BI, opc.base, size)
        val reverse = ByteVectorUtils.Code.reverse(byteVector.getReader(), opc.base, size)

        val expected = (opc.code as DataOpc2.Code.BI).N
        val actual = reverse.N
        assertTrue { actual.equals(expected) }
    }

    fun testBits(size: Size) {
        val matrix = Matrix.create(size) { i, j -> rand.nextBoolean() }
        val vector = ByteVector()
        ByteVectorUtils.Bits.direct(vector, matrix)

        val reader = vector.getReader()
        val reverse = ByteVectorUtils.Bits.reverse(reader, size)

        assertEquals(matrix, reverse)
        matrix[0, 0] = matrix[0, 0].not()
        assertNotEquals(matrix, reverse)
    }
}