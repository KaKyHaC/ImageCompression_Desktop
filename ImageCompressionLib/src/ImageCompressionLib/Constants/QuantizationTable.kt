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


    private val SIZEOFTABLE = 8
    private var smart: Array<ShortArray>? = null
    private var smartval: Int = 0

    private fun createSmart(`val`: Int) {
        smartval = `val`
        smart = Array(SIZEOFTABLE) { ShortArray(SIZEOFTABLE) }
        for (i in 0 until SIZEOFTABLE) {
            for (j in 0 until SIZEOFTABLE) {
                smart!![i][j] = (1 + `val` * (i + j)).toShort()
            }
        }
    }


    fun getLuminosity(x: Int, y: Int): Short {
        return luminosity[x][y]
    }

    fun getChromaticity(x: Int, y: Int): Short {
        return chromaticity[x][y]
    }

    fun getSmart(`val`: Int, i: Int, j: Int): Short {
        if (smart == null || `val` != smartval)
            createSmart(`val`)
        return smart!![i][j]
    }
}
