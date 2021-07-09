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
import kotlin.test.assertNotEquals


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
        val moduleOpc2 = ModuleOpc2()
        val moduleOpcToBytes = ModuleOpcToBytes()

        val imageData = ProcessingData.Image(Triple(
                Matrix.create(matrixSize) { i, j -> rand.nextInt(255).absoluteValue.toShort() },
                Matrix.create(Size(8, 8)) { i, j -> rand.nextInt(255).absoluteValue.toShort() },
                Matrix.create(Size(16, 16)) { i, j -> rand.nextInt(255).absoluteValue.toShort() })
        )

        val opcData = moduleOpc2.processDirectTyped(imageData)
        val bytesData = moduleOpcToBytes.processDirectTyped(opcData)
        val reverseOpc = moduleOpcToBytes.processReverseTyped(bytesData)
        assertEquals(opcData, reverseOpc)
        opcData.triple.first[0,0].base.baseMax[0]++
        assertNotEquals(opcData, reverseOpc)
    }
}