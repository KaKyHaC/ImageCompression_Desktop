package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Containers.DataOpc
import ImageCompressionLib.Containers.Flag
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.ShortMatrix

fun preDirectOpcProcess(parameters: Parameters, dataOrigin: ShortMatrix, DataOpc: DataOpc) {
    val flag = parameters.flag
    if (flag.isChecked(Flag.Parameter.DCT))
        MakeUnSigned(dataOrigin, DataOpc)
    if (flag.isChecked(Flag.Parameter.DC))
        DCminus(dataOrigin, DataOpc)

    FindBase(dataOrigin, DataOpc)
}
fun afterReverceOpcProcess(parameters: Parameters,DataOpc: DataOpc,dataOrigin: ShortMatrix) {
    val flag = parameters.flag

    if (flag.isChecked(Flag.Parameter.DC))
        DCplus(dataOrigin, DataOpc)
    if (flag.isChecked(Flag.Parameter.DCT))
        MakeSigned(dataOrigin, DataOpc)
}
fun directOPC(parameters: Parameters, dataOrigin: ShortMatrix, DataOpc: DataOpc) {
    if (parameters.flag.isChecked(Flag.Parameter.LongCode))
        OpcDirectUseOnlyLong(dataOrigin, DataOpc)
    else
        OpcDirectLongAndBI(dataOrigin, DataOpc)
}

fun reverseOPC(parameters: Parameters,DataOpc: DataOpc,dataOrigin: ShortMatrix) {
    val flag = parameters.flag
    if (flag.isChecked(Flag.Parameter.LongCode))
        OpcReverseUseOnlyLong(dataOrigin, DataOpc)
    else
        OpcReverseDefault(dataOrigin, DataOpc)


}
fun directOpcWithMessageAt(parameters: Parameters, dataOrigin: ShortMatrix, DataOpc: DataOpc,message:Boolean,position: Int) {
    val flag = parameters.flag

    if (flag.isChecked(Flag.Parameter.LongCode))
        throw Exception("can't write message using LongCode")
    
    if(position==0)
        OpcDirectWithMessageAtFirst(dataOrigin, DataOpc,message)
    else
        OpcDirectWithMessageAt(dataOrigin,DataOpc,message,position)
}

fun reverseOpcWithMessageAt(parameters: Parameters,DataOpc: DataOpc,dataOrigin: ShortMatrix,position: Int):Boolean {
    val flag = parameters.flag
    if (flag.isChecked(Flag.Parameter.LongCode))
        throw Exception("can't read message using LongCode")

    var message=false
    if(position==0)
        message=OpcReverceWithMessageAtFirst(dataOrigin, DataOpc)
    else
        message=OpcReverceWithMessageAt(dataOrigin,DataOpc,position)

    return message
}
