package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Containers.Matrix.DataOpcMatrix
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.DataOpc

class OpcUtils {
    companion object {
        @JvmStatic
        fun FindBase(dataOrigin: ShortMatrix, dataOpc: DataOpc) {
            for (i in 0 until dataOrigin.height) {
                dataOpc.base[i] = dataOrigin[0,i]
                for (j in 0 until dataOrigin.width) {
                    if (dataOpc.base[i] < dataOrigin[j,i]) {
                        dataOpc.base[i] = dataOrigin[j,i]
                    }
                }
                dataOpc.base[i]++
            }
        }
        @JvmStatic
        fun setSameBaseIn(dataOrigin: DataOpcMatrix) {
            val baseSize=dataOrigin[0,0].base.size
            val res = ShortArray(baseSize)
            for (b in 0 until baseSize) {
                for (i in 0 until dataOrigin.height) {
                    for (j in 0 until dataOrigin.width) {
                        if (dataOrigin[i,j].base[b] > res[b]) {
                            res[b] = dataOrigin[i,j].base[b]
                        }
                    }
                }
            }
            for (b in 0 until baseSize) {
                for (i in 0 until dataOrigin.height) {
                    for (j in 0 until dataOrigin.width) {
                        dataOrigin[i,j].base[b] = res[b]
                    }
                }
            }
        }
        @JvmStatic
        fun MakeUnSigned(dataOrigin: ShortMatrix, dataOpc: DataOpc) {
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
        fun MakeSigned(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
            for (i in 0 until dataOrigin.width) {
                for (j in 0 until dataOrigin.height) {
                    if (!DataOpc.sign[i][j]) {
                        dataOrigin[i,j] = (dataOrigin[i,j] * -1).toShort()
                    }
                }
            }
        }
        @JvmStatic
        fun DCminus(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
            DataOpc.DC = dataOrigin[0,0]
            dataOrigin[0,0] = 0
        }
        @JvmStatic
        fun DCplus(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
            dataOrigin[0,0] = DataOpc.DC
        }
    }
}

