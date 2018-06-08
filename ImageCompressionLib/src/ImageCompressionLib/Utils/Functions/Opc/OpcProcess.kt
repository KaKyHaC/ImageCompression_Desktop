package ImageCompressionLib.Utils.Functions.Opc

import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcDirectLongAndBI
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcDirectUseOnlyLong
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcDirectWithMessageAt
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcDirectWithMessageAtFirst
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcReverceWithMessageAt
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcReverceWithMessageAtFirst
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcReverseDefault
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcReverseUseOnlyLong
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils.Companion.DCminus
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils.Companion.DCplus
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils.Companion.FindBase
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils.Companion.MakeSigned
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils.Companion.MakeUnSigned

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
        fun directOpcWithMessageAt(parameters: Parameters, dataOrigin: Matrix<Short>, DataOpc: DataOpc, message: Boolean, position: Int) {
            val flag = parameters.flag

            if (flag.isChecked(Flag.Parameter.LongCode))
                throw Exception("can't write message using LongCode")

//            if (position == 0)
//                OpcDirectWithMessageAtFirst(dataOrigin, DataOpc, message)
//            else
            OpcDirectWithMessageAt(dataOrigin, DataOpc, message, position)
        }
        @JvmStatic
        fun reverseOpcWithMessageAt(parameters: Parameters, DataOpc: DataOpc, dataOrigin: Matrix<Short>, position: Int): Boolean {
            val flag = parameters.flag
            if (flag.isChecked(Flag.Parameter.LongCode))
                throw Exception("can't read message using LongCode")

//            val message = if (position == 0)
//                OpcReverceWithMessageAtFirst(dataOrigin, DataOpc)
//            else
            return OpcReverceWithMessageAt(dataOrigin, DataOpc, position)

//            return message
        }
    }
}
