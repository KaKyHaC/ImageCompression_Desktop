package ImageCompressionLib.Containers

import ImageCompressionLib.Constants.SIZE_OF_BLOCK
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.Size

class Parameters {
    val flag: Flag
    val imageSize: Size
    val unitSize: Size
    val sameBaseSize: Size
    val dataSize: Size
    val matrixOfUnitSize: Size

    constructor(
        flag: Flag,
        imageSize: Size,
        unitSize: Size = Size(SIZE_OF_BLOCK, SIZE_OF_BLOCK),
        sameBaseSize: Size = Size(1, 1)
    ) {
        this.flag = flag
        this.imageSize = imageSize
        this.unitSize = unitSize
        this.sameBaseSize = sameBaseSize
        dataSize = calculateDataSize(imageSize, unitSize)
        matrixOfUnitSize = calculateMatrixOfUnitSize(dataSize, unitSize)
    }

    fun calculateDataSize(imageSize: Size, unitSize: Size): Size {
        var w = 0
        var h = 0

        if (imageSize.width % unitSize.width != 0)
            w = unitSize.width - imageSize.width % unitSize.width
        if (imageSize.height % unitSize.height != 0)
            h = unitSize.height - imageSize.height % unitSize.height

        return Size(imageSize.width + w, imageSize.height + h)
    }

    fun calculateMatrixOfUnitSize(dataSize: Size, unitSize: Size): Size {
        var w = dataSize.width / unitSize.width
        var h = dataSize.height / unitSize.height
        if (dataSize.width % unitSize.width != 0) w++
        if (dataSize.height % unitSize.height != 0) h++
        return Size(w, h)
    }

    fun toByteVector(vector: ByteVector) {
        vector.append(flag.flag)
        vector.append(imageSize.width.toShort())
        vector.append(imageSize.height.toShort())
        vector.append(unitSize.width.toShort())
        vector.append(unitSize.height.toShort())
        if (flag.isChecked(Flag.Parameter.GlobalBase)) {
            vector.append(sameBaseSize.width.toShort())
            vector.append(sameBaseSize.height.toShort())
        }
    }

    override fun toString(): String {
        return "Parameters(flag=$flag, imageSize=$imageSize, unitSize=$unitSize, sameBaseSize=$sameBaseSize)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Parameters) return false

        if (flag != other.flag) return false
        if (imageSize != other.imageSize) return false
        if (unitSize != other.unitSize) return false
        if (sameBaseSize != other.sameBaseSize) return false
        if (dataSize != other.dataSize) return false
        if (matrixOfUnitSize != other.matrixOfUnitSize) return false

        return true
    }

    override fun hashCode(): Int {
        var result = flag.hashCode()
        result = 31 * result + imageSize.hashCode()
        result = 31 * result + unitSize.hashCode()
        result = 31 * result + sameBaseSize.hashCode()
        result = 31 * result + dataSize.hashCode()
        result = 31 * result + matrixOfUnitSize.hashCode()
        return result
    }


    companion object {
        @JvmStatic
        fun fromByteVector(vector: ByteVector): Parameters {
            val flag = Flag(vector.getNextShort())
            val imageSize = Size(vector.getNextShort().toInt(), vector.getNextShort().toInt())
            val unitSize = Size(vector.getNextShort().toInt(), vector.getNextShort().toInt())
            var sameBaseSize = Size(1, 1)
            if (flag.isChecked(Flag.Parameter.GlobalBase)) {
                sameBaseSize = Size(vector.getNextShort().toInt(), vector.getNextShort().toInt())
            }
            return Parameters(flag, imageSize, unitSize, sameBaseSize)
        }

        @Deprecated("only for tests")
        @JvmStatic
        fun createParametresForTest(
            imageSize: Size,
            unitSize: Size = Size(SIZE_OF_BLOCK, SIZE_OF_BLOCK),
            flag: Flag = Flag.createDefaultFlag()
        ): Parameters {
            val s2 = Size(1, 1)
            return Parameters(flag, imageSize, unitSize, s2)
        }
    }
}