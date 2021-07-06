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
        fun makeUnSigned(dataOrigin: Matrix<Short>, dataOpc: DataOpc.Builder) {
            dataOrigin.applyEach { i, j, value ->
                if (value < 0) {
                    dataOpc.sign[i, j] = false
                    (value * -1).toShort()
                } else {
                    dataOpc.sign[i, j] = true
                    null
                }
            }
        }

        @JvmStatic
        fun makeSigned(dataOrigin: Matrix<Short>, dataOpc: DataOpc) {
            dataOrigin.applyEach { i, j, value ->
                if (dataOpc.sign[i, j].not()) (value * -1).toShort() else null
            }
        }
    }

    object AC {
        @JvmStatic
        fun minus(dataOrigin: Matrix<Short>, dataOpc: DataOpc.Builder) {
            dataOpc.AC = dataOrigin[0, 0]
            dataOrigin[0, 0] = 0
        }

        @JvmStatic
        fun plus(dataOrigin: Matrix<Short>, dataOpc: DataOpc) {
            dataOrigin[0, 0] = dataOpc.AC
        }
    }

    object Data {

        @JvmStatic
        fun makeDataOdd(dataOrigin: Matrix<Short>) {
            dataOrigin.applyEach { i, j, value ->
                if (value % 2 == 0) (value + 1).toShort() else null
            }
        }

        fun createImageMatrix(dataOpcMatrixSize: Size, childSize: Size): Matrix<Short> {
            val width = dataOpcMatrixSize.width * childSize.width
            val height = dataOpcMatrixSize.height * childSize.height
            val imageSize = Size(width, height)
            return Matrix.create(imageSize) { _, _ -> 0.toShort() }
        }
    }
}