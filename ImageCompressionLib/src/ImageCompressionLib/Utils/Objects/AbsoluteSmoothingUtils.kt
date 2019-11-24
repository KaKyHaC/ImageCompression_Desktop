package ImageCompressionLib.Utils.Objects

import ImageCompressionLib.Data.Interfaces.IMatrix

object AbsoluteSmoothingUtils : IUtils<IMatrix<Short>, IMatrix<Short>> {
    private const val SMOOTHING_VALUE = 128

    override fun direct(input: IMatrix<Short>) =
        input.mapIndexed { i, j, value -> (value - SMOOTHING_VALUE).toShort() }

    override fun reverse(output: IMatrix<Short>) =
        output.mapIndexed { i, j, value -> (value + SMOOTHING_VALUE).toShort() }
}