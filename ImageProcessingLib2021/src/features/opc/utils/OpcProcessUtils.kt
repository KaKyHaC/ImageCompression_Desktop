package features.opc.utils

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc
import data_model.types.Size
import features.opc.algorithms.OpcDefaultAlgorithms
import features.opc.algorithms.OpcLongOnlyAlgorithms
import java.lang.Exception


object OpcProcessUtils {

    data class PreOpcParams(
            val makeUnsigned: Boolean = true, // flag.isChecked(Flag.Parameter.DCT)
            val removeAC: Boolean = true // flag.isChecked(Flag.Parameter.DC)
    )

    data class OpcParams(
            val useLongCode: Boolean = false // flag.isChecked(Flag.Parameter.LongCode)
    )

    fun directProcess(preOpcParams: PreOpcParams, opcParams: OpcParams, dataOrigin: Matrix<Short>): DataOpc {
        val builder = DataOpc.Builder(dataOrigin.size)
        preDirectOpcProcess(preOpcParams, dataOrigin, builder)
        return directOPC(opcParams, dataOrigin, builder).build() ?: throw Exception("")
    }

    fun reverseProcess(preOpcParams: PreOpcParams, dataOpc: DataOpc, originSize: Size): Matrix<Short> {
        val dataOrigin = Matrix.create(originSize) { _, _ -> 0.toShort() }
        reverseApplyProcess(preOpcParams, dataOpc, dataOrigin)
        return dataOrigin
    }

    fun reverseApplyProcess(preOpcParams: PreOpcParams, dataOpc: DataOpc, dataOrigin: Matrix<Short>) {
        reverseOPC(dataOpc, dataOrigin)
        afterReverseOpcProcess(preOpcParams, dataOpc, dataOrigin)
    }

    @JvmStatic
    fun preDirectOpcProcess(preOpcParams: PreOpcParams, dataOrigin: Matrix<Short>, dataOpc: DataOpc.Builder) {
        if (preOpcParams.makeUnsigned)
            DataOpcUtils.Sign.makeUnSigned(dataOrigin, dataOpc)
        if (preOpcParams.removeAC)
            DataOpcUtils.AC.minus(dataOrigin, dataOpc)

        DataOpcUtils.Base.findBase(dataOrigin, dataOpc)
    }

    @JvmStatic
    fun afterReverseOpcProcess(preOpcParams: PreOpcParams, dataOpc: DataOpc, dataOrigin: Matrix<Short>) {
        if (preOpcParams.removeAC)
            DataOpcUtils.AC.plus(dataOrigin, dataOpc)
        if (preOpcParams.makeUnsigned)
            DataOpcUtils.Sign.makeSigned(dataOrigin, dataOpc)
    }

    @JvmStatic
    fun directOPC(opcParams: OpcParams, dataOrigin: Matrix<Short>, dataOpc: DataOpc.Builder): DataOpc.Builder {
        return if (opcParams.useLongCode)
            OpcLongOnlyAlgorithms.direct(dataOrigin, dataOpc)
        else
            OpcDefaultAlgorithms.direct(dataOrigin, dataOpc)
    }

    @JvmStatic
    fun reverseOPC(dataOpc: DataOpc, dataOrigin: Matrix<Short>) {
        when (dataOpc) {
            is DataOpc.BI -> OpcDefaultAlgorithms.applyReverse(dataOrigin, dataOpc)
            is DataOpc.Long -> OpcLongOnlyAlgorithms.applyReverse(dataOrigin, dataOpc)
        }
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