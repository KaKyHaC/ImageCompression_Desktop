package features.write_bytes

import data_model.processing_data.ProcessingData
import data_model.types.ByteVector
import features.AbsDataProcessor
import java.io.File
import java.util.*

class ModuleWriteBytes(
    val parameters: Parameters = Parameters()
) : AbsDataProcessor<ProcessingData.Bytes, ProcessingData.File>(
        ProcessingData.Bytes::class, ProcessingData.File::class
) {

    data class Parameters(
            val fileName: String = "image.bar",
            val useDataFile: Boolean = true
    )

    override fun processDirectTyped(data: ProcessingData.Bytes): ProcessingData.File {
        val file = File(parameters.fileName)
        file.createNewFile()
        file.setWritable(true)
        file.writeBytes(data.byteVector.getBytes())
        return ProcessingData.File(file)
    }

    override fun processReverseTyped(data: ProcessingData.File): ProcessingData.Bytes {
        val file = if (parameters.useDataFile) data.file else File(parameters.fileName)
        val readBytes = file.readBytes()
        val byteVector = ByteVector()
        readBytes.forEach { byteVector.putByte(it) }
        return ProcessingData.Bytes(byteVector)
    }
}