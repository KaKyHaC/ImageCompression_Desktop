package features.opc_format.manager

import data_model.types.ByteVector
import data_model.types.DataOpc2
import features.opc_format.utils.OpcBasesToBytesUtils
import org.junit.Test
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals


internal class OpcBasesToBytesUnitTest {
    val rand = Random()

//    @Test
//    fun unit1() {
//        val opcBasesToBytesUnit = OpcBasesToBytesUnit(OpcBasesToBytesUnit.Type.MIN_AND_MAX)
//        for (i in 0..100000)
//            customTestUnit(opcBasesToBytesUnit, 8)
//    }


    @Test
    fun utils1() {
        for (i in 0..100000)
            customTestUtils(8)
    }


//    @Test
//    fun unit2() {
//        val opcBasesToBytesUnit = OpcBasesToBytesUnit(OpcBasesToBytesUnit.Type.MIN_AND_MAX)
//        for (i in 0..100000)
//            customTestUnit(opcBasesToBytesUnit, 8)
//    }


    @Test
    fun utils2() {
        for (i in 0..100000)
            customTestUtils(8)
    }

//
//    @Test
//    fun unit3() {
//        val opcBasesToBytesUnit = OpcBasesToBytesUnit(OpcBasesToBytesUnit.Type.MIN_AND_MAX)
//        for (i in 0..100000)
//            customTestUnit(opcBasesToBytesUnit, 8)
//    }


    @Test
    fun utils3() {
        for (i in 0..100000)
            customTestUtils(8)
    }

    fun customTestUtils(size: Int) {
        val opcBasesToBytesUnit = OpcBasesToBytesUtils
        val min = ShortArray(size) { rand.nextInt(255).absoluteValue.toShort() }
        val max = ShortArray(size) { rand.nextInt(255).absoluteValue.toShort() }
        val base = DataOpc2.Base.MaxMin(max, min)
        val vector = ByteVector()
        opcBasesToBytesUnit.direct(vector, base, OpcBasesToBytesUtils.Type.MIN_AND_MAX)
        val reverse = opcBasesToBytesUnit.reverse(vector.getReader(), size, OpcBasesToBytesUtils.Type.MIN_AND_MAX)
        assertEquals(base.baseMin.toList(), (reverse as DataOpc2.Base.MaxMin).baseMin.toList())
        assertEquals(base.baseMax.toList(), reverse.baseMax.toList())
    }


//    fun customTestUnit(unit: OpcBasesToBytesUnit, size: Int) {
//        val min = ShortArray(size) { rand.nextInt(255).absoluteValue.toShort() }
//        val max = ShortArray(size) { rand.nextInt(255).absoluteValue.toShort() }
//        val base = DataOpc2.Base.MaxMin(max, min)
//        val vector = ByteVector()
//        unit.direct(vector, base)
//        val reverse = unit.reverse(vector.getReader(), size)
//        assertEquals(base.baseMin.toList(), (reverse as DataOpc2.Base.MaxMin).baseMin.toList())
//        assertEquals(base.baseMax.toList(), reverse.baseMax.toList())
//    }
}