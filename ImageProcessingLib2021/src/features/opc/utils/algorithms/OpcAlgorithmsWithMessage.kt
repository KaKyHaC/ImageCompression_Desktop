package features.opc.utils.algorithms

object OpcAlgorithmsWithMessage {

//    @JvmStatic
//    fun OpcDirectWithMessageAtFirst(dataOrigin: Matrix<Short>, dataOpc: DataOpc, message: Boolean) {
//        var base = BigInteger.ONE
//        for (i in dataOrigin.width - 1 downTo 0) {
//            for (j in dataOrigin.height - 1 downTo 0) {
//                if (dataOrigin[i, j].toInt() != 0) {
//                    dataOpc.N = dataOpc.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i, j].toLong())));
//                }
//                base = base.multiply(BigInteger.valueOf(dataOpc.base[j].toLong()));
//            }
//        }
//        if (message)
//            dataOpc.N += base
//
//        dataOpc.N /= TWO
//    }
//
//    @JvmStatic
//    fun OpcReverceWithMessageAtFirst(dataOrigin: Matrix<Short>, dataOpc: DataOpc): Boolean {
//        dataOpc.N *= TWO
//        var copy = BigInteger.ONE
//        var b: BigInteger
//        for (i in dataOrigin.width - 1 downTo 0) {
//            for (j in dataOrigin.height - 1 downTo 0) {
//                val baseL = dataOpc.base[j].toLong()
//                val a = dataOpc.N.divide(copy)
//                copy = copy.multiply(BigInteger.valueOf(baseL))
//                b = dataOpc.N.divide(copy).multiply(BigInteger.valueOf(baseL))
//                dataOrigin[i, j] = a.subtract(b).toShort()
//            }
//        }
//        val message = (((dataOpc.N / copy) - (dataOpc.N / (copy * TWO)) * TWO).toInt() == 1)
//        return message
//    }
//
//    @JvmStatic
//    fun OpcDirectWithMessageAt(dataOrigin: Matrix<Short>, dataOpc: DataOpc, message: Boolean, message_position: Int) {
//        var base = BigInteger.ONE
//        for (i in dataOrigin.width - 1 downTo 0) {
//            for (j in dataOrigin.height - 1 downTo 0) {
//                if (dataOrigin[i, j].toInt() != 0) {
//                    dataOpc.N = dataOpc.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i, j].toLong())));
//                }
//                base = base.multiply(BigInteger.valueOf(dataOpc.base[j].toLong()));
//
//                if (i * dataOrigin.width + j == message_position) {
//                    if (message)
//                        dataOpc.N += base
//
//                    base *= TWO
//                    // or after loops
//                    dataOpc.base[0] = (dataOpc.base[0] * 2).toShort()
//                    if (dataOpc.base[0] > 255)
//                        dataOpc.base[0] = 255
//                }
//            }
//        }
//    }
//
//    @JvmStatic
//    fun OpcReverceWithMessageAt(dataOrigin: Matrix<Short>, DataOpc: DataOpc, message_position: Int): Boolean {// method copy from C++ Project MAH
//        var copy = BigInteger.ONE
//        var b: BigInteger
//        var message = false
////            DataOpc.base[0] = (DataOpc.base[0] / 2).toShort()// is it need ?//TODO try !=0
//        for (i in dataOrigin.width - 1 downTo 0) {
//            for (j in dataOrigin.height - 1 downTo 0) {
//                val a = DataOpc.N.divide(copy)
//                val baseL = DataOpc.base[j].toLong()
//                copy = copy.multiply(BigInteger.valueOf(baseL))
//
//                b = DataOpc.N.divide(copy).multiply(BigInteger.valueOf(baseL))
//                dataOrigin[i, j] = a.subtract(b).toShort()
//
//                if (i * dataOrigin.width + j == message_position) {
//                    val tmp = (DataOpc.N / copy) - (DataOpc.N / (copy * TWO) * TWO)
//                    message = tmp.compareTo(BigInteger.ONE) == 0
//                    copy *= TWO
//                }
//            }
//        }
//        return message
//    }
}