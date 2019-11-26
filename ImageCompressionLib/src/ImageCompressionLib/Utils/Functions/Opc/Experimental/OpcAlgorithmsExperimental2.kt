package ImageCompressionLib.Utils.Functions.Opc.Experimental

import ImageCompressionLib.Constants.TWO
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Type.DataOpc
import java.math.BigInteger

object OpcAlgorithmsExperimental2 {

    fun opcDirectWithMessageAt(
        dataOrigin: Matrix<Short>,
        dataOpc: DataOpc,
        data: MessageAndPositionArray
    ) {
        var base = BigInteger.ONE
        for (i in dataOrigin.width - 1 downTo 0) {
            for (j in dataOrigin.height - 1 downTo 0) {
                if (dataOrigin[i, j].toInt() != 0) {
                    dataOpc.N =
                        dataOpc.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i, j].toLong())));
                }
                base = base.multiply(BigInteger.valueOf(dataOpc.base[j].toLong()));

                val pos = i * dataOrigin.width + j
                if (data.isHadMessage(pos)) {
                    if (data.getMessageAt(pos))
                        dataOpc.N += base

                    base *= TWO
                }
            }
        }
        dataOpc.base[0] = (dataOpc.base[0] * 2).toShort()
    }


    fun opcReverseWithMessageAt(
        dataOrigin: Matrix<Short>,
        dataOpc: DataOpc,
        data: MessageAndPositionArray
    ) {// method copy from C++ Project MAH
        var copy = BigInteger.ONE
        var b: BigInteger
        dataOpc.base[0] = (dataOpc.base[0] / 2).toShort()
        for (i in dataOrigin.width - 1 downTo 0) {
            for (j in dataOrigin.height - 1 downTo 0) {
                val a = dataOpc.N.divide(copy)
                val baseL = dataOpc.base[j].toLong()
                copy = copy.multiply(BigInteger.valueOf(baseL))

                b = dataOpc.N.divide(copy).multiply(BigInteger.valueOf(baseL))
                dataOrigin[i, j] = a.subtract(b).toShort()

                val pos = i * dataOrigin.width + j
                if (data.isHadMessage(pos)) {
                    val tmp = (dataOpc.N / copy) - (dataOpc.N / (copy * TWO) * TWO)
                    data.setMessageAt(pos, tmp.compareTo(BigInteger.ONE) == 0)
                    copy *= TWO
                }
            }
        }
    }


    fun opcDirectStegoAt(
        dataOrigin: Matrix<Short>,
        dataOpc: DataOpc,
        message: Boolean,
        message_position: Int
    ) {
        var base = BigInteger.ONE
        for (i in dataOrigin.width - 1 downTo 0) {
            for (j in dataOrigin.height - 1 downTo 0) {
                if (dataOrigin[i, j].toInt() != 0) {
                    dataOpc.N =
                        dataOpc.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i, j].toLong())));
                }
                base = base.multiply(BigInteger.valueOf(dataOpc.base[j].toLong()));

                if (i * dataOrigin.width + j == message_position) {
                    if (message)
                        dataOpc.N += base

                    base *= TWO
                }
            }
        }
        dataOpc.base[0] = (dataOpc.base[0] * 2).toShort()
    }
}