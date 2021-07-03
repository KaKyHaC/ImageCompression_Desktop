package ImageCompressionLib.Utils.Functions.Dct

import ImageCompressionLib.Containers.Matrix.Matrix

interface IDct {
    fun directDCT(data: Matrix<Short>): Matrix<Short>
    fun reverceDCT(data: Matrix<Short>): Matrix<Short>
}