package data_model.types

import data_model.generics.matrix.Matrix
import java.math.BigInteger

data class DataOpc2(
        val base: Base,
        val sign: Matrix<Boolean>?,
        val AC: Short?,
        val code: Code
) {
    sealed class Base(val baseMax: ShortArray) {
        class Max(baseMax: ShortArray) : Base(baseMax)
        class MaxMin(baseMax: ShortArray, val baseMin: ShortArray) : Base(baseMax)

        override fun equals(other: Any?): Boolean {
            (other as? Base)?.let {
                if (it.baseMax.size != baseMax.size) return false
                for (i in baseMax.indices)
                    if (it.baseMax[i] != baseMax[i]) return false
            } ?: return false
            return true
        }

        fun getLengthOfCode(unitSize: Size): Int {//TODO optimize this fun
            var bi = BigInteger("1")
            for (i in 0 until unitSize.width) {
                for (j in 0 until unitSize.height)
                    bi = bi.multiply(BigInteger.valueOf(baseMax[j].toLong()))
            }
            return bi.toByteArray().size
        }
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