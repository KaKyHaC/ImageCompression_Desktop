package features.opc2

import data_model.generics.Triple
import data_model.generics.matrix.Matrix
import data_model.processing_data.ProcessingData
import data_model.types.Size
import features.opc2.ModuleOpc2
import features.opc_format.ModuleOpcToBytes
import org.junit.Test
import utils.MatrixUtils
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class ImageOpcBytesTest {

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


    @Test
    fun testHd() {
        test(Size(1920,1080))
    }

    fun test(matrixSize: Size) {
        val moduleOpc2 = ModuleOpc2()
        val moduleOpcToBytes = ModuleOpcToBytes()

        val imageData = ProcessingData.Image(Triple(
                Matrix.create(matrixSize) { i, j -> (-100 + rand.nextInt(255)).toShort() },
                Matrix.create(Size(8, 8)) { i, j -> (-100 + rand.nextInt(255)).toShort() },
                Matrix.create(Size(16, 16)) { i, j -> (-100 + rand.nextInt(255)).toShort() })
        )

        val copy = ProcessingData.Image(Triple(
                MatrixUtils.copy(imageData.triple.first),
                MatrixUtils.copy(imageData.triple.second),
                MatrixUtils.copy(imageData.triple.third)
        ))

        val opcData = moduleOpc2.processDirectTyped(imageData)
        val bytesData = moduleOpcToBytes.processDirectTyped(opcData)

        val reverseOpc = moduleOpcToBytes.processReverseTyped(bytesData)
        val reverseImage = moduleOpc2.processReverseTyped(reverseOpc)

        assertEquals(copy, reverseImage)
        copy.triple.first[0, 0]++
        assertNotEquals(copy, reverseImage)
    }
}