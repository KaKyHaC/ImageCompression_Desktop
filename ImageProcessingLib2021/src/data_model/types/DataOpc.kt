package data_model.types

import java.math.BigInteger

sealed class DataOpc(
        val base: ShortArray,
        val sign: Array<BooleanArray>,
        val AC: Short
) {
    class BI(
            val N: BigInteger,
            base: ShortArray,
            sign: Array<BooleanArray>,
            AC: Short
    ) : DataOpc(base, sign, AC)

    class Long(
            val vectorCode: List<kotlin.Long>,
            base: ShortArray,
            sign: Array<BooleanArray>,
            AC: Short
    ) : DataOpc(base, sign, AC)

    data class Builder(
            val base: ShortArray,
            val sign: Array<BooleanArray>,
            val AC: Short
    ) {

        constructor(size: Size) : this(
                ShortArray(size.height) { 1 },
                Array(size.width) { BooleanArray(size.height) },
                0
        )

        fun build(N: BigInteger) = BI(N, base, sign, AC)

        fun build(vectorCode: List<kotlin.Long>) = Long(vectorCode, base, sign, AC)
    }
}