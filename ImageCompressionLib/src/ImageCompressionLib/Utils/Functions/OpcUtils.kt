package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Containers.*
import java.math.BigInteger

//     fun directOPC(parameters: Parameters, dataOrigin: ShortMatrix, DataOpc: DataOpc) {
//        if (flag.isChecked(Flag.Parameter.DCT))
//            MakeUnSigned(dataOrigin, DataOpc)
//        if (flag.isChecked(Flag.Parameter.DC))
//            DCminus(dataOrigin, DataOpc)
//        FindeBase(dataOrigin, DataOpc)
//
//        if (flag.isChecked(Flag.Parameter.LongCode))
//            OPCdirectUseOnlyLong(dataOrigin, DataOpc)
//        else
//            OPCdirect(dataOrigin, DataOpc)
//    }
//
//     fun reverseOPC(parameters: Parameters,DataOpc: DataOpc,dataOrigin: ShortMatrix) {
//        if (flag.isChecked(Flag.Parameter.LongCode))
//            OPCreverseUseOnlyLong(dataOrigin, DataOpc)
//        else
//            OPCreverse(dataOrigin, DataOpc)
//
//        if (flag.isChecked(Flag.Parameter.DC))
//            DCplus(dataOrigin, DataOpc)
//
//        if (flag.isChecked(Flag.Parameter.DCT))
//            MakeSigned(dataOrigin, DataOpc)
//    }


     fun FindBase(dataOrigin: ShortMatrix, dataOpc: DataOpc) {
         for (i in 0 until dataOrigin.height) {
             dataOpc.base[i] = dataOrigin[i][0]
             for (j in 0 until dataOrigin.width) {
                 if (dataOpc.base[i] < dataOrigin[j][i]) {
                     dataOpc.base[i] = dataOrigin[j][i]
                 }
             }
             dataOpc.base[i]++
         }
     }
    fun setSameBaseIn(dataOrigin: DataOpcMatrix,baseSize:Int) {
        val res=ShortArray(baseSize)
        for(b in 0 until baseSize) {
            for (i in 0 until dataOrigin.height) {
                for (j in 0 until dataOrigin.width) {
                    if (dataOrigin[i][j].base[b] > res[b]) {
                        res[b]=dataOrigin[i][j].base[b]
                    }
                }
            }
        }
        for(b in 0 until baseSize) {
            for (i in 0 until dataOrigin.height) {
                for (j in 0 until dataOrigin.width) {
                    dataOrigin[i][j].base[b]=res[b]
                }
            }
        }
    }



     fun MakeUnSigned(dataOrigin: ShortMatrix, dataOpc: DataOpc) {
        for (i in 0 until dataOrigin.width) {
            for (j in 0 until dataOrigin.height) {
                if (dataOrigin[i][j] < 0.toShort()) {
                    dataOrigin[i][j] = (dataOrigin[i][j] * -1).toShort()
                    dataOpc.sign[i][j] = false
                } else {
                    dataOpc.sign[i][j] = true
                }
            }
        }
    }

     fun MakeSigned(dataOrigin: ShortMatrix, DataOpc: DataOpc){
        for (i in 0 until dataOrigin.width) {
            for (j in 0 until dataOrigin.height) {
                if (!DataOpc.sign[i][j]) {
                    dataOrigin[i][j] = (dataOrigin[i][j] * -1).toShort()
                }
            }
        }
    }

     fun DCminus(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
        DataOpc.DC = dataOrigin[0][0]
        dataOrigin[0][0] = 0
    }

     fun DCplus(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
        dataOrigin[0][0] = DataOpc.DC
    }


