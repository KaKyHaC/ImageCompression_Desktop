package features.write_bytes

import data_model.processing_data.ProcessingData
import data_model.types.ByteVector
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


internal class ModuleWriteBytesTest {

    val rand = Random()

    @Test
    fun customTest() {
        customTest(100)
    }

    fun customTest(len: Int = 100) {
        val byteVector = ByteVector()
        for (i in 0..len) byteVector.putInt(rand.nextInt())
        val bytes = ProcessingData.Bytes(byteVector)
        val moduleWriteBytes = ModuleWriteBytes(ModuleWriteBytes.Parameters(fileName = "customTest.txt"))

        val processDirectTyped = moduleWriteBytes.processDirectTyped(bytes)
        val processReverseTyped = moduleWriteBytes.processReverseTyped(processDirectTyped)
        assertEquals(bytes, processReverseTyped)
        byteVector.putInt(123)
        assertNotEquals(bytes, processReverseTyped)
    }
}