package ImageCompressionLib.Utils.Functions.Opc

import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.opcDirectLongAndBI
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.opcDirectUseOnlyLong
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.opcReverseDefault
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.opcReverseUseOnlyLong
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils.Companion.DCminus
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils.Companion.DCplus
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils.Companion.FindBase
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils.Companion.MakeSigned
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils.Companion.MakeUnSigned

class OpcProcess {
    companion object {
        @JvmStatic
        fun preDirectOpcProcess(
            parameters: Parameters,
            dataOrigin: Matrix<Short>,
            DataOpc: DataOpc
        ) {
            val flag = parameters.flag

            if (flag.isChecked(Flag.Parameter.DCT))
                MakeUnSigned(dataOrigin, DataOpc)
            if (flag.isChecked(Flag.Parameter.DC))
                DCminus(dataOrigin, DataOpc)

            FindBase(dataOrigin, DataOpc)
        }

        @JvmStatic
        fun afterReverceOpcProcess(
            parameters: Parameters,
            DataOpc: DataOpc,
            dataOrigin: Matrix<Short>
        ) {
            val flag = parameters.flag

            if (flag.isChecked(Flag.Parameter.DC))
                DCplus(dataOrigin, DataOpc)
            if (flag.isChecked(Flag.Parameter.DCT))
                MakeSigned(dataOrigin, DataOpc)
        }

        @JvmStatic
        fun directOPC(parameters: Parameters, dataOrigin: Matrix<Short>, DataOpc: DataOpc) {
            if (parameters.flag.isChecked(Flag.Parameter.LongCode))
                opcDirectUseOnlyLong(dataOrigin, DataOpc)
            else
                opcDirectLongAndBI(dataOrigin, DataOpc)
        }

        @JvmStatic
        fun reverseOPC(parameters: Parameters, DataOpc: DataOpc, dataOrigin: Matrix<Short>) {
            val flag = parameters.flag
            if (flag.isChecked(Flag.Parameter.LongCode))
                opcReverseUseOnlyLong(dataOrigin, DataOpc)
            else
                opcReverseDefault(dataOrigin, DataOpc)
        }

        @JvmStatic
        fun directOpcWithMessageAt(
            parameters: Parameters,
            dataOrigin: Matrix<Short>,
            dataOpc: DataOpc,
            message: Boolean,
            position: Int
        ) {
            val flag = parameters.flag

            if (flag.isChecked(Flag.Parameter.LongCode))
                throw Exception("can't write message using LongCode")

            OpcAlgorithms.opcDirectWithMessageAt(dataOrigin, dataOpc, message, position)
            val maxLen = DataOpc.getLengthOfCode(dataOpc.base, dataOrigin.size)
            val len = dataOpc.N.toByteArray().size
            if (len > maxLen) {
                println("len $len>max $maxLen")
                OpcAlgorithms.opcDirectDefault(dataOrigin, dataOpc)
            }
        }

        @JvmStatic
        fun reverseOpcWithMessageAt(
            parameters: Parameters,
            DataOpc: DataOpc,
            dataOrigin: Matrix<Short>,
            position: Int
        ): Boolean {
            val flag = parameters.flag
            if (flag.isChecked(Flag.Parameter.LongCode))
                throw Exception("can't read message using LongCode")

            val message = if (position == 0)
                OpcAlgorithms.opcReverseWithMessageAtFirst(dataOrigin, DataOpc)
            else
                OpcAlgorithms.opcReverseWithMessageAt(dataOrigin, DataOpc, position)

            return message
        }
    }
}
