package features.dct.table

import data_model.generics.matrix.Matrix
import data_model.types.Size


class DctCoefficientUtil(unitSize: Size) {
    private val coefficients: Matrix<Double>

    init {
        val OneDivideMathsqrtW = 1.0 / Math.sqrt(unitSize.width.toDouble())
        val OneDivideMathsqrtH = 1.0 / Math.sqrt(unitSize.height.toDouble())
        val TwoDivideMathsqrtW = Math.sqrt(2.0 / unitSize.width.toDouble())
        val TwoDivideMathsqrtH = Math.sqrt(2.0 / unitSize.width.toDouble())

        coefficients = Matrix.create(Size(2, 2)) { i, j ->
            val ap = if (i == 0) OneDivideMathsqrtW else TwoDivideMathsqrtW
            val aq = if (j == 0) OneDivideMathsqrtH else TwoDivideMathsqrtH
            ap * aq
        }
    }


    operator fun get(p: Int, q: Int): Double {
        val tp = if (p > 0) 1 else p
        val tq = if (q > 0) 1 else q
        return coefficients[tp, tq]
    }

}