package ImageCompressionLib.ProcessingModules


import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.TripleShortMatrix
import ImageCompressionLib.Constants.State
import ImageCompressionLib.Constants.TypeQuantization
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Utils.Functions.Dct.DctUniversalAlgorithm
import ImageCompressionLib.Utils.Objects.DctConvertor

import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.reflect.KFunction1

/**
 * Created by Димка on 19.09.2016.
 */
class ModuleDCT(private val tripleShortMatrixOld: TripleShortMatrix) { //    private TripleShortMatrix resTripleShortMatrix;
    private val parameter:Parameters
    private val flag:Flag
    private var a: DctConvertor
    private var b: DctConvertor
    private var c: DctConvertor
    private var state:DctConvertor.State

    init {
        parameter=tripleShortMatrixOld.parameters
        flag=parameter.flag
        val dctUtils=DctUniversalAlgorithm(parameter.unitSize)
//        if (tripleShortMatrixOld.state == State.Yenl)
//        //new code . Does it is needed ?
//            tripleShortMatrixOld.state = State.YBR

        state = if (tripleShortMatrixOld.state == State.Dct) DctConvertor.State.DCT else DctConvertor.State.ORIGIN

        a = DctConvertor(tripleShortMatrixOld.a, state, TypeQuantization.luminosity,parameter,dctUtils.copy() )
        b = DctConvertor(tripleShortMatrixOld.b, state, TypeQuantization.Chromaticity, parameter,dctUtils.copy())
        c = DctConvertor(tripleShortMatrixOld.c, state, TypeQuantization.Chromaticity, parameter,dctUtils)

    }//        this.resTripleShortMatrix=tripleShortMatrixOld;



    fun dataProcessing(forEach: KFunction1<DctConvertor, Matrix<Short>>) {
//        if (tripleShortMatrixOld.state == State.Yenl)
//        //new code . Does it is needed ?
//            tripleShortMatrixOld.state = State.YBR

        forEach.invoke(a)
        forEach.invoke(b)
        forEach.invoke(c)

        processState()
    }

    fun dataProcessingInThreads(forEach: KFunction1<DctConvertor, Matrix<Short>>) {
//        if (tripleShortMatrixOld.state == State.Yenl)
//        //new code . Does it is needed ?
//            tripleShortMatrixOld.state = State.YBR

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

        processState()
    }
    fun getYCbCrMatrix(isMultiThreads: Boolean): TripleShortMatrix {
        if(flag.isChecked(Flag.Parameter.DCT))
            return getYCbCrMatrix1(isMultiThreads)
        else {
//            setState(DctConvertor.State.ORIGIN)
//            processState()
            return tripleShortMatrixOld
        }
    }
    fun getDCTMatrix(isMultiThreads: Boolean): TripleShortMatrix {
        if(flag.isChecked(Flag.Parameter.DCT))
            return getDCTMatrix1(isMultiThreads)
        else {
//            setState(DctConvertor.State.DCT)
//            processState()
            return tripleShortMatrixOld
        }
    }

    private fun getYCbCrMatrix1(isMultiThreads: Boolean): TripleShortMatrix {
        when (state) {
            DctConvertor.State.DCT -> if (isMultiThreads)
                dataProcessingInThreads(DctConvertor::getMatrixOrigin)
            else
                dataProcessing(DctConvertor::getMatrixOrigin)
        }
        return tripleShortMatrixOld
//        return if (tripleShortMatrixOld.state == State.YBR || tripleShortMatrixOld.state == State.Yenl) tripleShortMatrixOld else  throw Exception("State not correct")
    }
    private fun getDCTMatrix1(isMultiThreads: Boolean): TripleShortMatrix {
        when (state) {
            DctConvertor.State.ORIGIN-> if (isMultiThreads)
                dataProcessingInThreads(DctConvertor::getMatrixDct)
            else
                dataProcessing(DctConvertor::getMatrixDct)
        }

        return tripleShortMatrixOld
//        return if (tripleShortMatrixOld.state == State.DCT) tripleShortMatrixOld else throw Exception("tripleShortMatrixOld.state:${tripleShortMatrixOld.state}!=DCT")
    }
    private fun processState(){
        tripleShortMatrixOld.state = if (a.state == DctConvertor.State.DCT) State.Dct else State.Origin
    }

    //    public TripleShortMatrix getMatrix() {
    //        return tripleShortMatrixOld;
    //    }
}
