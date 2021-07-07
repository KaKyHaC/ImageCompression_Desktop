package data_model.types

import org.junit.Test
import kotlin.test.assertEquals


internal class ByteVectorTest {

    @Test
    fun customTest() {
        val byteVector = ByteVector()
        byteVector.putInt(4)
        byteVector.putByte(32)
        byteVector.putLong(212)
        byteVector.putShort(342)
        val reader = byteVector.getReader()
        assertEquals(reader.getInt(), 4)
        assertEquals(reader.getByte(),32)
        assertEquals(reader.getLong(), 212)
        assertEquals(reader.getShort(), 342)
    }
}