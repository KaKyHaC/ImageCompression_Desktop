package ImageProcessingLib2021.features.opc

import data_model.generics.matrix.Matrix
import data_model.types.Size
import features.opc.utils.OpcProcessUtils
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals


class OpcProcessUtilsTest {

    val random = Random()

    @Test
    fun directProcess() {
        directProcess(Size(8,8))
    }

    fun directProcess(size: Size) {
        val preOpcParams = OpcProcessUtils.PreOpcParams()
        val dataOrigin = Matrix.create(size) { i, j -> (random.nextInt()% 256).toShort()  }
        val dataCopy = Matrix.create(dataOrigin.size) { i, j -> dataOrigin[i, j] }
        println("dataOrigin = $dataOrigin")
        val dataOpc = OpcProcessUtils.directProcess(preOpcParams, OpcProcessUtils.OpcParams(), dataOrigin)
        println("dataOpc = $dataOpc")
        val reverseProcess = OpcProcessUtils.reverseProcess(preOpcParams, dataOpc, size)
        println("reverseProcess = $reverseProcess")
        assertEquals(reverseProcess, dataCopy)
    }
}