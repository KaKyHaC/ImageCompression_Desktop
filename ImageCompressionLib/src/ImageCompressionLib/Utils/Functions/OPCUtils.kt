package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Containers.*
import java.math.BigInteger

class OPCUtils private constructor() {
    companion object {
        @JvmStatic val instance = OPCUtils()
    }

    private fun directOPC(parameters: Parameters, dataOrigin: ShortMatrix, DataOpc: DataOpc) {
        if (flag.isChecked(Flag.Parameter.DCT))
            MakeUnSigned(dataOrigin, DataOpc)
        if (flag.isChecked(Flag.Parameter.DC))
            DCminus(dataOrigin, DataOpc)
        FindeBase(dataOrigin, DataOpc)

        if (flag.isChecked(Flag.Parameter.LongCode))
            OPCdirectUseOnlyLong(dataOrigin, DataOpc)
        else
            OPCdirect(dataOrigin, DataOpc)
    }

    private fun reverseOPC(parameters: Parameters,DataOpc: DataOpc,dataOrigin: ShortMatrix) {
        if (flag.isChecked(Flag.Parameter.LongCode))
            OPCreverseUseOnlyLong(dataOrigin, DataOpc)
        else
            OPCreverse(dataOrigin, DataOpc)

        if (flag.isChecked(Flag.Parameter.DC))
            DCplus(dataOrigin, DataOpc)

        if (flag.isChecked(Flag.Parameter.DCT))
            MakeSigned(dataOrigin, DataOpc)
    }


    private fun FindBase(dataOrigin: ShortMatrix, dataOpc: DataOpc){
        for (i in 0 until dataOrigin.height) {
            dataOpc.base[i] = dataOrigin[i][0]
            for (j in 0 until dataOrigin.width) {
                if (dataOpc.base[i] < dataOrigin[i][j]) {
                    dataOpc.base[i] = dataOrigin[i][j]
                }
            }
            dataOpc.base[i]++
        }
    }

    private fun MakeUnSigned(dataOrigin: ShortMatrix, dataOpc: DataOpc) {
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

    private fun MakeSigned(dataOrigin: ShortMatrix, DataOpc: DataOpc){
        for (i in 0 until dataOrigin.width) {
            for (j in 0 until dataOrigin.height) {
                if (!DataOpc.sign[i][j]) {
                    dataOrigin[i][j] = (dataOrigin[i][j] * -1).toShort()
                }
            }
        }
    }

    private fun DCminus(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
        DataOpc.DC = dataOrigin[0][0]
        dataOrigin[0][0] = 0
    }

    private fun DCplus(dataOrigin: ShortMatrix, DataOpc: DataOpc) {
        dataOrigin[0][0] = DataOpc.DC
    }




    fun getDataOrigin(dopc: DataOpc, flag: Flag): ShortMatrix {
        val dataOrigin = Array(SIZEOFBLOCK) { ShortArray(SIZEOFBLOCK) }
        reverseOPC(flag, dataOrigin, dopc)
        return dataOrigin
    }

    fun getDataOpc(dataOrigin: ShortMatrix, flag: Flag): DataOpc {
        val DataOpc = DataOpc()
        directOPC(flag, dataOrigin, DataOpc)
        return DataOpc
    }

    fun findBase(dataOrigin: ShortMatrix, flag: Flag): DataOpc {//TODO What it that doing ?
        val DataOpc = DataOpc()

        MakeUnSigned(dataOrigin, DataOpc)
        if (flag.isChecked(Flag.Parameter.DC)) {
            DCminus(dataOrigin, DataOpc)
        }
        FindeBase(dataOrigin, DataOpc)

        return DataOpc
    }

    fun directOPCwithFindedBase(dataOrigin: ShortMatrix, d: DataOpc, flag: Flag): DataOpc {

        MakeUnSigned(dataOrigin, d)
        if (flag.isChecked(Flag.Parameter.DC)) {
            DCminus(dataOrigin, d)
        }

        if (flag.isChecked(Flag.Parameter.LongCode))
            OPCdirectUseOnlyLong(dataOrigin, d)
        else
            OPCdirect(dataOrigin, d)
        return d
    }

}