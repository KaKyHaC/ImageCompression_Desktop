package data_model.types

import data_model.generics.matrix.Matrix
import java.math.BigInteger

class DataOpc2(
        val base: Base,
        val sign: Matrix<Boolean>?,
        val AC: Short?,
        val code: Code
) {
    sealed class Base(val baseMax: ShortArray) {
        class Max(baseMax: ShortArray) : Base(baseMax)
        class MaxMin(baseMax: ShortArray, val baseMin: ShortArray) : Base(baseMax)
    }

    sealed class Code {
        data class BI(val N: BigInteger) : Code()
        data class L(val vectorCode: List<Long>) : Code()
    }

    data class Builder(
            var baseMin: ShortArray? = null,
            var baseMax: ShortArray? = null,
            var sign: Matrix<Boolean>? = null,
            var AC: Short? = null,
            var N: BigInteger? = null,
            var vectorCode: List<Long>? = null
    ) {

        constructor(dataOpc: DataOpc2) : this(
                baseMax = dataOpc.base.baseMax,
                baseMin = (dataOpc.base as? Base.MaxMin)?.baseMin,
                sign = dataOpc.sign,
                AC = dataOpc.AC,
                N = (dataOpc.code as? Code.BI)?.N,
                vectorCode = (dataOpc.code as? Code.L)?.vectorCode
        )

        fun build() = DataOpc2(
                base = baseMin?.let { Base.MaxMin(baseMax!!, it) } ?: Base.Max(baseMax!!),
                AC = AC,
                sign = sign,
                code = N?.let { Code.BI(it) } ?: vectorCode?.let { Code.L(it) }!!
        )
    }
}