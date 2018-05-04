package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Constants.MAX_LONG
import ImageCompressionLib.Constants.TWO
import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import java.math.BigInteger

class OpcAlgorithms {
    companion object {
        @JvmStatic
        fun OpcDirectDefault(dataOrigin: ShortMatrix, DataOpc: DataOpc) {//TODO diagonal for optimization
            var base = BigInteger.ONE
            for (i in dataOrigin.width - 1 downTo 0) {
                for (j in dataOrigin.height - 1 downTo 0) {
                    if (dataOrigin[i,j].toInt() != 0) {
                        DataOpc.N = DataOpc.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i,j].toLong())));
                    }
                    base = base.multiply(BigInteger.valueOf(DataOpc.base[j].toLong()));
                }
            }
        }

        @JvmStatic
        fun OpcReverseDefault(dataOrigin: ShortMatrix, DataOpc: DataOpc) {// method copy from C++ Project MAH
            var copy = BigInteger.ONE
            var b: BigInteger
            for (i in dataOrigin.width - 1 downTo 0) {
                for (j in dataOrigin.height - 1 downTo 0) {
                    val a = DataOpc.N.divide(copy)
                    val baseL = DataOpc.base[j].toLong()
                    copy = copy.multiply(BigInteger.valueOf(baseL))
                    b = DataOpc.N.divide(copy).multiply(BigInteger.valueOf(baseL))
                    dataOrigin[i,j] = a.subtract(b).toShort()
                }
            }
        }

        @JvmStatic
        fun OpcDirectLongAndBI(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
            var base: Long = 1
            var res: Long = 0
            var bufbase: Long = 1
            for (i in dataOrigin.width - 1 downTo 0) {
                for (j in dataOrigin.height - 1 downTo 0) {
                    bufbase = base * DataOpc.base[j]
                    if (bufbase > MAX_LONG) {//is true ? //todo try < -1
                        OpcDirectBIfromLong(res, base, i, j, dataOrigin, DataOpc)
                        return
                    }
                    if (dataOrigin[i,j].toInt() != 0) {
                        res += base * dataOrigin[i,j]
                    }
                    base = bufbase
                }
            }
            DataOpc.N = BigInteger.valueOf(res)
        }

        private fun OpcDirectBIfromLong(res: Long, baseval: Long, i1: Int, j1: Int, dataOrigin: ShortMatrix, DataOpc: DataOpc) {
            var `val` = BigInteger.valueOf(res)
            var base = BigInteger.valueOf(baseval)

            var i = i1
            var j = j1
            while (i >= 0) {
                while (j >= 0) {
                    if (dataOrigin[i,j].toInt() != 0) {
                        `val` = `val`.add(base.multiply(BigInteger.valueOf(dataOrigin[i,j].toLong())))
                    }
                    base = base.multiply(BigInteger.valueOf(DataOpc.base[j].toLong()))
                    j--
                }
                j = dataOrigin.height - 1
                i--
            }
            DataOpc.N = `val`
        }

        @JvmStatic
        fun OpcDirectUseOnlyLong(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
            var base: Long = 1
            var res: Long = 0
            var bufbase: Long
            for (i in dataOrigin.width - 1 downTo 0) {
                for (j in dataOrigin.height - 1 downTo 0) {
                    bufbase = base * DataOpc.base[j]
                    if (bufbase > MAX_LONG) {
                        DataOpc.vectorCode.add(res)
                        base = 1
                        res = 0
                        bufbase = base * DataOpc.base[j]
                    }
                    if (dataOrigin[i,j].toInt() != 0) {
                        res += base * dataOrigin[i,j]
                    }
                    base = bufbase
                }
            }
            DataOpc.vectorCode.add(res)
        }

        @JvmStatic
        fun OpcReverseUseOnlyLong(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
            var copy: Long = 1
            var index = 0
            var curN = DataOpc.vectorCode.elementAt(index)
            var nextcopy: Long
            for (i in dataOrigin.width - 1 downTo 0) {
//        if (DataOpc.base[i].toInt() <= 0)//for wrong password;// old ==
//            DataOpc.base[i] = 1
                for (j in dataOrigin.height - 1 downTo 0) {
                    nextcopy = copy * DataOpc.base[j]
                    if (nextcopy > MAX_LONG || nextcopy < 0) {
                        copy = 1
                        index++
                        nextcopy = copy * DataOpc.base[j]
                        if (index < DataOpc.vectorCode.size)
                            curN = DataOpc.vectorCode.elementAt(index)
                    }
                    val a: Long
                    var b: Long

                    a = curN / copy
                    copy = nextcopy

                    b = curN / copy
                    b = b * DataOpc.base[j]
                    dataOrigin[i,j] = (a - b).toShort()
                }
            }
        }

        @JvmStatic
        fun OpcDirectWithMessageAtFirst(dataOrigin: ShortMatrix, dataOpc: DataOpc, message: Boolean) {
            var base = BigInteger.ONE
            for (i in dataOrigin.width - 1 downTo 0) {
                for (j in dataOrigin.height - 1 downTo 0) {
                    if (dataOrigin[i,j].toInt() != 0) {
                        dataOpc.N = dataOpc.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i,j].toLong())));
                    }
                    base = base.multiply(BigInteger.valueOf(dataOpc.base[j].toLong()));
                }
            }
            if (message)
                dataOpc.N += base

            dataOpc.N /= TWO
        }

        @JvmStatic
        fun OpcReverceWithMessageAtFirst(dataOrigin: ShortMatrix, dataOpc: DataOpc): Boolean {
            dataOpc.N *= TWO
            var copy = BigInteger.ONE
            var b: BigInteger
            for (i in dataOrigin.width - 1 downTo 0) {
                for (j in dataOrigin.height - 1 downTo 0) {
                    val baseL = dataOpc.base[j].toLong()
                    val a = dataOpc.N.divide(copy)
                    copy = copy.multiply(BigInteger.valueOf(baseL))
                    b = dataOpc.N.divide(copy).multiply(BigInteger.valueOf(baseL))
                    dataOrigin[i,j] = a.subtract(b).toShort()
                }
            }
            val message = (((dataOpc.N / copy) - (dataOpc.N / (copy * TWO)) * TWO).toInt() == 1)
            return message
        }

        @JvmStatic
        fun OpcDirectWithMessageAt(dataOrigin: ShortMatrix, dataOpc: DataOpc, message: Boolean, message_position: Int) {
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
                        // or after loops
                        dataOpc.base[0] = (dataOpc.base[0] * 2).toShort()
                        if (dataOpc.base[0] > 255)
                            dataOpc.base[0] = 255
                    }
                }
            }
        }

        @JvmStatic
        fun OpcReverceWithMessageAt(dataOrigin: ShortMatrix, DataOpc: DataOpc, message_position: Int): Boolean {// method copy from C++ Project MAH
            var copy = BigInteger.ONE
            var b: BigInteger
            var message = false
            DataOpc.base[0] = (DataOpc.base[0] / 2).toShort()// is it need ?
            for (i in dataOrigin.width - 1 downTo 0) {
                for (j in dataOrigin.height - 1 downTo 0) {
                    val a = DataOpc.N.divide(copy)
                    val baseL = DataOpc.base[j].toLong()
                    copy = copy.multiply(BigInteger.valueOf(baseL))

                    b = DataOpc.N.divide(copy).multiply(BigInteger.valueOf(baseL))
                    dataOrigin[i,j] = a.subtract(b).toShort()

                    if (i * dataOrigin.width + j == message_position) {
                        val tmp = (DataOpc.N / copy) - (DataOpc.N / (copy * TWO) * TWO)
                        message = tmp.compareTo(BigInteger.ONE) == 0
                        copy *= TWO
                    }
                }
            }
            return message
        }
    }
}