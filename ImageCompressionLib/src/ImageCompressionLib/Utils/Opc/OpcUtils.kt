package ImageCompressionLib.Utils.Opc

import ImageCompressionLib.Data.Matrix.Matrix
import ImageCompressionLib.Data.Primitives.DataOpc

class OpcUtils {
    companion object {
        @JvmStatic
        fun FindBase(dataOrigin: Matrix<Short>, dataOpc: DataOpc) {
            for (j in 0 until dataOrigin.height) {
                for (i in 0 until dataOrigin.width) {
                    if (dataOpc.base[j] <= dataOrigin[i,j]) {
                        dataOpc.base[j] = (dataOrigin[i,j]+1).toShort()
                    }
                }
            }
        }
        @JvmStatic
        fun setSameBaseIn(dataOrigin: Matrix<DataOpc>) {
            val baseSize=dataOrigin[0,0].base.size
            val res = ShortArray(baseSize)
            for (b in 0 until baseSize) {
                for (i in 0 until dataOrigin.width) {
                    for (j in 0 until dataOrigin.height) {
                        if (dataOrigin[i,j].base[b] > res[b]) {
                            res[b] = dataOrigin[i,j].base[b]
                        }
                    }
                }
            }
            for (b in 0 until baseSize) {
                for (i in 0 until dataOrigin.width) {
                    for (j in 0 until dataOrigin.height) {
                        dataOrigin[i,j].base[b] = res[b]
                    }
                }
            }
        }
        @JvmStatic
        fun MakeUnSigned(dataOrigin: Matrix<Short>, dataOpc: DataOpc) {
            for (i in 0 until dataOrigin.width) {
                for (j in 0 until dataOrigin.height) {
                    if (dataOrigin[i,j] < 0.toShort()) {
                        dataOrigin[i,j] = (dataOrigin[i,j] * -1).toShort()
                        dataOpc.sign[i][j] = false
                    } else {
                        dataOpc.sign[i][j] = true
                    }
                }
            }
        }
        @JvmStatic
        fun MakeSigned(dataOrigin: Matrix<Short>, DataOpc: DataOpc) {
            for (i in 0 until dataOrigin.width) {
                for (j in 0 until dataOrigin.height) {
                    if (!DataOpc.sign[i][j]) {
                        dataOrigin[i,j] = (dataOrigin[i,j] * -1).toShort()
                    }
                }
            }
        }
        @JvmStatic
        fun DCminus(dataOrigin: Matrix<Short>, DataOpc: DataOpc) {
            DataOpc.DC = dataOrigin[0,0]
            dataOrigin[0,0] = 0
        }
        @JvmStatic
        fun DCplus(dataOrigin: Matrix<Short>, DataOpc: DataOpc) {
            dataOrigin[0,0] = DataOpc.DC
        }
        @JvmStatic
        fun MakeBaseEven(dataOpc: DataOpc){
            for(i  in 0 until dataOpc.base.size)
                if(dataOpc.base[i]%2!=0)
                    dataOpc.base[i]++
        }
        @JvmStatic
        fun MakeDataOdd(dataOrigin: Matrix<Short>){
            dataOrigin.forEach{i, j, value ->
                var tmp=value
                if(value%2==0)
                    tmp++
                tmp
            }
        }
    }
}

