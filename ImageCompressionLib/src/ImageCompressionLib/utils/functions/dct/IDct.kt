package ImageCompressionLib.utils.functions.dct

import ImageCompressionLib.containers.matrix.Matrix

interface IDct {
    fun directDCT(data: Matrix<Short>): Matrix<Short>
    fun reverceDCT(data: Matrix<Short>): Matrix<Short>
}