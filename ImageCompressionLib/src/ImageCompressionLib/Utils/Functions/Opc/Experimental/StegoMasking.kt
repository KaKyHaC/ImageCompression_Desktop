package ImageCompressionLib.Utils.Functions.Opc.Experimental

import ImageCompressionLib.Constants.TWO
import ImageCompressionLib.Containers.Type.DataOpc

class StegoMasking {
    companion object {
        @JvmStatic
        fun divideTwo(dataOpc: DataOpc): Unit {
            dataOpc.N /= TWO
        }

        @JvmStatic
        fun multiTwo(dataOpc: DataOpc): Unit {
            dataOpc.N *= TWO
        }

        @JvmStatic
        fun multiBase(dataOpc: DataOpc): Unit {
            dataOpc.base[0] = (dataOpc.base[0] * 2).toShort()
        }
    }
}