package data_model.types

import data_model.generics.matrix.Matrix
import java.math.BigInteger

sealed class DataOpc(
        val base: ShortArray,
        val sign: Matrix<Boolean>,
        val AC: Short
) {
    class BI(
            val N: BigInteger,
            base: ShortArray,
            sign: Matrix<Boolean>,
            AC: Short
    ) : DataOpc(base, sign, AC)

    class Long(
            val vectorCode: List<kotlin.Long>,
            base: ShortArray,
            sign: Matrix<Boolean>,
            AC: Short
    ) : DataOpc(base, sign, AC)

    data class Builder(
            var base: ShortArray,
            var sign: Matrix<Boolean>,
            var AC: Short
    ) {

        constructor(size: Size) : this(
                ShortArray(size.height) { 1 },
                Matrix.create(size) { _, _ -> false },
                0
        )

        constructor(dataOpc: DataOpc) : this(
                dataOpc.base,
                dataOpc.sign,
                dataOpc.AC
        )

        fun build(N: BigInteger) = BI(N, base, sign, AC)

        fun build(vectorCode: List<kotlin.Long>) = Long(vectorCode, base, sign, AC)
    }
}