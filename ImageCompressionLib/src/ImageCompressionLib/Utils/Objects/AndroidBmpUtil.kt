package ImageCompressionLib.Utils.Objects


//import sun.rmi.runtime.Log;

import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

object AndroidBmpUtil {

    private val BMP_WIDTH_OF_TIMES = 4
    private val BYTE_PER_PIXEL = 3

    interface Bitmap {
        val width: Int
        val height: Int
        fun getPixels(pixels: IntArray, param1: Int, paramWidth: Int, param2: Int, param3: Int, width: Int, height: Int)
    }

    /**
     * Android Bitmap Object to Window's v3 24bit Bmp Format File
     * @param orgBitmap
     * @param filePath
     * @return file saved result
     */
    @Throws(IOException::class)
    fun save(orgBitmap: Bitmap?, filePath: String?): Boolean {
        val start = System.currentTimeMillis()
        if (orgBitmap == null) {
            return false
        }

        if (filePath == null) {
            return false
        }

        val isSaveSuccess = true

        //image size
        val width = orgBitmap.width
        val height = orgBitmap.height

        //image dummy data size
        //reason : the amount of bytes per image row must be a multiple of 4 (requirements of bmp format)
        var dummyBytesPerRow: ByteArray? = null
        var hasDummy = false
        val rowWidthInBytes = BYTE_PER_PIXEL * width //source image width * number of bytes to encode one pixel.
        if (rowWidthInBytes % BMP_WIDTH_OF_TIMES > 0) {
            hasDummy = true
            //the number of dummy bytes we need to add on each row
            dummyBytesPerRow = ByteArray(BMP_WIDTH_OF_TIMES - rowWidthInBytes % BMP_WIDTH_OF_TIMES)
            //just fill an array with the dummy bytes we need to append at the end of each row
            for (i in dummyBytesPerRow.indices) {
                dummyBytesPerRow[i] = 0xFF.toByte()
            }
        }

        //an array to receive the pixels from the source image
        val pixels = IntArray(width * height)

        //the number of bytes used in the file to store raw image data (excluding file headers)
        val imageSize = (rowWidthInBytes + if (hasDummy) dummyBytesPerRow!!.size else 0) * height
        //file headers size
        val imageDataOffset = 0x36

        //final size of the file
        val fileSize = imageSize + imageDataOffset

        //Android Bitmap Image Data
        orgBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        //ByteArrayOutputStream baos = new ByteArrayOutputStream(fileSize);
        val buffer = ByteBuffer.allocate(fileSize)

        /**
         * BITMAP FILE HEADER Write Start
         */
        buffer.put(0x42.toByte())
        buffer.put(0x4D.toByte())

        //size
        buffer.put(writeInt(fileSize))

        //reserved
        buffer.put(writeShort(0.toShort()))
        buffer.put(writeShort(0.toShort()))

        //image data start offset
        buffer.put(writeInt(imageDataOffset))

        /** BITMAP FILE HEADER Write End  */

        //*******************************************

        /** BITMAP INFO HEADER Write Start  */
        //size
        buffer.put(writeInt(0x28))

        //width, height
        //if we add 3 dummy bytes per row : it means we add a pixel (and the image width is modified.
        buffer.put(writeInt(width + if (hasDummy) if (dummyBytesPerRow!!.size == 3) 1 else 0 else 0))
        buffer.put(writeInt(height))

        //planes
        buffer.put(writeShort(1.toShort()))

        //bit count
        buffer.put(writeShort(24.toShort()))

        //bit compression
        buffer.put(writeInt(0))

        //image data size
        buffer.put(writeInt(imageSize))

        //horizontal resolution in pixels per meter
        buffer.put(writeInt(0))

        //vertical resolution in pixels per meter (unreliable)
        buffer.put(writeInt(0))

        buffer.put(writeInt(0))

        buffer.put(writeInt(0))

        /** BITMAP INFO HEADER Write End  */

        var row = height
        var startPosition = (row - 1) * width
        var endPosition = row * width
        while (row > 0) {
            for (i in startPosition until endPosition) {
                buffer.put((pixels[i] and 0x000000FF).toByte())
                buffer.put((pixels[i] and 0x0000FF00 shr 8).toByte())
                buffer.put((pixels[i] and 0x00FF0000 shr 16).toByte())
            }
            if (hasDummy) {
                buffer.put(dummyBytesPerRow!!)
            }
            row--
            endPosition = startPosition
            startPosition = startPosition - width
        }

        val fos = FileOutputStream(filePath)
        fos.write(buffer.array())
        fos.close()
        //        Log.v("AndroidBmpUtil" ,System.currentTimeMillis()-start+" ms");

        return isSaveSuccess
    }

    /**
     * Write integer to little-endian
     * @param value
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeInt(value: Int): ByteArray {
        val b = ByteArray(4)

        b[0] = (value and 0x000000FF).toByte()
        b[1] = (value and 0x0000FF00 shr 8).toByte()
        b[2] = (value and 0x00FF0000 shr 16).toByte()
        b[3] = (value and -0x1000000 shr 24).toByte()

        return b
    }

    /**
     * Write short to little-endian byte array
     * @param value
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeShort(value: Short): ByteArray {
        val b = ByteArray(2)

        b[0] = (value and 0x00FF).toByte()
        b[1] = (value and 0xFF00 shr 8).toByte()

        return b
    }
}
