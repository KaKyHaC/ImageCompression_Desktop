package features.opc2.managers

import data_model.generics.matrix.Matrix
import data_model.types.Size
import org.junit.Test
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class OpcProcessUnit2Test {

    val random = Random()

    @Test
    fun directProcess4x4() {
        directProcess(Size(4))
    }


    fun directProcess(size: Size) {
        val dataOrigin = Matrix.create(size) { i, j -> (random.nextInt(255).absoluteValue).toShort() }
        val dataCopy = Matrix.create(dataOrigin.size) { i, j -> dataOrigin[i, j] }
        val opcProcessUnit2 = OpcProcessUnit2(OpcProcessUnit2.Parameters())
        println("dataOrigin = $dataOrigin")
        val dataOpc = opcProcessUnit2.direct(dataOrigin)
        println("dataOpc = $dataOpc")
        val reverseProcess = opcProcessUnit2.reverse(dataOpc, size)
        println("reverseProcess = $reverseProcess")
        assertEquals(reverseProcess, dataCopy)
        dataCopy[0, 0]++
        assertNotEquals(reverseProcess, dataCopy)
    }
}