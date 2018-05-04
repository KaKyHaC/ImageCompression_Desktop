package ImageCompressionLib.ProcessingModules.ModuleOPC

import ImageCompressionLib.Containers.DataOpcOld
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.TripleDataOpcMatrixOld
import ImageCompressionLib.Containers.TripleShortMatrixOld
import ImageCompressionLib.Utils.Objects.OpcConvertor
import java.util.ArrayList
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ModuleOpc {
    public class ModuleOpcOld : AbsModuleOPC {
        private var a: OpcConvertor
        private var b:OpcConvertor
        private var c:OpcConvertor
        var isAsyn: Boolean = false

        constructor(tripleShortMatrixOld: TripleShortMatrixOld, parameters: Parameters, isAsyn: Boolean=true): super(tripleShortMatrixOld, parameters.flag){
            this.isAsyn = isAsyn

            a = OpcConvertor(tripleShortOld.a, parameters)
            b = OpcConvertor(tripleShortOld.b, parameters)
            c = OpcConvertor(tripleShortOld.c, parameters)
        }

        constructor(tripleDataOpcOld: TripleDataOpcMatrixOld, parameters: Parameters, isAsyn: Boolean=true):super(tripleDataOpcOld, parameters.flag) {
            this.isAsyn=isAsyn

            a = OpcConvertor(tripleDataOpcOld.a, parameters)
            b = OpcConvertor(tripleDataOpcOld.b, parameters)
            c = OpcConvertor(tripleDataOpcOld.c, parameters)
        }


        fun directOPC() {
            if (state === AbsModuleOPC.State.OPC || isReady)
                return

            appendTimeManager("direct OPC")
            tripleDataOpcOld.a = a!!.dataOpcOlds
            appendTimeManager("get A")
            tripleDataOpcOld.b = b!!.getDataOpcOlds()
            appendTimeManager("get B")
            tripleDataOpcOld.c = c!!.getDataOpcOlds()
            appendTimeManager("get C")

            isReady = true
        }

        fun reverseOPC() {
            if (state === AbsModuleOPC.State.Data || isReady)
                return

            appendTimeManager("start reOPC")
            tripleShortOld.a = a!!.dataOrigin
            appendTimeManager("get A")
            tripleShortOld.b = b!!.getDataOrigin()
            appendTimeManager("get B")
            tripleShortOld.c = c!!.getDataOrigin()
            appendTimeManager("get C")

            isReady = true
        }

        fun directOPCMultiThreads() { //multy thred
            if (state === AbsModuleOPC.State.OPC || isReady)
                return

            val executorService = Executors.newFixedThreadPool(3)
            val futures = ArrayList<Future<Array<Array<DataOpcOld>>>>()

            appendTimeManager("direct OPC")
            futures.add(executorService.submit(Callable { a!!.dataOpcOlds }))
            futures.add(executorService.submit(Callable { b!!.getDataOpcOlds() }))
            futures.add(executorService.submit(Callable { c!!.getDataOpcOlds() }))

            try {
                tripleDataOpcOld.b = futures[1].get()
                appendTimeManager("get B")
                tripleDataOpcOld.c = futures[2].get()
                appendTimeManager("get C")
                tripleDataOpcOld.a = futures[0].get()
                appendTimeManager("get A")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            isReady = true
        }

        fun reverseOPCMultiThreads() {// create getTripleShortOld()() with corect size of b and c (complite)  //multy thred
            if (state === AbsModuleOPC.State.Data || isReady)
                return

            val executorService = Executors.newFixedThreadPool(3)
            val futures = ArrayList<Future<Array<ShortArray>>>()

            appendTimeManager("start reOPC")

            futures.add(executorService.submit(Callable { a!!.dataOrigin }))
            futures.add(executorService.submit(Callable { b!!.getDataOrigin() }))
            futures.add(executorService.submit(Callable { c!!.getDataOrigin() }))

            try {
                tripleShortOld.b = futures[1].get()
                appendTimeManager("get B")
                tripleShortOld.c = futures[2].get()
                appendTimeManager("get C")
                tripleShortOld.a = futures[0].get()
                appendTimeManager("get A")
                //            getTripleShortOld()().setC(futures.get(2).get());
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            isReady = true
        }

        fun directOPCParallel() { //multy thread
            if (state === AbsModuleOPC.State.OPC || isReady)
                return

            //omp parallel
            run {
                tripleDataOpcOld.a = a!!.dataOpcOlds
                tripleDataOpcOld.b = b!!.getDataOpcOlds()
                tripleDataOpcOld.c = c!!.getDataOpcOlds()
            }
            isReady = true
        }

        fun reverseOPCParallel() {
            if (state === AbsModuleOPC.State.Data || isReady)
                return

            //omp parallel
            run {
                tripleShortOld.a = a!!.dataOrigin
                tripleShortOld.b = b!!.getDataOrigin()
                tripleShortOld.c = c!!.getDataOrigin()
            }
            isReady = true
        }

        fun directOpcGlobalBase(n: Int, m: Int) {
            if (state === AbsModuleOPC.State.OPC || isReady)
                return
            tripleDataOpcOld.a = a!!.getDataOpcs(n, m) //TODO set a
            tripleDataOpcOld.b = b!!.getDataOpcs(n, m) //TODO set a
            tripleDataOpcOld.c = c!!.getDataOpcs(n, m) //TODO set a

            isReady = true
        }


        @Deprecated("")
        fun getMatrix(isAsync: Boolean): TripleShortMatrixOld {
            if (state !== AbsModuleOPC.State.Data && !isReady) {
                if (isAsync)
                    reverseOPCMultiThreads()
                else
                    reverseOPC()
            }

            return tripleShortOld
        }

        @Deprecated("")
        fun getBoxOfOpc(isAsync: Boolean): TripleDataOpcMatrixOld {
            if (state !== AbsModuleOPC.State.OPC && !isReady) {
                if (isAsync)
                    directOPCMultiThreads()
                else
                    directOPC()
            }

            return tripleDataOpcOld
        }

        private fun appendTimeManager(s: String) {
            //        TimeManager.getInstance().append(s);
        }

        override fun direct(shortMatrixOld: TripleShortMatrixOld): TripleDataOpcMatrixOld {
            return getBoxOfOpc(isAsyn)
        }

        override fun reverce(dataOpcMatrixOld: TripleDataOpcMatrixOld): TripleShortMatrixOld {
            return getMatrix(isAsyn)
        }

    }