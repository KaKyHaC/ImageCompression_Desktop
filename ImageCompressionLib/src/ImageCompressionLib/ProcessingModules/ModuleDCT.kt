package ImageCompressionLib.ProcessingModules


import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.TripleShortMatrix
import ImageCompressionLib.Constants.State
import ImageCompressionLib.Constants.TypeQuantization
import ImageCompressionLib.Utils.Objects.DctConvertor

import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.reflect.KFunction1

/**
 * Created by Димка on 19.09.2016.
 */
class ModuleDCT(private val tripleShortMatrix: TripleShortMatrix, //    private TripleShortMatrix resTripleShortMatrix;
                private val flag: Flag) {
    private var a: DctConvertor
    private var b: DctConvertor
    private var c: DctConvertor

    init {

//        if (tripleShortMatrix.state == State.Yenl)
//        //new code . Does it is needed ?
//            tripleShortMatrix.state = State.YBR

        val state = if (tripleShortMatrix.state == State.DCT) DctConvertor.State.DCT else DctConvertor.State.ORIGIN

        a = DctConvertor(tripleShortMatrix.a, state, TypeQuantization.luminosity, flag)
        b = DctConvertor(tripleShortMatrix.b, state, TypeQuantization.Chromaticity, flag)
        c = DctConvertor(tripleShortMatrix.c, state, TypeQuantization.Chromaticity, flag)

    }//        this.resTripleShortMatrix=tripleShortMatrix;



    fun dataProcessing(forEach: KFunction1<DctConvertor, Array<ShortArray>>) {
//        if (tripleShortMatrix.state == State.Yenl)
//        //new code . Does it is needed ?
//            tripleShortMatrix.state = State.YBR

        forEach.invoke(a)
        forEach.invoke(b)
        forEach.invoke(c)

        processState()
    }

    fun dataProcessingInThreads(forEach: KFunction1<DctConvertor, Array<ShortArray>>) {
//        if (tripleShortMatrix.state == State.Yenl)
//        //new code . Does it is needed ?
//            tripleShortMatrix.state = State.YBR

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
    fun getYCbCrMatrix(isMultiThreads: Boolean): TripleShortMatrix{
        if(flag.isChecked(Flag.Parameter.DCT))
            return getYCbCrMatrix1(isMultiThreads)
        else {
            setState(DctConvertor.State.ORIGIN)
            processState()
            return tripleShortMatrix
        }
    }
    fun getDCTMatrix(isMultiThreads: Boolean): TripleShortMatrix{
        if(flag.isChecked(Flag.Parameter.DCT))
            return getDCTMatrix1(isMultiThreads)
        else {
            setState(DctConvertor.State.DCT)
            processState()
            return tripleShortMatrix
        }
    }

    private fun getYCbCrMatrix1(isMultiThreads: Boolean): TripleShortMatrix {
        when (tripleShortMatrix.state) {
            State.DCT -> if (isMultiThreads)
                dataProcessingInThreads(DctConvertor::getMatrixOrigin)
            else
                dataProcessing(DctConvertor::getMatrixOrigin)
        }
        return if (tripleShortMatrix.state == State.YBR || tripleShortMatrix.state == State.Yenl) tripleShortMatrix else  throw Exception("State not correct")
    }
    fun getDCTMatrix1(isMultiThreads: Boolean): TripleShortMatrix {
        when (tripleShortMatrix.state) {
            State.YBR, State.Yenl -> if (isMultiThreads)
                dataProcessingInThreads(DctConvertor::getMatrixDct)
            else
                dataProcessing(DctConvertor::getMatrixDct)
        }

        return if (tripleShortMatrix.state == State.DCT) tripleShortMatrix else throw Exception("state not correct")
    }
    private fun processState(){
        tripleShortMatrix.state = if (a.state == DctConvertor.State.DCT) State.DCT else State.YBR
        if (tripleShortMatrix.a.size > tripleShortMatrix.b.size && tripleShortMatrix.state == State.YBR)
            tripleShortMatrix.state = State.Yenl
    }
    private fun setState(state:DctConvertor.State){
        a = DctConvertor(tripleShortMatrix.a, state, TypeQuantization.luminosity, flag)
        b = DctConvertor(tripleShortMatrix.b, state, TypeQuantization.Chromaticity, flag)
        c = DctConvertor(tripleShortMatrix.c, state, TypeQuantization.Chromaticity, flag)
    }

    //    public TripleShortMatrix getMatrix() {
    //        return tripleShortMatrix;
    //    }
}
