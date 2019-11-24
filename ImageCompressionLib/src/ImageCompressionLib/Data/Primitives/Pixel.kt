package ImageCompressionLib.Data.Primitives

import ImageCompressionLib.Data.Interfaces.IByteVector
import ImageCompressionLib.Data.Interfaces.ICopyable
import ImageCompressionLib.Data.Interfaces.IDeviationable
import ImageCompressionLib.Data.Interfaces.ISavable

abstract class Pixel private constructor(
    val dataList: List<Int>
) : ICopyable, ISavable, IDeviationable<Pixel> {

    data class RGB(
        val r: Int,
        val g: Int,
        val b: Int
    ) : Pixel(listOf(r, g, b)) {
        override fun copy() = RGB(r, g, b)
    }

    data class YCbCr(
        val y: Int,
        val cb: Int,
        val cr: Int
    ) : Pixel(listOf(y, cb, cr)) {
        override fun copy() = YCbCr(y, cb, cr)
    }

    override fun appendByteVector(vector: IByteVector) {
        dataList.forEach { vector.append(it) }
    }

    override fun calculateDeviation(other: Pixel) = dataList
        .mapIndexed { index, i -> i - other.dataList[index] }
        .reduce { acc, i -> acc + i }
        .div(3.0)
}