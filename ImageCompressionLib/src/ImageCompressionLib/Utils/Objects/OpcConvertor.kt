package ImageCompressionLib.Utils.Objects

import ImageCompressionLib.Containers.*
import ImageCompressionLib.Containers.Matrix.DataOpcMatrix
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.Size
import ImageCompressionLib.Utils.Functions.Opc.OpcProcess
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils

class OpcConvertor {
    enum class State {
        Opc, Origin
    }
    //TODO replace forEach with for()
    //TODO do all process in one loop

    private val shortMatrix: Matrix<Short>
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
        val size=calculataDataOpcMatrixSize(Size(dataOrigin.size,dataOrigin[0].size),parameters.unitSize)
        dataOpcMatrix= DataOpcMatrix(size.width, size.height, parameters.unitSize)
    }
    constructor(dataOrigin: ShortMatrix, parameters: Parameters) {
        this.shortMatrix = dataOrigin
        this.parameters=parameters
        state = State.Origin
        val size=calculataDataOpcMatrixSize(dataOrigin.size,parameters.unitSize)
        dataOpcMatrix= DataOpcMatrix(size.width, size.height, parameters.unitSize)
    }


    constructor(dataOpcMatrix: DataOpcMatrix, parameters: Parameters){
        this.dataOpcMatrix= (dataOpcMatrix)
        this.parameters=parameters
        state = State.Opc
        val size=calculateShortMatrixSize(dataOpcMatrix.size,parameters.unitSize)
        shortMatrix= ShortMatrix(size.width, size.height)
    }
    private fun createSplitedMatrix(){
        splitedShortMatrix=shortMatrix.splitWithZeroIterator(parameters.unitSize.width,parameters.unitSize.height,0)
    }
    private fun calculataDataOpcMatrixSize(imageSize: Size, unitSize:Size): Size {
        var w= imageSize.width/unitSize.width
        var h= imageSize.height/unitSize.height
        if(imageSize.width%unitSize.width!=0)w++
        if(imageSize.height%unitSize.height!=0)h++
        return Size(w, h)
    }
    private fun calculateShortMatrixSize(dataOpcMatrixSize:Size,unitSze:Size): Size {
        return Size(dataOpcMatrixSize.width*unitSze.width,dataOpcMatrixSize.height*unitSze.height)
    }
    private fun beforDirectOpc(){
        dataOpcMatrix.forEach() { i, j, value ->
            OpcProcess.preDirectOpcProcess(parameters, splitedShortMatrix[i, j], value)
            return@forEach null
        }
    }
    private fun afterReverceOpc(){
        dataOpcMatrix.forEach() { i, j, value ->
            OpcProcess.afterReverceOpcProcess(parameters,value, splitedShortMatrix[i, j])
            return@forEach null
        }
    }
    private fun setGlobalBase(){
//        if(!parameters.flag.isChecked(Flag.Parameter.GlobalBase))
//            return
        val splitedDataOpcMatrix=dataOpcMatrix.split(parameters.sameBaseSize.width,parameters.sameBaseSize.height)
        splitedDataOpcMatrix.forEach(){i, j, value ->
            OpcUtils.setSameBaseIn(value)
            return@forEach null
        }
    }
    private fun directOpc(){
        dataOpcMatrix.forEach(){i, j, value ->
            OpcProcess.directOPC(parameters, splitedShortMatrix[i,j],value)
            return@forEach null
        }
    }
    private fun reverceOPC(){
        dataOpcMatrix.forEach(){i, j, value ->
            OpcProcess.reverseOPC(parameters,value, splitedShortMatrix[i,j])
            return@forEach null
        }
    }
    private fun directOpcWithMessageAt(position: Int,message: ByteVector){
        dataOpcMatrix.forEach(){i, j, value ->
            if(message.hasNextBit())
                OpcProcess.directOpcWithMessageAt(parameters, splitedShortMatrix[i,j] ,value,message.getNextBoolean(),position)
            else
                OpcProcess.directOpcWithMessageAt(parameters, splitedShortMatrix[i,j] ,value,false,position)
            return@forEach null
        }
    }
    private fun reverceOPCWithMessageAt(position: Int): ByteVector {
        val res= ByteVector()
        dataOpcMatrix.forEach(){i, j, value ->
            res.append(OpcProcess.reverseOpcWithMessageAt(parameters,value, splitedShortMatrix[i,j] ,position))
            return@forEach null
        }
        return res
    }
    private fun directProcess(position: Int?,message: ByteVector?){
        createSplitedMatrix()

        beforDirectOpc()

        if(parameters.flag.isChecked(Flag.Parameter.GlobalBase))
            setGlobalBase()

        if(position!=null&&message!=null) {
            if (parameters.flag.isChecked(Flag.Parameter.Steganography))//TODO remove
                directOpcWithMessageAt(position, message)
        }else
            directOpc()
    }
    private fun reverceProcess(position: Int?): ByteVector?{
        createSplitedMatrix()

        var res: ByteVector?=null
        if(position!=null) {
            if (parameters.flag.isChecked(Flag.Parameter.Steganography))//TODO remove
                res = reverceOPCWithMessageAt(position)
        } else
            reverceOPC()

        afterReverceOpc()

        return res
    }


    fun getDataOrigin(position: Int?=null): Pair<Matrix<Short>,ByteVector?> {
        var m:ByteVector?=null
        if (state == State.Opc && !isReady) {
            m=reverceProcess(position)
            isReady = true
        }

        return Pair(shortMatrix,m)
    }

    /**
     * calculate(if need) DataOpcs with global base for (nxm)
     * @param n - vertical size of same base
     * @param m - horizonlat size of same base
     * @return matrix of DataOpcOld with same base
     */
    fun getDataOpcs(position: Int?=null,message: ByteVector?=null): Matrix<DataOpc> {
        if (state == State.Origin && !isReady) {
            directProcess(position, message)
            isReady = true
        }
        return dataOpcMatrix
    }
}