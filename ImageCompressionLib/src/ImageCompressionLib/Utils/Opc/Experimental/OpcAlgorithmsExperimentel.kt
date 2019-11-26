package ImageCompressionLib.Utils.Opc.Experimental

import ImageCompressionLib.Constants.TWO
import ImageCompressionLib.Data.Matrix.Matrix
import ImageCompressionLib.Data.Primitives.DataOpc
import java.math.BigInteger

class OpcAlgorithmsExperimentel {
    companion object {
        @JvmStatic
        fun OpcDirectWithMessageAt(dataOrigin: Matrix<Short>, dataOpc: DataOpc,
                                   data:MessageAndPositionArray, CodeCoefisient:BigInteger) {
            var base = BigInteger.ONE
            for (i in dataOrigin.width - 1 downTo 0) {
                for (j in dataOrigin.height - 1 downTo 0) {
                    if (dataOrigin[i,j].toInt() != 0) {
                        dataOpc.N = dataOpc.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i,j].toLong())));
                    }
                    base = base.multiply(BigInteger.valueOf(dataOpc.base[j].toLong()));

                    val pos=i * dataOrigin.width + j
                    if (data.isHadMessage(pos)) {
                        if (data.getMessageAt(pos))
                            dataOpc.N += base

                        base *= TWO
//                         or after loops
//                        dataOpc.base[0] = (dataOpc.base[0] * 2).toShort()
//                        if (dataOpc.base[0] > 255)
//                            dataOpc.base[0] = 255
                    }
                }
            }
            dataOpc.N/=CodeCoefisient
        }

        @JvmStatic
        fun OpcReverceWithMessageAt(dataOrigin: Matrix<Short>, DataOpc: DataOpc,
                                    data:MessageAndPositionArray, CodeCoefisient:BigInteger){// method copy from C++ Project MAH
            var copy = BigInteger.ONE
            var b: BigInteger
            var message = false
            DataOpc.N*=CodeCoefisient
//            DataOpc.base[0] = (DataOpc.base[0] / 2).toShort()// is it need ?//TODO try !=0
            for (i in dataOrigin.width - 1 downTo 0) {
                for (j in dataOrigin.height - 1 downTo 0) {
                    val a = DataOpc.N.divide(copy)
                    val baseL = DataOpc.base[j].toLong()
                    copy = copy.multiply(BigInteger.valueOf(baseL))

                    b = DataOpc.N.divide(copy).multiply(BigInteger.valueOf(baseL))
                    dataOrigin[i,j] = a.subtract(b).toShort()

                    val pos=i * dataOrigin.width + j
                    if (data.isHadMessage(pos)) {
                        val tmp = (DataOpc.N / copy) - (DataOpc.N / (copy * TWO) * TWO)
                        data.setMessageAt(pos,tmp.compareTo(BigInteger.ONE) == 0)
                        copy *= TWO
                    }
                }
            }
        }

        @JvmStatic
        fun OpcDirectStegoAt(dataOrigin: Matrix<Short>, dataOpc: DataOpc, message: Boolean, message_position: Int) {
            var base = BigInteger.ONE
            for (i in dataOrigin.width - 1 downTo 0) {
                for (j in dataOrigin.height - 1 downTo 0) {
                    if (dataOrigin[i,j].toInt() != 0) {
                        dataOpc.N = dataOpc.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i,j].toLong())));
                    }
                    base = base.multiply(BigInteger.valueOf(dataOpc.base[j].toLong()));

                    if (i * dataOrigin.width + j == message_position) {
                        if (message)
                            dataOpc.N += base

                        base *= TWO
                    }
                }
            }
        }
    }
}