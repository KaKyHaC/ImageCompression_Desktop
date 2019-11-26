package ImageCompressionLib.Utils.Opc

import ImageCompressionLib.Data.Primitives.DataOpc
import ImageCompressionLib.Data.Type.Flag
import ImageCompressionLib.Data.Parameters
import ImageCompressionLib.Data.Matrix.Matrix
import ImageCompressionLib.Utils.Opc.Experimental.OpcAlgorithmsExperimentel
import ImageCompressionLib.Utils.Opc.OpcAlgorithms.Companion.OpcDirectLongAndBI
import ImageCompressionLib.Utils.Opc.OpcAlgorithms.Companion.OpcDirectUseOnlyLong
import ImageCompressionLib.Utils.Opc.OpcAlgorithms.Companion.OpcReverceWithMessageAt
import ImageCompressionLib.Utils.Opc.OpcAlgorithms.Companion.OpcReverceWithMessageAtFirst
import ImageCompressionLib.Utils.Opc.OpcAlgorithms.Companion.OpcReverseDefault
import ImageCompressionLib.Utils.Opc.OpcAlgorithms.Companion.OpcReverseUseOnlyLong
import ImageCompressionLib.Utils.Opc.OpcUtils.Companion.DCminus
import ImageCompressionLib.Utils.Opc.OpcUtils.Companion.DCplus
import ImageCompressionLib.Utils.Opc.OpcUtils.Companion.FindBase
import ImageCompressionLib.Utils.Opc.OpcUtils.Companion.MakeSigned
import ImageCompressionLib.Utils.Opc.OpcUtils.Companion.MakeUnSigned

class OpcProcess {
    companion object {
        @JvmStatic
        fun preDirectOpcProcess(parameters: Parameters, dataOrigin: Matrix<Short>, DataOpc: DataOpc) {
            val flag = parameters.flag

            if (flag.isChecked(Flag.Parameter.DCT))
                MakeUnSigned(dataOrigin, DataOpc)
            if (flag.isChecked(Flag.Parameter.DC))
                DCminus(dataOrigin, DataOpc)

            FindBase(dataOrigin, DataOpc)
        }
        @JvmStatic
        fun afterReverceOpcProcess(parameters: Parameters, DataOpc: DataOpc, dataOrigin: Matrix<Short>) {
            val flag = parameters.flag

            if (flag.isChecked(Flag.Parameter.DC))
                DCplus(dataOrigin, DataOpc)
            if (flag.isChecked(Flag.Parameter.DCT))
                MakeSigned(dataOrigin, DataOpc)
        }
        @JvmStatic
        fun directOPC(parameters: Parameters, dataOrigin: Matrix<Short>, DataOpc: DataOpc) {
            if (parameters.flag.isChecked(Flag.Parameter.LongCode))
                OpcDirectUseOnlyLong(dataOrigin, DataOpc)
            else
                OpcDirectLongAndBI(dataOrigin, DataOpc)
        }
        @JvmStatic
        fun reverseOPC(parameters: Parameters, DataOpc: DataOpc, dataOrigin: Matrix<Short>) {
            val flag = parameters.flag
            if (flag.isChecked(Flag.Parameter.LongCode))
                OpcReverseUseOnlyLong(dataOrigin, DataOpc)
            else
                OpcReverseDefault(dataOrigin, DataOpc)
        }
        @JvmStatic
        fun directOpcWithMessageAt(parameters: Parameters, dataOrigin: Matrix<Short>, dataOpc: DataOpc, message: Boolean, position: Int) {
            val flag = parameters.flag

            if (flag.isChecked(Flag.Parameter.LongCode))
                throw Exception("can't write message using LongCode")

            OpcAlgorithmsExperimentel.OpcDirectStegoAt(dataOrigin,dataOpc,message,position)
            val maxLen= DataOpc.getLengthOfCode(dataOpc.base,dataOrigin.size)
            val len = dataOpc.N.toByteArray().size
            if(len>maxLen) {
                println("len $len>max $maxLen")
                OpcAlgorithms.OpcDirectDefault(dataOrigin,dataOpc)
            }
        }
        @JvmStatic
        fun reverseOpcWithMessageAt(parameters: Parameters, DataOpc: DataOpc, dataOrigin: Matrix<Short>, position: Int): Boolean {
            val flag = parameters.flag
            if (flag.isChecked(Flag.Parameter.LongCode))
                throw Exception("can't read message using LongCode")

            val message = if (position == 0)
                OpcReverceWithMessageAtFirst(dataOrigin, DataOpc)
            else
                OpcReverceWithMessageAt(dataOrigin, DataOpc, position)

            return message
        }
    }
}
