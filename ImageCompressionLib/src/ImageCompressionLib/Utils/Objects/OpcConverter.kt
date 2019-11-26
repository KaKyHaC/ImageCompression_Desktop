package ImageCompressionLib.Utils.Objects

import ImageCompressionLib.Containers.EncryptParameters
import ImageCompressionLib.Containers.Matrix.DataOpcMatrix
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.Size
import ImageCompressionLib.Utils.Functions.Opc.OpcProcess
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils

class OpcConverter {
    enum class State {
        Opc, Origin
    }
    //TODO replace forEach with for()
    //TODO do all process in one loop

    private val shortMatrix: Matrix<Short>
    private val dataOpcMatrix: DataOpcMatrix
    private val parameters: Parameters
    var isReady = false
        private set
    var state: State
        private set

    private lateinit var splitedShortMatrix: Matrix<Matrix<Short>>
//    private lateinit var splitedDataOpcMatrix:Matrix<DataOpcMatrix>//TODO make local


    constructor(dataOrigin: Array<Array<Short>>, parameters: Parameters) {
        this.shortMatrix = ShortMatrix.valueOf(dataOrigin)
        this.parameters = parameters
        state = State.Origin
        val size = calculateDataOpcMatrixSize(
            Size(dataOrigin.size, dataOrigin[0].size),
            parameters.unitSize
        )
        dataOpcMatrix = DataOpcMatrix(size.width, size.height, parameters.unitSize)
    }

    constructor(dataOrigin: ShortMatrix, parameters: Parameters) {
        this.shortMatrix = dataOrigin
        this.parameters = parameters
        state = State.Origin
        val size = calculateDataOpcMatrixSize(dataOrigin.size, parameters.unitSize)
        dataOpcMatrix = DataOpcMatrix(size.width, size.height, parameters.unitSize)
    }


    constructor(dataOpcMatrix: DataOpcMatrix, parameters: Parameters) {
        this.dataOpcMatrix = (dataOpcMatrix)
        this.parameters = parameters
        state = State.Opc
        val size = calculateShortMatrixSize(dataOpcMatrix.size, parameters.unitSize)
        shortMatrix = ShortMatrix(size.width, size.height)
    }

    private fun createSplitedMatrix() {
        if (!parameters.flag.isChecked(Flag.Parameter.Enlargement))
            splitedShortMatrix =
                shortMatrix.split(parameters.unitSize.width, parameters.unitSize.height)
        else
            splitedShortMatrix = shortMatrix.splitWithZeroIterator(
                parameters.unitSize.width,
                parameters.unitSize.height,
                0
            )
    }

    private fun calculateDataOpcMatrixSize(imageSize: Size, unitSize: Size): Size {
        var w = imageSize.width / unitSize.width
        var h = imageSize.height / unitSize.height
        if (imageSize.width % unitSize.width != 0) w++
        if (imageSize.height % unitSize.height != 0) h++
        return Size(w, h)
    }

    private fun calculateShortMatrixSize(dataOpcMatrixSize: Size, unitSze: Size): Size {
        return Size(
            dataOpcMatrixSize.width * unitSze.width,
            dataOpcMatrixSize.height * unitSze.height
        )
    }

    private fun beforeDirectOpc() {
        dataOpcMatrix.forEach() { i, j, value ->
            OpcProcess.preDirectOpcProcess(parameters, splitedShortMatrix[i, j], value)
            return@forEach null
        }
    }

    private fun afterReverseOpc() {
        dataOpcMatrix.forEach { i, j, value ->
            OpcProcess.afterReverseOpcProcess(parameters, value, splitedShortMatrix[i, j])
            return@forEach null
        }
    }

    private fun setGlobalBase() {
//        if(!parameters.flag.isChecked(Flag.Parameter.GlobalBase))
//            return
        val splittedDataOpcMatrix =
            dataOpcMatrix.split(parameters.sameBaseSize.width, parameters.sameBaseSize.height)
        splittedDataOpcMatrix.forEach { i, j, value ->
            OpcUtils.setSameBaseIn(value)
            return@forEach null
        }
    }

    private fun directOpc() {
        dataOpcMatrix.forEach { i, j, value ->
            OpcProcess.directOPC(parameters, splitedShortMatrix[i, j], value)
            return@forEach null
        }
    }

    private fun reverseOPC() {
        dataOpcMatrix.forEach { i, j, value ->
            OpcProcess.reverseOPC(parameters, value, splitedShortMatrix[i, j])
            return@forEach null
        }
    }

    private fun directOpcWithMessageAt(encParameters: EncryptParameters, message: ByteVector) {
        if (encParameters.steganography == null)
            throw Exception("steganography==null")
        val position = encParameters.steganography!!.stegoPosition
        val stegoGeter = encParameters.steganography!!.stegoBlockKeygenFactory.invoke()
        dataOpcMatrix.forEach() { i, j, value ->
            if (message.hasNextBit() && stegoGeter.isUseNextBlock())
                OpcProcess.directOpcWithMessageAt(
                    parameters,
                    splitedShortMatrix[i, j],
                    value,
                    message.getNextBoolean(),
                    position
                )
            else
                OpcProcess.directOpcWithMessageAt(
                    parameters,
                    splitedShortMatrix[i, j],
                    value,
                    false,
                    position
                )
            return@forEach null
        }
    }

    private fun reverseOPCWithMessageAt(encParameters: EncryptParameters): ByteVector {
        val res = ByteVector()
        if (encParameters.steganography == null)
            throw Exception("steganography==null")
        val position = encParameters.steganography!!.stegoPosition
        val stegoGeter = encParameters.steganography!!.stegoBlockKeygenFactory.invoke()
        dataOpcMatrix.forEach { i, j, value ->
            val tmp = OpcProcess.reverseOpcWithMessageAt(
                parameters,
                value,
                splitedShortMatrix[i, j],
                position
            )
            if (stegoGeter.isUseNextBlock()) res.append(tmp)
            return@forEach null
        }
        encParameters.message = res
        return res
    }

    private fun directProcess(encParameters: EncryptParameters?, message: ByteVector?) {
        createSplitedMatrix()

        beforeDirectOpc()

        if (parameters.flag.isChecked(Flag.Parameter.GlobalBase))
            setGlobalBase()

        if (encParameters?.steganography != null && message != null)
            directOpcWithMessageAt(encParameters, message)
        else
            directOpc()
    }

    private fun reverseProcess(encParameters: EncryptParameters?): ByteVector? {
        createSplitedMatrix()

        var res: ByteVector? = null
        if (encParameters?.steganography != null)
            res = reverseOPCWithMessageAt(encParameters)
        else
            reverseOPC()

        afterReverseOpc()

        return res
    }


    fun getDataOrigin(encParameters: EncryptParameters? = null): Pair<Matrix<Short>, ByteVector?> {
        var m: ByteVector? = null
        if (state == State.Opc && !isReady) {
            m = reverseProcess(encParameters)
            isReady = true
        }

        return Pair(shortMatrix, m)
    }

    /**
     * calculate(if need) DataOpcs with global base for (nxm)
     * @param n - vertical size of same base
     * @param m - horizonlat size of same base
     * @return matrix of DataOpcOld with same base
     */
    fun getDataOpcs(): Matrix<DataOpc> {
        if (state == State.Origin && !isReady) {
            directProcess(null, null)
            isReady = true
        }
        return dataOpcMatrix
    }

    fun getDataOpcs(encParameters: EncryptParameters?, message: ByteVector?): Matrix<DataOpc> {
        if (state == State.Origin && !isReady) {
            directProcess(encParameters, message)
            isReady = true
        }
        return dataOpcMatrix
    }
}