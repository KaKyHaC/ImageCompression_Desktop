package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Constants.MAX_LONG
import ImageCompressionLib.Containers.DataOpc
import ImageCompressionLib.Containers.ShortMatrix
import java.math.BigInteger

fun OPCdirectDefault(dataOrigin: ShortMatrix, DataOpc: DataOpc) {//TODO diagonal for optimization
    var base= BigInteger("1")
    for (i in dataOrigin.width-1 downTo 0){
        for (j in dataOrigin.height-1 downTo 0) {
            if (dataOrigin[i][j].toInt() != 0) {
                DataOpc.N = DataOpc.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j].toLong())));
            }
            base = base.multiply(BigInteger.valueOf(DataOpc.base[i].toLong()));
        }
    }
}
fun OPCreverseDefault(dataOrigin: ShortMatrix, DataOpc: DataOpc) {// method copy from C++ Project MAH
    var copy = BigInteger("1")
    var b: BigInteger
    for (i in dataOrigin.width- 1 downTo 0) {
        for (j in dataOrigin.height - 1 downTo 0) {

            val a = DataOpc.N.divide(copy)
            copy = copy.multiply(BigInteger.valueOf(DataOpc.base[i].toLong()))

            b = DataOpc.N.divide(copy)
            b = b.multiply(BigInteger.valueOf(DataOpc.base[i].toLong()))
            dataOrigin[i][j] = a.subtract(b).toShort()
        }
    }
}

fun OPCdirectLongAndBI(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
    var base: Long = 1
    var res: Long = 0
    var bufbase: Long = 1
    for (i in dataOrigin.width - 1 downTo 0) {
        for (j in dataOrigin.height - 1 downTo 0) {
            bufbase = base * DataOpc.base[i]
            if (bufbase > MAX_LONG) {//is true ? //todo try <-1
                OPCdirectBIfromLong(res, base, i, j, dataOrigin, DataOpc)
                return
            }
            if (dataOrigin[i][j].toInt() != 0) {
                res += base * dataOrigin[i][j]
            }
            base = bufbase
        }
    }
    DataOpc.N = BigInteger.valueOf(res)
}

private fun OPCdirectBIfromLong(res: Long, baseval: Long, i1: Int, j1: Int, dataOrigin: ShortMatrix, DataOpc: DataOpc){
    var `val` = BigInteger.valueOf(res)
    var base = BigInteger.valueOf(baseval)

    var i = i1
    var j = j1
    while (i >= 0) {
        while (j >= 0) {
            if (dataOrigin[i][j].toInt() != 0) {
                `val` = `val`.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j].toLong())))
            }
            base = base.multiply(BigInteger.valueOf(DataOpc.base[i].toLong()))
            j--
        }
        j = dataOrigin.height- 1
        i--
    }
    DataOpc.N= `val`
}

private fun OPCdirectUseOnlyLong(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
    var base: Long = 1
    var res: Long = 0
    var bufbase: Long
    for (i in dataOrigin.width- 1 downTo 0) {
        for (j in dataOrigin.height - 1 downTo 0) {
            bufbase = base * DataOpc.base[i]
            if (bufbase > MAX_LONG)
            {
                DataOpc.vectorCode.add(res)
                base = 1
                res = 0
                bufbase = base * DataOpc.base[i]
            }
            if (dataOrigin[i][j].toInt() != 0) {
                res += base * dataOrigin[i][j]
            }
            base = bufbase
        }
    }
    DataOpc.vectorCode.add(res)
}

private fun OPCreverseUseOnlyLong(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
    var copy: Long = 1
    var index = 0
    var curN = DataOpc.vectorCode.elementAt(index)
    var nextcopy: Long
    for (i in dataOrigin.width - 1 downTo 0) {
        if (DataOpc.base[i].toInt() <= 0)//for wrong password;// old ==
            DataOpc.base[i] = 1
        for (j in dataOrigin.height - 1 downTo 0) {
            nextcopy = copy * DataOpc.base[i]
            if (nextcopy > MAX_LONG || nextcopy < 0) {
                copy = 1
                index++
                nextcopy = copy * DataOpc.base[i]
                if (index < DataOpc.vectorCode.size)
                    curN = DataOpc.vectorCode.elementAt(index)
            }
            val a: Long
            var b: Long

            a = curN / copy
            copy = nextcopy

            b = curN / copy
            b = b * DataOpc.base[i]
            dataOrigin[i][j] = (a - b).toShort()
        }
    }
}
