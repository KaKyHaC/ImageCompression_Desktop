package features.opc2

import data_model.generics.Triple
import data_model.generics.matrix.Matrix
import data_model.processing_data.ProcessingData
import data_model.types.Size
import org.junit.Test
import utils.MatrixUtils
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class ModuleBasesOpc2Test {

    val rand = Random()

    @Test
    fun test8() {
        test(Size(8))
    }

    @Test
    fun test100() {
        test(Size(100))
    }

    @Test
    fun test128() {
        test(Size(128))
    }

    @Test
    fun testHd() {
        test(Size(1920, 1080))
    }

    fun test(matrixSize: Size) {
        val moduleOpc2 = ModuleOpc2()
        val moduleBasesOpc2 = ModuleBasesOpc2()
        val data = ProcessingData.Image(Triple(
                Matrix.create(matrixSize) { i, j -> (-100 + rand.nextInt(255)).toShort() },
                Matrix.create(matrixSize) { i, j -> (-100 + rand.nextInt(255)).toShort() },
                Matrix.create(matrixSize) { i, j -> (-100 + rand.nextInt(255)).toShort() }
        ))
        val copy = ProcessingData.Image(Triple(
                MatrixUtils.copy(data.triple.first),
                MatrixUtils.copy(data.triple.second),
                MatrixUtils.copy(data.triple.third)
        ))
        val direct = moduleOpc2.processDirectTyped(data)
        val bases = ProcessingData.Opc2.Bases(direct)
        val processDirectTyped = moduleBasesOpc2.processDirectTyped(bases)
        val processReverseTyped = moduleBasesOpc2.processReverseTyped(processDirectTyped)
        assertEquals(bases, processReverseTyped)
    }
}