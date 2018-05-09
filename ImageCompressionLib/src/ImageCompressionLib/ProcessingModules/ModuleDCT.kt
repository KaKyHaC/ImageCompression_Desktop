package ImageCompressionLib.ProcessingModules


import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.TripleShortMatrix
import ImageCompressionLib.Constants.State
import ImageCompressionLib.Constants.TypeQuantization
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Utils.Objects.DctConvertor

import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.reflect.KFunction1

/**
 * Created by Димка on 19.09.2016.
 */
class ModuleDCT(private val tripleShortMatrixOld: TripleShortMatrix) { //    private TripleShortMatrix resTripleShortMatrix;
    private val flag: Flag
    private var a: DctConvertor
    private var b: DctConvertor
    private var c: DctConvertor

    init {
        flag=tripleShortMatrixOld.parameters.flag
//        if (tripleShortMatrixOld.state == State.Yenl)
//        //new code . Does it is needed ?
//            tripleShortMatrixOld.state = State.YBR

        val state = if (tripleShortMatrixOld.state == State.DCT) DctConvertor.State.DCT else DctConvertor.State.ORIGIN

        a = DctConvertor(tripleShortMatrixOld.a, state, TypeQuantization.luminosity, flag)
        b = DctConvertor(tripleShortMatrixOld.b, state, TypeQuantization.Chromaticity, flag)
        c = DctConvertor(tripleShortMatrixOld.c, state, TypeQuantization.Chromaticity, flag)

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
            setState(DctConvertor.State.ORIGIN)
            processState()
            return tripleShortMatrixOld
        }
    }
    fun getDCTMatrix(isMultiThreads: Boolean): TripleShortMatrix {
        if(flag.isChecked(Flag.Parameter.DCT))
            return getDCTMatrix1(isMultiThreads)
        else {
            setState(DctConvertor.State.DCT)
            processState()
            return tripleShortMatrixOld
        }
    }

    private fun getYCbCrMatrix1(isMultiThreads: Boolean): TripleShortMatrix {
        when (tripleShortMatrixOld.state) {
            State.DCT -> if (isMultiThreads)
                dataProcessingInThreads(DctConvertor::getMatrixOrigin)
            else
                dataProcessing(DctConvertor::getMatrixOrigin)
        }
        return if (tripleShortMatrixOld.state == State.YBR || tripleShortMatrixOld.state == State.Yenl) tripleShortMatrixOld else  throw Exception("State not correct")
    }
    private fun getDCTMatrix1(isMultiThreads: Boolean): TripleShortMatrix {
        when (tripleShortMatrixOld.state) {
            State.YBR, State.Yenl -> if (isMultiThreads)
                dataProcessingInThreads(DctConvertor::getMatrixDct)
            else
                dataProcessing(DctConvertor::getMatrixDct)
        }

        return if (tripleShortMatrixOld.state == State.DCT) tripleShortMatrixOld else throw Exception("state not correct")
    }
    private fun processState(){
        tripleShortMatrixOld.state = if (a.state == DctConvertor.State.DCT) State.DCT else State.YBR
        if (tripleShortMatrixOld.a.width > tripleShortMatrixOld.b.width&& tripleShortMatrixOld.state == State.YBR)
            tripleShortMatrixOld.state = State.Yenl
    }
    private fun setState(state:DctConvertor.State){
        a = DctConvertor(tripleShortMatrixOld.a, state, TypeQuantization.luminosity, flag)
        b = DctConvertor(tripleShortMatrixOld.b, state, TypeQuantization.Chromaticity, flag)
        c = DctConvertor(tripleShortMatrixOld.c, state, TypeQuantization.Chromaticity, flag)
    }

    //    public TripleShortMatrix getMatrix() {
    //        return tripleShortMatrixOld;
    //    }
}
