package features.opc_format.manager

import data_model.types.ByteVector
import data_model.types.DataOpc2
import org.junit.Test
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals


internal class OpcBasesToBytesUnitTest {
    val rand = Random()

    @Test
    fun test8() {
        customTest(8)
    }

    fun customTest(size: Int) {
        val opcBasesToBytesUnit = OpcBasesToBytesUnit(OpcBasesToBytesUnit.Type.MIN_AND_MAX)
        val min = ShortArray(size) { rand.nextInt(255).absoluteValue.toShort() }
        val max = ShortArray(size) { rand.nextInt(255).absoluteValue.toShort() }
        val base = DataOpc2.Base.MaxMin(max, min)
        val vector = ByteVector()
        opcBasesToBytesUnit.direct(vector, base)
        val reverse = opcBasesToBytesUnit.reverse(vector.getReader(), size)
        assertEquals(base.baseMin.toList(), (reverse as DataOpc2.Base.MaxMin).baseMin.toList())
        assertEquals(base.baseMax.toList(), reverse.baseMax.toList())
    }
}