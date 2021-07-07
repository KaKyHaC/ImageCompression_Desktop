package data_model.types

import data_model.generics.matrix.Matrix
import org.junit.Test
import java.nio.ByteBuffer
import java.util.*
import kotlin.test.assertEquals


internal class ByteBufferTest {
    val rand = Random()

    @Test
    fun test1() {
        testMatrix(Size(8))
    }
    fun testMatrix(size: Size) {
        val create = Matrix.create(size) { i, j -> rand.nextInt() }
        val buffer = ByteBuffer.allocate(1000000)
        create.applyEach{i, j, value -> buffer.putInt(value); null }
        val wrap = ByteBuffer.wrap(buffer.array())
        val create1 = Matrix.create(size) { i, j -> wrap.int }
        assertEquals(create,create1)
    }
}