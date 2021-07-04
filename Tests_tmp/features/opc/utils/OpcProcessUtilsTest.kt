package features.opc.utils

import data_model.generics.matrix.Matrix
import data_model.types.Size
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class OpcProcessUtilsTest {
    val random = Random()

    @Test
    fun directProcess8x8() {
        for (i in 4..16)
            for (j in 4..16)
                directProcess(Size(i, j))
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
        val preOpcParams = OpcProcessUtils.PreOpcParams()
        val dataOrigin = Matrix.create(size) { i, j -> (random.nextInt() % 256).toShort() }
        val dataCopy = Matrix.create(dataOrigin.size) { i, j -> dataOrigin[i, j] }
        println("dataOrigin = $dataOrigin")
        val dataOpc = OpcProcessUtils.directProcess(preOpcParams, OpcProcessUtils.OpcParams(), dataOrigin)
        println("dataOpc = $dataOpc")
        val reverseProcess = OpcProcessUtils.reverseProcess(preOpcParams, dataOpc, size)
        println("reverseProcess = $reverseProcess")
        assertEquals(reverseProcess, dataCopy)
        dataCopy[0, 0]++
        assertNotEquals(reverseProcess, dataCopy)
    }
}