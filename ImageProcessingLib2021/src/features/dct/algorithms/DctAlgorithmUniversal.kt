package features.dct.algorithms

import data_model.generics.matrix.Matrix
import data_model.types.Size
import features.dct.table.DctCoefficientUtil
import features.dct.table.DctCosineUtils


class DctAlgorithmUniversal(val unitSize: Size) {

    private val cosineTable = DctCosineUtils(unitSize)
    private val coefficient = DctCoefficientUtil(unitSize)

    fun direct(data: Matrix<Short>): Matrix<Short> {
        val dataRes = Matrix.create(data.size) { i, j -> 0.toShort() }
        return directDct(data, dataRes)
    }

    fun reverse(data: Matrix<Short>): Matrix<Short> {
        val dataRes = Matrix.create(data.size) { i, j -> 0.toShort() }
        return reverseDct(data, dataRes)
    }

    private fun directDct(data: Matrix<Short>, target: Matrix<Short>): Matrix<Short> {
        val w = unitSize.width
        val h = unitSize.height
        for (p in 0 until w) {
            for (q in 0 until h) {
                var sum = 0.0
                for (m in 0 until w) {
                    for (n in 0 until h) {
                        sum += data[m, n] * cosineTable[p, q, m, n]
                    }
                }
                target[p, q] = (coefficient[p, q] * sum).toShort()
            }
        }
        return target
    }

    private fun reverseDct(data: Matrix<Short>, target: Matrix<Short>): Matrix<Short> {
        val w = unitSize.width
        val h = unitSize.height
        for (m in 0 until w) {
            for (n in 0 until h) {
                var sum = 0.0
                for (p in 0 until w) {
                    for (q in 0 until h) {
                        sum += coefficient[p, q] * data[p, q] * cosineTable[p, q, m, n]
                    }
                }
                target[m, n] = sum.toShort()
            }
        }
        return target
    }
}
