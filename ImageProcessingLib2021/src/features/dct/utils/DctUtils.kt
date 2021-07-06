package features.dct.utils

import data_model.generics.matrix.Matrix

object DctUtils {

    /**
     * subtract the [0][0] element from each [%8][%8]
     */
    fun preProcess(origin: Matrix<Matrix<Short>>) =
            origin.applyEach { i, j, value ->
                if (i != 0 && j != 0) {
                    value[0, 0] = (origin[0, 0][0, 0] - value[0, 0]).toShort()
                }
                null
            }


    fun minus128(arr: Matrix<Short>) =
            arr.applyEach { i, j, value -> (value - 128).toShort() }


    fun plus128(arr: Matrix<Short>) =
            arr.applyEach { i, j, value -> (value + 128).toShort() }

}