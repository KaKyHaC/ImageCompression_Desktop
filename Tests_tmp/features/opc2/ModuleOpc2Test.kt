package features.opc2

import data_model.generics.Triple
import data_model.generics.matrix.Matrix
import data_model.processing_data.ProcessingData
import data_model.types.Size
import org.junit.Test
import utils.MatrixUtils
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class ModuleOpc2Test {

    val rand = Random()

    val customMatrix = Matrix(arrayOf(
            arrayOf(-970, -10, 0, 0, 0, 0, 0, 0),
            arrayOf(-10, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0)), Int::class
    ).map { i, j, value -> value.toShort() }

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
        val processReverseTyped = moduleOpc2.processReverseTyped(direct)
        assertEquals(copy, processReverseTyped)
        copy.triple.first[0, 0]++
        assertNotEquals(copy, processReverseTyped)
    }

}