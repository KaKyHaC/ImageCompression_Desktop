package ImageCompressionLib.Constants

import java.math.BigInteger

object Constants {
    const val SIZE_OF_BLOCK = 8;
    const val BITS_PER_BYTE = 8
    const val BYTES_PER_LONG = 8
    const val ONE_LEFT_BIT = 0b10000000
    val BI_TWO = BigInteger.valueOf(2)
    var MAX_LONG = Math.pow(2.0, 54.0).toLong()
    var BINARY_VALUE_OF_POSITION = 1
    var DEFAULT_PASSWORD = " "
    val MAX_QUANTIZATION_VALUE = 55
    val DEFAULT_MATRIX_VALUE = 128.toShort()
}
