package features.enlargement.utils

import data_model.generics.matrix.Matrix
import data_model.types.Size
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class EnlargementUtilsTest {

    val random = Random()

    @Test
    fun test2() {
        testInt(Size(16, 16))
        testInt(Size(16, 32))
    }

    @Test
    fun test1() {
        testInt(Size(13, 15))
        testInt(Size(17, 33))
    }

    @Test
    fun testShort() {
        testShort(Size(14, 16))
        testShort(Size(18, 32))
    }

    fun testInt(size: Size, value:Int = 5) {
        val origin = Matrix.create(size) { i, j -> value }
        val direct = EnlargementUtils.direct(origin)
        val reverse = EnlargementUtils.reverse(direct)
        assertEquals(origin, reverse)
        assertNotEquals(origin, direct)
    }

    fun testShort(size: Size, value:Int = 5) {
        val origin = Matrix.create(size) { i, j -> value.toShort() }
        val direct = EnlargementUtils.directShort(origin)
        val reverse = EnlargementUtils.reverseShort(direct)
        assertEquals(origin, reverse)
        assertNotEquals(origin, direct)
    }
}