package features.opc2.utils

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc
import data_model.types.DataOpc2
import data_model.types.Size

object DataOpcUtils2 {

    object Base {
        @JvmStatic
        fun findMaxBase(dataOrigin: Matrix<Short>, dataOpc: DataOpc2.Builder) {
            val base = ShortArray(dataOrigin.height)
            dataOrigin.applyEach { i, j, value ->
                if (base[j] <= value) {
                    base[j] = (value + 1).toShort()
                }
                null
            }
            dataOpc.baseMax = base
        }


        fun findMinBase(dataOrigin: Matrix<Short>, dataOpc: DataOpc2.Builder) {
            val base = ShortArray(dataOrigin.height) { 256 }
            dataOrigin.applyEach { i, j, value ->
                if (base[j] >= value) {
                    base[j] = value
                }
                null
            }
            dataOpc.baseMin = base
        }

        fun removeMinBase(dataOrigin: Matrix<Short>, dataOpc: DataOpc2.Builder) {
            dataOrigin.applyEach { i, j, value ->
                dataOpc.baseMin?.get(j)?.let { (value - it).toShort() } ?: value
            }
        }

        fun addMinBase(dataOrigin: Matrix<Short>, dataOpc: DataOpc2.Builder) {
            dataOrigin.applyEach { i, j, value ->
                dataOpc.baseMin?.get(j)?.let { (value + it).toShort() } ?: value
            }
        }

        @JvmStatic
        fun setSameBaseIn(dataOrigin: Matrix<DataOpc.Builder>) {
            val baseSize = dataOrigin[0, 0].base.size
            val res = ShortArray(baseSize)
            for (b in 0 until baseSize) {
                dataOrigin.applyEach { i, j, value ->
                    if (dataOrigin[i, j].base[b] > res[b]) {
                        res[b] = dataOrigin[i, j].base[b]
                    }
                    null
                }
            }
            for (i in 0 until dataOrigin.width) {
                for (j in 0 until dataOrigin.height) {
                    dataOrigin[i, j].base = res
                }
            }
        }

        @JvmStatic
        fun makeBaseEven(dataOpc: DataOpc.Builder) {
            for (i in 0 until dataOpc.base.size)
                if (dataOpc.base[i] % 2 != 0)
                    dataOpc.base[i]++
        }

        fun increaseBase(dataOpc: DataOpc2.Builder) {
            dataOpc.baseMax?.let { for (i in it.indices) it[i]++ }
        }

        fun decreaseBase(dataOpc: DataOpc2.Builder) {
            dataOpc.baseMax?.let { for (i in it.indices) it[i]-- }
        }
    }

    object Sign {
        @JvmStatic
        fun makeUnSigned(dataOrigin: Matrix<Short>, dataOpc: DataOpc2.Builder) {
            val sign = Matrix.create(dataOrigin.size) { i, j -> false }
            dataOrigin.applyEach { i, j, value ->
                if (value < 0) {
                    sign[i, j] = false
                    (value * -1).toShort()
                } else {
                    sign[i, j] = true
                    null
                }
            }
            dataOpc.sign = sign
        }

        @JvmStatic
        fun makeSigned(dataOrigin: Matrix<Short>, dataOpc: DataOpc2.Builder) {
            dataOpc.sign?.applyEach { i, j, value ->
                if (value.not()) dataOrigin[i, j] = (dataOrigin[i, j] * -1).toShort()
                null
            }
        }
    }

    object AC {
        @JvmStatic
        fun minus(dataOrigin: Matrix<Short>, dataOpc: DataOpc2.Builder) {
            dataOpc.AC = dataOrigin[0, 0]
            dataOrigin[0, 0] = 0
        }

        @JvmStatic
        fun plus(dataOrigin: Matrix<Short>, dataOpc: DataOpc2.Builder) {
            dataOpc.AC?.let { dataOrigin[0, 0] = it }
        }
    }

    object Data {

        @JvmStatic
        fun makeDataOdd(dataOrigin: Matrix<Short>) {
            dataOrigin.applyEach { i, j, value ->
                if (value % 2 == 0) (value + 1).toShort() else null
            }
        }
    }
}