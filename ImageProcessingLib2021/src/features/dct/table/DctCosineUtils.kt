package features.dct.table

import data_model.generics.matrix.Matrix
import data_model.types.Size


class DctCosineUtils(unitSize: Size) {
    private val cosineTableMultiply: Matrix<Matrix<Double>>

    init {
        val cosinTableW = Matrix.create(unitSize) { m, p ->
            Math.cos((2.0 * m + 1.0) * p.toDouble() * Math.PI / (2 * unitSize.width))
        }
        val cosinTableH = Matrix.create(unitSize) { n, q ->
            Math.cos((2.0 * n + 1.0) * q.toDouble() * Math.PI / (2 * unitSize.height))
        }
        cosineTableMultiply = Matrix.create(unitSize) { p, q ->
            Matrix.create(unitSize) { m, n -> cosinTableW[m, p] * cosinTableH[n, q] }
        }
    }


    operator fun get(p: Int, q: Int, m: Int, n: Int): Double {
        return cosineTableMultiply[p, q][m, n]
    }
}