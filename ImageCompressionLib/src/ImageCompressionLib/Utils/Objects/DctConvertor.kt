package ImageCompressionLib.Utils.Objects


import ImageCompressionLib.Constants.SIZEOFBLOCK
import ImageCompressionLib.Constants.TypeQuantization
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Utils.Functions.DCTMultiThread

/**
 * Class for transformation between DCT and Origin
 * use min Size and max Time
 * Created by Димка on 08.08.2016.
 */
class DctConvertor(private val dataOrigin: Matrix<Short>, state: State, private val tq: TypeQuantization, private val flag: Flag) {
    //    private boolean isReady=false;

    var state: State? = null
        private set
    private val dataProcessed: Matrix<Short>

    private val Width: Int
    private val Height: Int
    private var duWidth: Int = 0
    private var duHeight: Int = 0

    /**
     * Do main calculation if need
     * @return matrix with original date
     */
    //        if(!isReady&&state==State.DCT)
    //            dataProcessing();
    fun getMatrixOrigin(): Matrix<Short>{
            if (state == State.DCT)
                dataProcessing()

            return dataProcessed
        }

    /**
     * Do main calculation if need
     * @return matrix with DCT date
     */
    //        if(!isReady&&state==State.ORIGIN)
    //            dataProcessing();
    fun getMatrixDct(): Matrix<Short>{

            if (state == State.ORIGIN)
                dataProcessing()

            return dataProcessed
        }

    enum class State {
        DCT, ORIGIN
    }

    init {
        dataProcessed = dataOrigin//= new short[dataOrigin.length][dataOrigin[0].length];// = dataOrigin
        Width = dataOrigin.width
        Height = dataOrigin.height

        this.state = state
        sizeCalculate()
    }

    private fun sizeCalculate() {
        duWidth = Width / DCTMultiThread.SIZEOFBLOCK
        duHeight = Height / DCTMultiThread.SIZEOFBLOCK
        if (Width % DCTMultiThread.SIZEOFBLOCK != 0)
            duWidth++
        if (Height % DCTMultiThread.SIZEOFBLOCK != 0)
            duHeight++
        //   createMatrix();
    }

    /**
     * subtract the [0][0] element from each [%8][%8]
     */
    private fun preProsses() {
        for (i in 0 until duWidth) {
            for (j in 0 until duHeight) {

                val curX = i * DCTMultiThread.SIZEOFBLOCK
                val curY = j * DCTMultiThread.SIZEOFBLOCK
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
        for (x in 0 until DCTMultiThread.SIZEOFBLOCK) {
            for (y in 0 until DCTMultiThread.SIZEOFBLOCK) {
                var value: Short = 0
                val curX = i * DCTMultiThread.SIZEOFBLOCK + x
                val curY = j * DCTMultiThread.SIZEOFBLOCK + y
                if (curX < Width && curY < Height)
                    value = dataOrigin[curX,curY]
                buffer[x,y] = value
                // DU[i][j].setValue(val,x,y);
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

        buf = DCTMultiThread.directDCT(buf)

        if (flag.quantization == Flag.QuantizationState.First)
            DCTMultiThread.directQuantization(tq, buf)
        return buf
    }

    private fun reverceDCT(buf: Matrix<Short>): Matrix<Short>{
        var buf = buf
        if (flag.quantization == Flag.QuantizationState.First)
            DCTMultiThread.reverseQuantization(tq, buf)

        buf = DCTMultiThread.reverseDCT(buf)

        if (flag.isChecked(Flag.Parameter.Alignment))
            plus128(buf)

        return buf
    }

    /**
     * set 8x8 matrix from buffer into dataProcessed started at [i][j]
     * @param buffer - matrix with information
     */
    private fun fillDateProcessed(i: Int, j: Int, buffer: Matrix<Short>) {
        for (x in 0 until DCTMultiThread.SIZEOFBLOCK) {
            for (y in 0 until DCTMultiThread.SIZEOFBLOCK) {

                val curX = i * DCTMultiThread.SIZEOFBLOCK + x
                val curY = j * DCTMultiThread.SIZEOFBLOCK + y
                if (curX < Width && curY < Height)
                    dataProcessed[curX,curY] = buffer[x,y]
            }
        }
    }

    /**
     * do transmormation between DCT and Origin states
     */
    private fun dataProcessing() {
        var buf=ShortMatrix(SIZEOFBLOCK, SIZEOFBLOCK).toMatrix()
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
