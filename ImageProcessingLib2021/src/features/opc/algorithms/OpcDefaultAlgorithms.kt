package features.opc.algorithms

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc
import data_model.types.Size
import java.math.BigInteger

object OpcDefaultAlgorithms {
    @JvmStatic
    fun direct(dataOrigin: Matrix<Short>, dataOpc: DataOpc.Builder): DataOpc.BI {//TODO diagonal for optimization
        var base = BigInteger.ONE
        var N = BigInteger.ZERO
        for (i in dataOrigin.width - 1 downTo 0) {
            for (j in dataOrigin.height - 1 downTo 0) {
                if (dataOrigin[i, j].toInt() != 0) {
                    N = N.add(base.multiply(BigInteger.valueOf(dataOrigin[i, j].toLong())));
                }
                base = base.multiply(BigInteger.valueOf(dataOpc.base[j].toLong()));
            }
        }
        return dataOpc.build(N)
    }

    fun applyReverse(dataOrigin: Matrix<Short>, dataOpc: DataOpc.BI): Matrix<Short> {// method copy from C++ Project MAH
        var copy = BigInteger.ONE
        var b: BigInteger
        for (i in dataOrigin.width - 1 downTo 0) {
            for (j in dataOrigin.height - 1 downTo 0) {
                val a = dataOpc.N.divide(copy)
                val baseL = dataOpc.base[j].toLong()
                copy = copy.multiply(BigInteger.valueOf(baseL))
                b = dataOpc.N.divide(copy).multiply(BigInteger.valueOf(baseL))
                dataOrigin[i, j] = a.subtract(b).toShort()
            }
        }
        return dataOrigin
    }

    @JvmStatic
    fun reverse(size: Size, dataOpc: DataOpc.BI): Matrix<Short> {
        val dataOrigin = Matrix.create(size) { _, _ -> 0.toShort() }
        return applyReverse(dataOrigin, dataOpc)
    }
}