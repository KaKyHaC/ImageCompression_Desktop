package features.opc2.managers

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc
import data_model.types.DataOpc2
import data_model.types.Size
import features.opc2.algorithms.OpcDefaultAlgorithms2
import features.opc2.algorithms.OpcLongOnlyAlgorithms2
import features.opc2.utils.DataOpcUtils2
import java.lang.Exception


class OpcProcessUnit2(
        val parameters: Parameters
) {

    data class Parameters(
            val makeUnsigned: Boolean = true, // flag.isChecked(Flag.Parameter.DCT)
            val removeAC: Boolean = true, // flag.isChecked(Flag.Parameter.DC)
            val useLongCode: Boolean = false, // flag.isChecked(Flag.Parameter.LongCode)
            val decreaseBase: Boolean = true,
            val useMinBase: Boolean = true
    )

    fun direct(dataOrigin: Matrix<Short>): DataOpc2 {
        val builder = DataOpc2.Builder(originSize = dataOrigin.size)
        preDirectOpcProcess(dataOrigin, builder)
        val directOPC = directOPC(dataOrigin, builder)
        afterDirectOpcProcess(directOPC)
        return directOPC.build()
    }

    fun reverse(dataOpc: DataOpc2, originSize: Size): Matrix<Short> {
        val dataOrigin = Matrix.create(originSize) { _, _ -> 0.toShort() }
        reverseApplyProcess(dataOpc, dataOrigin)
        return dataOrigin
    }

    private fun reverseApplyProcess(dataOpc: DataOpc2, dataOrigin: Matrix<Short>) {
        val dataOpcBuilder = DataOpc2.Builder(dataOpc)
        preReverseOpcProcess(dataOpcBuilder)
        reverseOPC(dataOpcBuilder, dataOrigin)
        afterReverseOpcProcess(dataOpcBuilder, dataOrigin)
    }

    private fun preDirectOpcProcess(dataOrigin: Matrix<Short>, dataOpc: DataOpc2.Builder) {
        if (parameters.makeUnsigned)
            DataOpcUtils2.Sign.makeUnSigned(dataOrigin, dataOpc)
        if (parameters.removeAC)
            DataOpcUtils2.AC.minus(dataOrigin, dataOpc)
        if (parameters.useMinBase) {
            DataOpcUtils2.Base.findMinBase(dataOrigin, dataOpc)
            DataOpcUtils2.Base.removeMinBase(dataOrigin, dataOpc)
        }

        DataOpcUtils2.Base.findMaxBase(dataOrigin, dataOpc)
    }

    private fun afterReverseOpcProcess(dataOpc: DataOpc2.Builder, dataOrigin: Matrix<Short>) {
        if (parameters.useMinBase)
            DataOpcUtils2.Base.addMinBase(dataOrigin, dataOpc)
        if (parameters.removeAC)
            DataOpcUtils2.AC.plus(dataOrigin, dataOpc)
        if (parameters.makeUnsigned)
            DataOpcUtils2.Sign.makeSigned(dataOrigin, dataOpc)
    }

    private fun directOPC(dataOrigin: Matrix<Short>, dataOpc: DataOpc2.Builder): DataOpc2.Builder {
        return if (parameters.useLongCode)
            OpcLongOnlyAlgorithms2.direct(dataOrigin, dataOpc)
        else
            OpcDefaultAlgorithms2.direct(dataOrigin, dataOpc)
    }

    private fun reverseOPC(dataOpc: DataOpc2.Builder, dataOrigin: Matrix<Short>) {
        if (parameters.useLongCode)
            OpcLongOnlyAlgorithms2.applyReverse(dataOrigin, dataOpc)
        else
            OpcDefaultAlgorithms2.applyReverse(dataOrigin, dataOpc)
    }

    private fun afterDirectOpcProcess(dataOpc: DataOpc2.Builder) {
        if (parameters.decreaseBase)
            DataOpcUtils2.Base.decreaseBase(dataOpc)
    }

    private fun preReverseOpcProcess(dataOpc: DataOpc2.Builder) {
        if (parameters.decreaseBase)
            DataOpcUtils2.Base.increaseBase(dataOpc)
    }


//    @JvmStatic
//    fun directOpcWithMessageAt(parameters: Parameters, dataOrigin: Matrix<Short>, dataOpc: DataOpc, message: Boolean, position: Int) {
//        val flag = parameters.flag
//
//        if (flag.isChecked(Flag.Parameter.LongCode))
//            throw Exception("can't write message using LongCode")
//
//        OpcAlgorithmsExperimentel.OpcDirectStegoAt(dataOrigin, dataOpc, message, position)
//        val maxLen = DataOpc.getLengthOfCode(dataOpc.base, dataOrigin.size)
//        val len = dataOpc.N.toByteArray().size
//        if (len > maxLen) {
//            println("len $len>max $maxLen")
//            OpcAlgorithms.OpcDirectDefault(dataOrigin, dataOpc)
//        }
//    }
//
//    @JvmStatic
//    fun reverseOpcWithMessageAt(parameters: Parameters, DataOpc: DataOpc, dataOrigin: Matrix<Short>, position: Int): Boolean {
//        val flag = parameters.flag
//        if (flag.isChecked(Flag.Parameter.LongCode))
//            throw Exception("can't read message using LongCode")
//
//        val message = if (position == 0)
//            OpcReverceWithMessageAtFirst(dataOrigin, DataOpc)
//        else
//            OpcReverceWithMessageAt(dataOrigin, DataOpc, position)
//
//        return message
//    }
}
