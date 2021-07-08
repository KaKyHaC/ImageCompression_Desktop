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


internal class ByteVectorUtilsTest {

    val rand = Random()

    @Test
    fun testCode8(){
        testCode(Size(8))
    }

    fun testCode(size: Size) {
        val matrix = Matrix.create(size) { i, j -> rand.nextInt(10000).absoluteValue.toShort() }
        val opcProcessUnit2 = OpcProcessUnit2(OpcProcessUnit2.Parameters())
        val opc = opcProcessUnit2.direct(matrix)
        val byteVector = ByteVector()

        ByteVectorUtils.Code.direct(byteVector, opc.code as DataOpc2.Code.BI, opc.base, size)
        val reverse = ByteVectorUtils.Code.reverse(byteVector.getReader(), opc.base, size)

        assertEquals(opc.code, reverse)
    }
}