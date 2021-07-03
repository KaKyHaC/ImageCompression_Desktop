package data_model.types

import java.math.BigInteger
import java.util.*

sealed class DataOpc(
        val base: ShortArray,
        val sign: Array<BooleanArray>,
        val DC: Short,
        val size: Size
) {
    class BI(
            base: ShortArray,
            sign: Array<BooleanArray>,
            DC: Short,
            val N: BigInteger,
            size: Size
    ) : DataOpc(base, sign, DC, size)

    class Long(
            base: ShortArray,
            sign: Array<BooleanArray>,
            DC: Short,
            val vectorCode: Vector<Long>,
            size: Size
    ) : DataOpc(base, sign, DC, size)
}