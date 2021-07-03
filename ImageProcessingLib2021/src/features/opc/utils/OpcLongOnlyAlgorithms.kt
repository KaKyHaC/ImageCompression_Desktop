package features.opc.utils

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc
import utils.MatrixFactory
import java.util.*

object OpcLongOnlyAlgorithms {

    val MAX_LONG= Math.pow(2.0, 54.0).toLong()

    @JvmStatic
    fun direct(dataOrigin: Matrix<Short>, dataOpc: DataOpc.Builder): DataOpc.Long {
        var base: Long = 1
        var res: Long = 0
        var bufBase: Long
        val vectorCode = Vector<Long>()
        for (i in dataOrigin.width - 1 downTo 0) {
            for (j in dataOrigin.height - 1 downTo 0) {
                if (dataOrigin.height != dataOpc.base.size)
                    throw Exception("out of range")
                bufBase = base * dataOpc.base[j]
                if (bufBase > MAX_LONG) {
                    vectorCode.add(res)
                    base = 1
                    res = 0
                    bufBase = base * dataOpc.base[j]
                }
                if (dataOrigin[i, j].toInt() != 0) {
                    res += base * dataOrigin[i, j]
                }
                base = bufBase
            }
        }
        vectorCode.add(res)
        return dataOpc.build(vectorCode)
    }

    @JvmStatic
    fun reverse(dataOpc: DataOpc.Long): Matrix<Short> {
        val dataOrigin: Matrix<Short> = MatrixFactory.createShortMatrix(dataOpc.size)
        var copy: Long = 1
        var index = 0
        var curN = dataOpc.vectorCode.elementAt(index)
        var nextcopy: Long
        for (i in dataOrigin.width - 1 downTo 0) {
            for (j in dataOrigin.height - 1 downTo 0) {
                nextcopy = copy * dataOpc.base[j]
                if (nextcopy > MAX_LONG || nextcopy < 0) {
                    copy = 1
                    index++
                    nextcopy = copy * dataOpc.base[j]
                    if (index < dataOpc.vectorCode.size)
                        curN = dataOpc.vectorCode.elementAt(index)
                }
                val a: Long
                var b: Long

                a = curN / copy
                copy = nextcopy

                b = curN / copy
                b = b * dataOpc.base[j]
                dataOrigin[i, j] = (a - b).toShort()
            }
        }
        return dataOrigin
    }
}