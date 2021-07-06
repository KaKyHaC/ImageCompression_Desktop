package features.dct.utils

import data_model.generics.matrix.Matrix
import data_model.types.Size
import utils.MatrixUtils

@UseExperimental
class ExperimentalCosineTable(
        val bsf: Int = 8
) {

    val dct by lazy {
        val tmp = Matrix.create(Size(bsf, bsf)) { x, y ->
            val d = Math.sqrt((2.0 / bsf)) * Math.cos((2.0 * x + 1) * y * Math.PI / (2 * bsf))
            d.toFloat()
        }
        for (y in 0 until bsf) {
            val cc = 1 / Math.sqrt(bsf.toDouble())
            tmp[0, y] = cc.toFloat()
        }
        tmp
    }
    val dctT by lazy {
        val tmp = MatrixUtils.trans(dct)
    }
}