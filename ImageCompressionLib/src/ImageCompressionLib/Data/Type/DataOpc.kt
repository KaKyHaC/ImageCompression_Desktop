package ImageCompressionLib.Data.Type

import ImageCompressionLib.Data.Interfaces.IByteVector
import ImageCompressionLib.Data.Interfaces.ICopyable
import ImageCompressionLib.Data.Interfaces.ISavable
import java.math.BigInteger

data class DataOpc(
    val DC: Short?,
    val N: BigInteger,
    val base: ShortArray,
    val signMatrix: Array<BooleanArray>
) : ICopyable, ISavable {

    override fun copy() = builder().build()

    override fun appendByteVector(vector: IByteVector) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun builder() = Builder(
        base = base,
        signMatrix = signMatrix,
        DC = DC,
        N = N
    )

    data class Builder(
        var base: ShortArray? = null,
        var signMatrix: Array<BooleanArray>? = null,
        var DC: Short? = null,
        var N: BigInteger? = null
    ) {
        fun build() = DataOpc(
            DC = DC,
            N = N!!,
            signMatrix = signMatrix!!,
            base = base!!
        )
    }
}