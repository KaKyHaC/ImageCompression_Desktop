package ImageCompressionLib.Utils.Objects

import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

object CompressionUtils : IUtils<ByteArray, ByteArray> {

    override fun direct(data: ByteArray): ByteArray {
        val deflater = Deflater()
        deflater.setInput(data)

        val outputStream = ByteArrayOutputStream(data.size)

        deflater.finish()
        val buffer = ByteArray(1024)
        while (!deflater.finished()) {
            val count = deflater.deflate(buffer) // returns the generated code... index
            outputStream.write(buffer, 0, count)
        }
        outputStream.close()
        val output = outputStream.toByteArray()

        deflater.end()

        println("Original: " + data.size / 1024 + " Kb")
        println("Compressed: " + output.size / 1024 + " Kb")
        return output
    }

    override fun reverse(data: ByteArray): ByteArray {
        val inflater = Inflater()
        inflater.setInput(data)

        val outputStream = ByteArrayOutputStream(data.size)
        val buffer = ByteArray(1024)
        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }
        outputStream.close()
        val output = outputStream.toByteArray()

        inflater.end()

        println("Original: " + data.size / 1024 + " Kb")
        println("Compressed: " + output.size / 1024 + " Kb")
        return output
    }
}
