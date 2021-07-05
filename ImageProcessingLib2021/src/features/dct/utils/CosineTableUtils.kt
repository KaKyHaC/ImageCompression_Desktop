package features.dct.utils

import data_model.generics.matrix.Matrix
import data_model.types.Size

/**
 * Created by Димка on 07.08.2016.
 */
class CosineTableUtils(
        val SIZEOFBLOCK: Int = 8
) {


    private val cosine by lazy {
        Matrix.create(Size(SIZEOFBLOCK, SIZEOFBLOCK)) { i, j ->
            val data = (2.0 * i + 1.0) * j * Math.PI / (2 * SIZEOFBLOCK)
            Math.cos(data) //toRadiance
        }
    }
    private val value by lazy {
        Matrix.create(Size(SIZEOFBLOCK, SIZEOFBLOCK)) { i, j ->
            Matrix.create(Size(SIZEOFBLOCK, SIZEOFBLOCK)) { x, y ->
                cosine[y, j] * cosine[x, i]
            }
        }
    }
    private val DCTres by lazy {
        Matrix.create(Size(2, 2)) { i, j ->
            val Ci = if (i == 0) 1.0 / Math.sqrt(2.0) else 1.0
            val Cj = if (j == 0) 1.0 / Math.sqrt(2.0) else 1.0
            1.0 / Math.sqrt(2.0 * SIZEOFBLOCK) * Ci * Cj
        }
    }

    fun getCos(i: Int, j: Int) = cosine[i, j]

    fun getCos(x: Int, y: Int, i: Int, j: Int) = value[i, j][x, y]

    fun getDCTres(i: Int, j: Int) = DCTres[if (i != 0) 1 else 0, if (j != 0) 1 else 0]
}