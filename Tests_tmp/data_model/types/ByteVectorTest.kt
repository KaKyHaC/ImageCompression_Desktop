package data_model.types

import data_model.generics.matrix.Matrix
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals


internal class ByteVectorTest {

    val rand = Random()

    @Test
    fun customTest() {
        val byteVector = ByteVector()
        byteVector.putInt(4)
        byteVector.putByte(32)
        byteVector.putLong(212)
        byteVector.putShort(342)
        print(byteVector)
        val reader = byteVector.getReader()
        assertEquals(reader.nextInt(), 4)
        assertEquals(reader.nextByte(),32)
        assertEquals(reader.nextLong(), 212)
        assertEquals(reader.nextShort(), 342)
    }

    @Test
    fun intTest() {
        val byteVector = ByteVector()
        byteVector.putInt(4)
        byteVector.putInt(123)
        print(byteVector)
        val reader = byteVector.getReader()
        assertEquals(reader.nextInt(), 4)
        assertEquals(reader.nextInt(), 123)
    }

    @Test
    fun test100(){
        testMatrix(Size(100))
    }

    fun testMatrix(size: Size) {
        val create = Matrix.create(size) { i, j -> rand.nextInt() }
        val byteVector = ByteVector()
        create.applyEach{i, j, value -> byteVector.putInt(value); null }
        val wrap = byteVector.getReader()
        val create1 = Matrix.create(size) { i, j -> wrap.nextInt() }
        assertEquals(create,create1)
    }
}