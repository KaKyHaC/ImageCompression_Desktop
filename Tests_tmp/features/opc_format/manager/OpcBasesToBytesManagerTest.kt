package features.opc_format.manager

import data_model.generics.Triple
import data_model.generics.matrix.Matrix
import data_model.types.ByteVector
import data_model.types.DataOpc2
import data_model.types.Size
import org.junit.Test
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class OpcBasesToBytesManagerTest {

    val random = Random()

    @Test
    fun test1() {
        test(Size(100))
    }

    fun test(size: Size, len: Int = 8) {
        val m1 = Matrix.create(size) { i, j -> DataOpc2.Base.Max(ShortArray(len) { random.nextInt(255).absoluteValue.toShort() }) as DataOpc2.Base }
        val m2 = Matrix.create(size) { i, j -> DataOpc2.Base.Max(ShortArray(len) { random.nextInt(255).absoluteValue.toShort() }) as DataOpc2.Base }
        val m3 = Matrix.create(size) { i, j -> DataOpc2.Base.Max(ShortArray(len) { random.nextInt(255).absoluteValue.toShort() }) as DataOpc2.Base }

        val opcBasesToBytesManager = OpcBasesToBytesManager()
        val byteVector = ByteVector()
        val triple = Triple(m1, m2, m3)

        opcBasesToBytesManager.direct(byteVector, triple)
        val reverse = opcBasesToBytesManager.reverse(byteVector.getReader())

        assertEquals(triple, reverse)
        triple.first[0,0].baseMax[0]++
        assertNotEquals(triple, reverse)
    }
}