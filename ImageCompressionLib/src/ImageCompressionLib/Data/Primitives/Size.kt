package ImageCompressionLib.Data.Primitives

import ImageCompressionLib.Data.Interfaces.IByteVector
import ImageCompressionLib.Data.Interfaces.ISavable

data class Size(val width: Int, val height: Int) : ISavable{

    constructor(vector: IByteVector) : this(
        vector.getNextShort()?.toInt() ?: 0,
        vector.getNextShort()?.toInt() ?: 0
    )
    override fun appendByteVector(vector: IByteVector) {
        vector.append(width.toShort())
        vector.append(height.toShort())
    }
}
