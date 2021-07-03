package data_model.types

import java.math.BigInteger

sealed class DataOpc(
        val base: ShortArray,
        val sign: Array<BooleanArray>,
        val DC: Short,
        val size: Size
) {
    class BI(
            val N: BigInteger,
            base: ShortArray,
            sign: Array<BooleanArray>,
            DC: Short,
            size: Size
    ) : DataOpc(base, sign, DC, size)

    class Long(
            val vectorCode: List<kotlin.Long>,
            base: ShortArray,
            sign: Array<BooleanArray>,
            DC: Short,
            size: Size
    ) : DataOpc(base, sign, DC, size)

    data class Builder(
            var base: ShortArray,
            var sign: Array<BooleanArray>,
            var DC: Short,
            var size: Size
    ) {
        fun build(N: BigInteger) = BI(N, base, sign, DC, size)

        fun build(vectorCode: List<kotlin.Long>) = Long(vectorCode, base, sign, DC, size)
    }
}