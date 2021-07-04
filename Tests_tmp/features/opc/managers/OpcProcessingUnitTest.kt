package features.opc.managers

import data_model.generics.matrix.Matrix
import data_model.types.Size
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class OpcProcessingUnitTest {
    val random = Random()
    val opcProcessingUnit = OpcProcessingUnit()

    @Test
    fun directProcess4to16() {
        for (i in 4..16)
            for (j in 4..16)
                directProcess(Size(i, j))
    }

    @Test
    fun directProcess8x8() {
        directProcess(opcProcessingUnit.childSize)
    }

    @Test
    fun directProcess16x32() {
        directProcess(Size(16,32))
    }

    @Test
    fun directProcess50x50() {
        directProcess(Size(50, 50))
    }

    @Test
    fun directProcess100x100() {
        directProcess(Size(100, 100))
    }

    fun directProcess(size: Size) {
        val dataOrigin = Matrix.create(size) { i, j -> (random.nextInt() % 256).toShort() }
        val dataCopy = Matrix.create(dataOrigin.size) { i, j -> dataOrigin[i, j] }
        println("dataOrigin = $dataOrigin")
        val dataOpc = opcProcessingUnit.direct(dataOrigin)
        println("dataOpc = $dataOpc")
        val reverseProcess = opcProcessingUnit.reverse(dataOpc)
        println("reverseProcess = $reverseProcess")
        assertEquals(reverseProcess, dataCopy)
        dataCopy[0, 0]++
        assertNotEquals(reverseProcess, dataCopy)
    }
}