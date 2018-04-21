package ImageCompressionLib.ProcessingModules


import ImageCompressionLib.Containers.Flag
import ImageCompressionLib.Containers.TripleShortMatrix
import ImageCompressionLib.Constants.State
import ImageCompressionLib.Constants.TypeQuantization
import ImageCompressionLib.Utils.Objects.DctConvertor

import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Created by Димка on 19.09.2016.
 */
class ModuleDCT(private val tripleShortMatrix: TripleShortMatrix, //    private TripleShortMatrix resTripleShortMatrix;
                private val flag: Flag) {
    private val a: DctConvertor
    private val b: DctConvertor
    private val c: DctConvertor

    init {

        if (tripleShortMatrix.state == State.Yenl)
        //new code . Does it is needed ?
            tripleShortMatrix.state = State.YBR

        val state = if (tripleShortMatrix.state == State.DCT) DctConvertor.State.DCT else DctConvertor.State.ORIGIN

        a = DctConvertor(tripleShortMatrix.a, state, TypeQuantization.luminosity, flag)
        b = DctConvertor(tripleShortMatrix.b, state, TypeQuantization.Chromaticity, flag)
        c = DctConvertor(tripleShortMatrix.c, state, TypeQuantization.Chromaticity, flag)

    }//        this.resTripleShortMatrix=tripleShortMatrix;



    fun dataProcessing(forEach: (e:DctConvertor)->Unit) {
        if (tripleShortMatrix.state == State.Yenl)
        //new code . Does it is needed ?
            tripleShortMatrix.state = State.YBR

        forEach.invoke(a)
        forEach.invoke(b)
        forEach.invoke(c)

        val state = if (a.state == DctConvertor.State.DCT) State.DCT else State.YBR
        tripleShortMatrix.state = state
        if (tripleShortMatrix.a.size > tripleShortMatrix.b.size && tripleShortMatrix.state == State.YBR)
            tripleShortMatrix.state = State.Yenl
    }

    fun dataProcessingInThreads(forEach: (e:DctConvertor)->Unit) {
        if (tripleShortMatrix.state == State.Yenl)
        //new code . Does it is needed ?
            tripleShortMatrix.state = State.YBR

        val executorService = Executors.newFixedThreadPool(3)
        val futures = arrayOfNulls<Future<*>>(3)

        futures[0] = executorService.submit { forEach.invoke(a) }
        futures[1] = executorService.submit { forEach.invoke(b) }
        futures[2] = executorService.submit { forEach.invoke(c) }

        for (future in futures) {
            try {
                future!!.get()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

        }

        val state = if (a.state == DctConvertor.State.DCT) State.DCT else State.YBR
        tripleShortMatrix.state = state
        if (tripleShortMatrix.a.size > tripleShortMatrix.b.size && tripleShortMatrix.state == State.YBR)
            tripleShortMatrix.state = State.Yenl
    }

    fun getYCbCrMatrix(isMultiThreads: Boolean): TripleShortMatrix? {
        when (tripleShortMatrix.state) {
            State.DCT -> if (isMultiThreads)
                dataProcessingInThreads(IThraedable { it.getMatrixOrigin() })
            else
                dataProcessing(IThraedable { it.getMatrixOrigin() })
        }
        return if (tripleShortMatrix.state == State.YBR || tripleShortMatrix.state == State.Yenl) tripleShortMatrix else null
    }

    fun getDCTMatrix(isMultiThreads: Boolean): TripleShortMatrix? {
        when (tripleShortMatrix.state) {
            State.YBR, State.Yenl -> if (isMultiThreads)
                dataProcessingInThreads(DctConvertor::getMatrixDct)
            else
                dataProcessing(DctConvertor::getMatrixDct)
        }

        return if (tripleShortMatrix.state == State.DCT) tripleShortMatrix else null
    }

    //    public TripleShortMatrix getMatrix() {
    //        return tripleShortMatrix;
    //    }
}
