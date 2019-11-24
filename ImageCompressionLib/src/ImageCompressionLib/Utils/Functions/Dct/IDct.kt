package ImageCompressionLib.Utils.Functions.Dct

import ImageCompressionLib.Data.Matrix.Matrix

interface IDct {
    fun directDCT(data: Matrix<Short>): Matrix<Short>
    fun reverceDCT(data: Matrix<Short>): Matrix<Short>
}