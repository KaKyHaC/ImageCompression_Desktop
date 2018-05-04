package ImageCompressionLib.Utils.Objects

import ImageCompressionLib.Containers.*
import ImageCompressionLib.Containers.Matrix.DataOpcMatrix
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.Size
import ImageCompressionLib.Utils.Functions.*

class OpcConvertor {
    enum class State {
        Opc, Origin
    }
    //TODO replace forEach with for()
    //TODO do all process in one loop

    private val shortMatrix: ShortMatrix
    private val dataOpcMatrix: DataOpcMatrix
    private val parameters:Parameters
    var isReady = false
        private set
    var state: State
        private set

    private lateinit var splitedShortMatrix: Matrix<Matrix<Short>>
//    private lateinit var splitedDataOpcMatrix:Matrix<DataOpcMatrix>//TODO make local


    constructor(dataOrigin: Array<Array<Short>>, parameters: Parameters) {
        this.shortMatrix = ShortMatrix.valueOf(dataOrigin)
        this.parameters=parameters
        state = State.Origin
        val size=calculataDataOpcMatrixSize(parameters)
        dataOpcMatrix= DataOpcMatrix(size.width, size.height, parameters.unitSize)
    }

    constructor(dataOpcMatrix: Array<Array<DataOpc>>, parameters: Parameters){
        this.dataOpcMatrix= DataOpcMatrix(dataOpcMatrix)
        this.parameters=parameters
        state = State.Opc
        shortMatrix= ShortMatrix(parameters.imageSize.width, parameters.imageSize.height)
    }
    private fun createSplitedMatrix(){
        splitedShortMatrix=shortMatrix.split(parameters.unitSize.width,parameters.unitSize.height)
    }
    private fun calculataDataOpcMatrixSize(parameters: Parameters): Size {
        var w=parameters.imageSize.width/parameters.unitSize.width
        var h=parameters.imageSize.height/parameters.unitSize.height
        if(parameters.imageSize.width%parameters.unitSize.width!=0)w++
        if(parameters.imageSize.height%parameters.unitSize.height!=0)h++
        return Size(w, h)
    }
    private fun beforDirectOpc(){
        dataOpcMatrix.forEach() { i, j, value ->
            OpcProcess.preDirectOpcProcess(parameters, ShortMatrix.valueOf(splitedShortMatrix[i, j]), value)
            return@forEach null
        }
    }
    private fun afterReverceOpc(){
        dataOpcMatrix.forEach() { i, j, value ->
            OpcProcess.afterReverceOpcProcess(parameters,value, ShortMatrix.valueOf(splitedShortMatrix[i, j]) )
            return@forEach null
        }
    }
    private fun setGlobalBase(){
        if(!parameters.flag.isChecked(Flag.Parameter.GlobalBase))
            return
        val splitedDataOpcMatrix=dataOpcMatrix.split(parameters.sameBaseSize.width,parameters.sameBaseSize.height) as Matrix<DataOpcMatrix>
        splitedDataOpcMatrix.forEach(){i, j, value ->
            OpcUtils.setSameBaseIn(value)
            return@forEach null
        }
    }
    private fun directOpc(){
        dataOpcMatrix.forEach(){i, j, value ->
            OpcProcess.directOPC(parameters, ShortMatrix.valueOf(splitedShortMatrix[i,j]),value)
            return@forEach null
        }
    }
    private fun reverceOPC(){
        dataOpcMatrix.forEach(){i, j, value ->
            OpcProcess.reverseOPC(parameters,value, ShortMatrix.valueOf(splitedShortMatrix[i,j]) )
            return@forEach null
        }
    }
    private fun directOpcWithMessageAt(position: Int,message: ByteVector){
        dataOpcMatrix.forEach(){i, j, value ->
            if(message.hasNextBit())
                OpcProcess.directOpcWithMessageAt(parameters, ShortMatrix.valueOf(splitedShortMatrix[i,j]) ,value,message.getNextBoolean(),position)
            else
                OpcProcess.directOpcWithMessageAt(parameters, ShortMatrix.valueOf(splitedShortMatrix[i,j]) ,value,false,position)
            return@forEach null
        }
    }
    private fun reverceOPCWithMessageAt(position: Int): ByteVector {
        val res= ByteVector()
        dataOpcMatrix.forEach(){i, j, value ->
            res.append(OpcProcess.reverseOpcWithMessageAt(parameters,value, ShortMatrix.valueOf(splitedShortMatrix[i,j]) ,position))
            return@forEach null
        }
        return res
    }
    private fun directProcess(position: Int?,message: ByteVector?){
        createSplitedMatrix()

        beforDirectOpc()

        if(parameters.flag.isChecked(Flag.Parameter.GlobalBase))
            setGlobalBase()

        if(position!=null&&message!=null)
            directOpcWithMessageAt(position,message)
        else
            directOpc()
    }
    private fun reverceProcess(position: Int?): ByteVector?{
        createSplitedMatrix()

        var res: ByteVector?=null
        if(position!=null)
            res=reverceOPCWithMessageAt(position)
        else
            reverceOPC()

        afterReverceOpc()

        return res
    }


    fun getDataOrigin(position: Int?=null): Array<Array<Short>> {
        if (state == State.Opc && !isReady) {
            reverceProcess(position)
            isReady = true
        }

        return shortMatrix.toArrayShort()
    }

    /**
     * calculate(if need) DataOpcs with global base for (nxm)
     * @param n - vertical size of same base
     * @param m - horizonlat size of same base
     * @return matrix of DataOpcOld with same base
     */
    fun getDataOpcs(position: Int?=null,message: ByteVector?=null): Array<Array<DataOpc>> {
        if (state == State.Origin && !isReady) {
            directProcess(position, message)
            isReady = true
        }
        return dataOpcMatrix.toDataOpcArray()
    }
}