package ImageCompressionLib.Utils.Functions.Opc.Experimental

import ImageCompressionLib.Data.Type.DataOpc
import java.math.BigInteger

class StegoMasking {
    companion object {
        @JvmStatic
        fun divideTwo(dataOpc: DataOpc): Unit {
            dataOpc.N/= BigInteger.TWO
        }
        @JvmStatic
        fun multiTwo(dataOpc: DataOpc): Unit {
            dataOpc.N*= BigInteger.TWO
        }
        @JvmStatic
        fun multiBase(dataOpc: DataOpc): Unit {
            dataOpc.base[0]=(dataOpc.base[0]*2).toShort()
        }
    }
}