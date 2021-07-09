package features.opc_format

import data_model.generics.Triple
import data_model.generics.matrix.Matrix
import data_model.processing_data.ProcessingData
import data_model.types.Size
import features.opc2.ModuleOpc2
import org.junit.Test
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals


internal class ModuleOpcToBytesTest {

    val rand = Random()

    @Test
    fun test16() {
        test(Size(16))
    }

    @Test
    fun test1128() {
        test(Size(128))
    }

    @Test
    fun test128() {
        test(Size(128))
    }

    fun test(matrixSize: Size) {
        val matrix = Matrix.create(matrixSize) { i, j -> rand.nextInt(255).absoluteValue.toShort() }
        val moduleOpc2 = ModuleOpc2()
        val imageData = ProcessingData.Image(Triple(matrix, matrix, matrix))
        val opcData = moduleOpc2.processDirect(imageData)
        val moduleOpcToBytes = ModuleOpcToBytes()
        val bytesData = moduleOpcToBytes.processDirect(opcData)
        val reverseOpc = moduleOpcToBytes.processReverse(bytesData)
        assertEquals(opcData, reverseOpc)
    }
}