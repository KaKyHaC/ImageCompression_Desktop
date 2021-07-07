package features.opc.managers

import data_model.generics.matrix.Matrix
import data_model.types.Size
import features.opc2.managers.OpcProcessUnit2
import features.opc2.managers.OpcProcessingManager2
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@RunWith(value = Parameterized::class)
internal class OpcProcessingManager2Test(parameters: OpcProcessingManager2.Parameters) {

    val random = Random()
    val opcProcessingUnit = OpcProcessingManager2(parameters)

    @Test
    fun directProcess4to16() {
        for (i in 4..16)
            for (j in 4..16)
                directProcess(Size(i, j))
    }

    @Test
    fun directProcess8x8() {
        directProcess(opcProcessingUnit.parameters.childSize)
    }

    @Test
    fun directProcess16x32() {
        directProcess(Size(16, 32))
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
        val reverseProcess = opcProcessingUnit.reverse(dataOpc, size)
        println("reverseProcess = $reverseProcess")
        assertEquals(reverseProcess, dataCopy)
        dataCopy[0, 0]++
        assertNotEquals(reverseProcess, dataCopy)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Any> {
            val res = listOf(
                    OpcProcessingManager2.Parameters(),
                    OpcProcessingManager2.Parameters(childSize = Size(5, 7)),
                    OpcProcessingManager2.Parameters(childSize = Size(10, 10)),
                    OpcProcessingManager2.Parameters(params = OpcProcessUnit2.Parameters(true, true, false, true, true)),
                    OpcProcessingManager2.Parameters(params = OpcProcessUnit2.Parameters(false, true, false, true, true)),
                    OpcProcessingManager2.Parameters(params = OpcProcessUnit2.Parameters(true, false, true, true, true)),
                    OpcProcessingManager2.Parameters(params = OpcProcessUnit2.Parameters(true, true, false, false, true)),
                    OpcProcessingManager2.Parameters(params = OpcProcessUnit2.Parameters(true, true, false, true, false))
            )
            return res
        }
    }

}