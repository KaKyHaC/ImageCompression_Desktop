package ImageCompressionLib.Utils.Dct


import ImageCompressionLib.Constants.Cosine
import ImageCompressionLib.Constants.QuantizationTable
import ImageCompressionLib.Data.Enumerations.TypeQuantization
import ImageCompressionLib.Data.Matrix.Matrix
import ImageCompressionLib.Data.Matrix.ShortMatrix

object DctAlgorithm8x8 {
    //singelton
    val SIZEOFBLOCK = 8
    private val OneDivideMathsqrt2 = 1.0 / Math.sqrt(2.0)

    fun directDCT(data: Matrix<Short>): Matrix<Short> {
        val dataProcessed = ShortMatrix(data.width, data.height)
        return directDCT(data, dataProcessed)
    }

    fun reverseDCT(data: Matrix<Short>): Matrix<Short> {
        val dataProcessed = ShortMatrix(data.width, data.height)
        return reverseDCT(data, dataProcessed)
    }

    fun directQuantization(_tq: TypeQuantization, data: Matrix<Short>): Matrix<Short> {
        if (_tq === TypeQuantization.Luminosity)
            for (i in 0 until SIZEOFBLOCK)
                for (j in 0 until SIZEOFBLOCK) {
                    //dataProcessed[i][j]/=QuantizationTable.getSmart(1,i,j);
                    data[i, j] = (data[i, j] / QuantizationTable.Companion.getLuminosity(i, j)) as Short
                }
        else if (_tq === TypeQuantization.Chromaticity)
            for (i in 0 until SIZEOFBLOCK)
                for (j in 0 until SIZEOFBLOCK) {
                    // dataProcessed[i][j]/=QuantizationTable.getSmart(3,i,j);
                    data[i, j] = (data[i, j] / QuantizationTable.Companion.getChromaticity(i, j)) as Short
                    //                    data[i][j] /= QuantizationTable.getChromaticity(i, j);
                }
        return data
    }

    fun reverseQuantization(_tq: TypeQuantization, data: Matrix<Short>): Matrix<Short> {
        if (_tq === TypeQuantization.Luminosity)
            for (i in 0 until SIZEOFBLOCK)
                for (j in 0 until SIZEOFBLOCK) {
                    // dataProcessed[i][j]*=QuantizationTable.getSmart(1,i,j);
                    data[i, j] = (data[i, j] * QuantizationTable.Companion.getLuminosity(i, j)) as Short
                    //                    data[i][j] *= QuantizationTable.getLuminosity(i, j);
                }
        else if (_tq === TypeQuantization.Chromaticity)
            for (i in 0 until SIZEOFBLOCK)
                for (j in 0 until SIZEOFBLOCK) {
                    // dataProcessed[i][j]*=QuantizationTable.getSmart(3,i,j);
                    data[i, j] = (data[i, j] * QuantizationTable.Companion.getChromaticity(i, j)) as Short
                    //                    data[i][j] *= QuantizationTable.getChromaticity(i, j);
                }
        return data

    }

    /*-------main metode---------*/
    private fun directDCT(dataOriginal: Matrix<Short>, dataProcessed: Matrix<Short>): Matrix<Short> {
        val w = SIZEOFBLOCK//dataOriginal.getWidth();
        val h = SIZEOFBLOCK//dataOriginal.getHeight();
        for (i in 0 until w) {
            for (j in 0 until h) {
                assert(dataOriginal[i, j] <= 0xff) { "dataOriginal[" + i + "][" + j + "]=" + dataOriginal[i, j] }
                var res = Cosine.getDCTres(i, j)
                var sum = 0.0
                for (x in 0 until w) {
                    for (y in 0 until h) {
                        sum += dataOriginal[x, y] * Cosine.getCos(x, y, i, j)
                    }
                }
                res *= sum
                dataProcessed[i, j] = res.toShort()
            }
        }
        return dataProcessed
    }

    private fun reverseDCT(dataOriginal: Matrix<Short>, dataProcessed: Matrix<Short>): Matrix<Short> {
        val w = SIZEOFBLOCK//dataOriginal.getWidth();
        val h = SIZEOFBLOCK//dataOriginal.getHeight();
        for (x in 0 until w) {
            for (y in 0 until h) {
                // double res=1.0/4.0;
                var sum = 0.0
                for (i in 0 until w) {
                    for (j in 0 until h) {
                        val Ci = if (i == 0) OneDivideMathsqrt2 else 1.0
                        val Cj = if (j == 0) OneDivideMathsqrt2 else 1.0
                        val buf = Ci * Cj * dataOriginal[i, j] * Cosine.getCos(x, y, i, j)
                        sum += buf
                    }
                }
                //                assert FromDoubleToShort(0.25*sum)<=0xff:"dataProcessed["+x+"]["+y+"]="+0.25*sum;
                dataProcessed[x, y] = FromDoubleToShort(0.25 * sum) // old (short)
            }
        }
        // plus128();//
        return dataProcessed
    }

    private fun minus128(dataOriginal: Matrix<Short>): Matrix<Short> {
        for (i in 0 until SIZEOFBLOCK)
            for (j in 0 until SIZEOFBLOCK) {
                dataOriginal[i, j] = (dataOriginal[i, j] - 128).toShort()
            }
        return dataOriginal
    }

    private fun plus128(dataProcessed: Matrix<Short>): Matrix<Short> {
        for (i in 0 until SIZEOFBLOCK)
            for (j in 0 until SIZEOFBLOCK) {
                dataProcessed[i, j] = (dataProcessed[i, j] + 128).toShort()
            }
        return dataProcessed
    }
    // обопщенно позоционное кодирование

    private fun FromDoubleToShort(d: Double): Short {
        if (d >= 255)
            return 255
        var res = d.toShort()
        if (d % 1 > 0.5)
            res++
        return res
    }
}
