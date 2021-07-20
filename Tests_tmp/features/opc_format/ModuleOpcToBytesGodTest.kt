package features.opc_format

import data_model.generics.Triple
import data_model.generics.matrix.Matrix
import data_model.processing_data.ProcessingData
import data_model.types.Size
import features.opc2.ModuleOpc2
import features.opc2_god_format.ModuleOpc2God
import org.junit.Test
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class ModuleOpcToBytesGodTest {

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
        val moduleOpcToBytes = ModuleOpc2God()

        val imageData = ProcessingData.Image(Triple(
                Matrix.create(matrixSize) { i, j -> rand.nextInt(255).absoluteValue.toShort() },
                Matrix.create(Size(8, 8)) { i, j -> rand.nextInt(255).absoluteValue.toShort() },
                Matrix.create(Size(16, 16)) { i, j -> rand.nextInt(255).absoluteValue.toShort() })
        )

        val opcData = moduleOpc2.processDirectTyped(imageData)
//        val reverseOpc  = moduleOpcToBytes.test(opcData)
        val bytes  = moduleOpcToBytes.processDirectTyped(opcData)
        val reverseOpc  = moduleOpcToBytes.processReverseTyped(bytes)

        assertEquals(opcData, reverseOpc)
        opcData.triple.first[0,0].base.baseMax[0]++
        assertNotEquals(opcData, reverseOpc)
    }
}