package ImageCompressionLib.Constants

/**
 * Created by Димка on 08.08.2016.
 */
object QuantizationTable {

    val luminosity = arrayOf(
        shortArrayOf(1, 2, 3, 4, 5, 6, 7, 8),
        shortArrayOf(2, 2, 3, 4, 5, 8, 10, 15),
        shortArrayOf(3, 4, 5, 6, 7, 9, 16, 20),
        shortArrayOf(4, 6, 8, 19, 21, 27, 28, 36),
        shortArrayOf(5, 12, 27, 36, 48, 59, 69, 77),
        shortArrayOf(7, 35, 35, 44, 51, 69, 79, 82),
        shortArrayOf(9, 44, 47, 48, 49, 59, 69, 79),
        shortArrayOf(12, 49, 55, 68, 79, 80, 99, 99)
    )

    val chromaticity = arrayOf(
        shortArrayOf(1, 1, 2, 4, 9, 99, 99, 99),
        shortArrayOf(1, 2, 2, 6, 99, 99, 99, 99),
        shortArrayOf(2, 2, 56, 99, 99, 99, 99, 99),
        shortArrayOf(4, 66, 99, 99, 99, 99, 99, 99),
        shortArrayOf(99, 99, 99, 99, 99, 99, 99, 99),
        shortArrayOf(99, 99, 99, 99, 99, 99, 99, 99),
        shortArrayOf(99, 99, 99, 99, 99, 99, 99, 99),
        shortArrayOf(99, 99, 99, 99, 99, 99, 99, 99)
    )


    private var smart: Array<ShortArray>? = null
    private var smartValue: Int = 0

    private fun initSmart(value: Int) {
        smartValue = value
        smart = Array(Constants.SIZE_OF_BLOCK) { i ->
            ShortArray(Constants.SIZE_OF_BLOCK) { j ->
                (1 + value * (i + j)).toShort()
            }
        }
    }


    fun getLuminosity(i: Int, j: Int) = luminosity[i][j]

    fun getChromaticity(i: Int, j: Int) = chromaticity[i][j]

    fun getSmart(value: Int, i: Int, j: Int): Short {
        if (smart == null || value != smartValue)
            initSmart(value)
        return smart!![i][j]
    }
}
