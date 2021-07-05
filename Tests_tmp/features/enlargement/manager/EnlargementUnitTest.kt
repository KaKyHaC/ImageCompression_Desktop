package features.enlargement.manager

import data_model.generics.matrix.Matrix
import data_model.types.Size
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class EnlargementUnitTest {

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

    fun testInt(size: Size, value:Int = 5) {
        val enlargementUnit = EnlargementUnit()
        val origin = Matrix.create(size) { i, j -> value }
        val direct = enlargementUnit.direct(origin)
        val reverse = enlargementUnit.reverse(direct)
        assertEquals(origin, reverse)
        assertNotEquals(origin, direct)
    }
}