package ImageCompressionLib.utils.objects


import ImageCompressionLib.constants.TypeQuantization
import ImageCompressionLib.containers.matrix.Matrix
import ImageCompressionLib.containers.matrix.ShortMatrix
import ImageCompressionLib.containers.Parameters
import ImageCompressionLib.containers.type.Flag
import ImageCompressionLib.utils.functions.dct.DctUniversalAlgorithm

/**
 * Class for transformation between DCT and Origin
 * use min Size and max Time
 * Created by Димка on 08.08.2016.
 */
class DctConvertor(private val dataOrigin: Matrix<Short>, state: State, private val tq: TypeQuantization
                   , private val parameters:Parameters,val dctUtil:DctUniversalAlgorithm) {
    //    private boolean isReady=false;

    enum class State {
        DCT, ORIGIN
    }

    var state: State? = null
        private set
    private val dataProcessed: Matrix<Short>

    private val Width: Int
    private val Height: Int
    private var duWidth: Int = 0
    private var duHeight: Int = 0
    private val flag=parameters.flag

    /**
     * Do main calculation if need
     * @return matrix with original date
     */
    fun getMatrixOrigin(): Matrix<Short>{
            if (state == State.DCT)
                dataProcessing()
            return dataProcessed
        }

    /**
     * Do main calculation if need
     * @return matrix with DCT date
     */
    fun getMatrixDct(): Matrix<Short>{

            if (state == State.ORIGIN)
                dataProcessing()

            return dataProcessed
        }

    init {
        dataProcessed = dataOrigin//= new short[dataOrigin.length][dataOrigin[0].length];// = dataOrigin
        Width = dataOrigin.width
        Height = dataOrigin.height

        this.state = state
        val duS=parameters.calculateMatrixOfUnitSize(dataOrigin.size,parameters.unitSize)
        duWidth=duS.width
        duHeight=duS.height
    }
    

    /**
     * subtract the [0][0] element from each [%8][%8]
     */
    private fun preProsses() {
        for (i in 0 until duWidth) {
            for (j in 0 until duHeight) {

                val curX = i * parameters.unitSize.width
                val curY = j * parameters.unitSize.height
                if (i != 0 && j != 0)
                    dataOrigin[curX,curY] = (dataOrigin[0,0] - dataOrigin[curX,curY]).toShort()
            }
        }
    }

    /**
     * copy 8x8 matrix from dataOrigin started at [i][j] into buffer
     * @param buffer - target matrix to copy
     * @return buffer
     */
    private fun fillBufferForDU(i: Int, j: Int, buffer: Matrix<Short>): Matrix<Short> {
        val w=parameters.unitSize.width
        val h=parameters.unitSize.height
        for (x in 0 until parameters.unitSize.width) {
            for (y in 0 until parameters.unitSize.height) {
                var value: Short = 0
                val curX = i * w + x
                val curY = j * h + y
                if (curX < Width && curY < Height)
                    value = dataOrigin[curX,curY]
                buffer[x,y] = value
            }
        }
        return buffer
    }


    internal interface FIConvertor {
        fun convert(buf: Matrix<Short>): Matrix<Short>
    }

    private fun directDCT(buf: Matrix<Short>): Matrix<Short>{
        var buf = buf
        if (flag.isChecked(Flag.Parameter.Alignment))
            minus128(buf)

        buf = dctUtil.directDCT(buf)

        if (flag.isChecked(Flag.Parameter.Quantization))
            dctUtil.directQuantization(buf)
        return buf
    }

    private fun reverceDCT(buf: Matrix<Short>): Matrix<Short>{
        var buf = buf
        if (flag.isChecked(Flag.Parameter.Quantization))
            dctUtil.reverseQuantization( buf)

        buf = dctUtil.reverseDCT(buf)

        if (flag.isChecked(Flag.Parameter.Alignment))
            plus128(buf)

        return buf
    }

    /**
     * set 8x8 matrix from buffer into dataProcessed started at [i][j]
     * @param buffer - matrix with information
     */
    private fun fillDateProcessed(i: Int, j: Int, buffer: Matrix<Short>) {
        val w=parameters.unitSize.width
        val h=parameters.unitSize.height
        for (x in 0 until w) {
            for (y in 0 until h) {
                val curX = i * w+ x
                val curY = j * h+ y
                if (curX < Width && curY < Height)
                    dataProcessed[curX,curY] = buffer[x,y]
            }
        }
    }

    /**
     * do transmormation between DCT and Origin states
     */
    private fun dataProcessing() {
        var buf=ShortMatrix(parameters.unitSize.width,parameters.unitSize.height).toMatrix()
        if (state == State.DCT)
            preProsses()

        val convertor = if (state == State.ORIGIN) this::directDCT  else this::reverceDCT

        for (i in 0 until duWidth) {
            for (j in 0 until duHeight) {
                buf = fillBufferForDU(i, j, buf)
                buf = convertor.invoke(buf)
                fillDateProcessed(i, j, buf)
            }
        }
        if (state == State.ORIGIN)
            preProsses()


        if (state == State.ORIGIN)
            state = State.DCT
        else if (state == State.DCT)
            state = State.ORIGIN
        //        isReady=true;
    }

    private fun minus128(arr: Matrix<Short>) {
        for (i in 0 until arr.width) {
            for (j in 0 until arr.height){
                arr[i,j] =(arr[i,j]- 128).toShort()
            }
        }
    }

    private fun plus128(arr: Matrix<Short>) {
        for (i in 0 until arr.width) {
            for (j in 0 until arr.height) {
                arr[i,j] =(arr[i,j]+128).toShort()
            }
        }
    }
}
