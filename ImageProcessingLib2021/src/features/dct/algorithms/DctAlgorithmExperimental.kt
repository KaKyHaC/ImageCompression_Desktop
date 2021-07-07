package features.dct.algorithms

import data_model.generics.matrix.Matrix
import data_model.types.Size
import utils.MatrixUtils
import kotlin.reflect.KClass

class DctAlgorithmExperimental(
        val dct: Matrix<Float>,
        val dctT: Matrix<Float>
) {

    fun direct(origin: Matrix<Float>): Matrix<Float> {
        val in_dct = MatrixUtils.mulMat(dct, origin)
        val in_dct_dctT = MatrixUtils.mulMat(in_dct, dctT)
        return in_dct_dctT
    }

    inline fun <reified T : Number, reified OUT : Number> direct(origin: Matrix<T>, mapper: (Double) -> OUT): Matrix<OUT> {
        val in_dct = MatrixUtils.mulMat(dct, origin, { it })
        val in_dct_dctT = MatrixUtils.mulMat(in_dct, dctT, mapper)
        return in_dct_dctT
    }

    fun reverse(dctMatrix: Matrix<Float>): Matrix<Float> {
        val in_dctT = MatrixUtils.mulMat(dctT, dctMatrix)
        val in_dctT_dct = MatrixUtils.mulMat(in_dctT, dct)
        return in_dctT_dct
    }

    inline fun <reified T : Number, reified OUT : Number> reverse(dctMatrix: Matrix<T>, mapper: (Double) -> OUT): Matrix<OUT> {
        val in_dctT = MatrixUtils.mulMat(dctT, dctMatrix, { it })
        val in_dctT_dct = MatrixUtils.mulMat(in_dctT, dct, mapper)
        return in_dctT_dct
    }

}
