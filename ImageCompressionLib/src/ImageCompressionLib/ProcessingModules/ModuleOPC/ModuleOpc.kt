package ImageCompressionLib.ProcessingModules.ModuleOPC

import ImageCompressionLib.Containers.DataOpcOld
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.TripleDataOpcMatrix
import ImageCompressionLib.Containers.TripleShortMatrix
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Utils.Objects.OpcConvertor
import ImageCompressionLib.Utils.Objects.OpcConvertorOld
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

        constructor(tripleShortMatrix: TripleShortMatrix, parameters: Parameters, isAsyn: Boolean=true): super(tripleShortMatrix, parameters.flag){
            this.isAsyn = isAsyn

            a = OpcConvertor(tripleShort.a, parameters)
            b = OpcConvertor(tripleShort.b, parameters)
            c = OpcConvertor(tripleShort.c, parameters)
        }

        constructor(tripleDataOpc: TripleDataOpcMatrix, parameters: Parameters, isAsyn: Boolean=true):super(tripleDataOpc, parameters.flag) {
            this.isAsyn=isAsyn

            a = OpcConvertor(tripleDataOpc.a, parameters)
            b = OpcConvertor(tripleDataOpc.b, parameters)
            c = OpcConvertor(tripleDataOpc.c, parameters)
        }


        fun directOPC() {
            if (state === AbsModuleOPC.State.OPC || isReady)
                return

            appendTimeManager("direct OPC")
            tripleDataOpc.a = a!!.dataOpcOlds
            appendTimeManager("get A")
            tripleDataOpc.b = b!!.getDataOpcOlds()
            appendTimeManager("get B")
            tripleDataOpc.c = c!!.getDataOpcOlds()
            appendTimeManager("get C")

            isReady = true
        }

        fun reverseOPC() {
            if (state === AbsModuleOPC.State.Data || isReady)
                return

            appendTimeManager("start reOPC")
            tripleShort.a = a!!.dataOrigin
            appendTimeManager("get A")
            tripleShort.b = b!!.getDataOrigin()
            appendTimeManager("get B")
            tripleShort.c = c!!.getDataOrigin()
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
                tripleDataOpc.b = futures[1].get()
                appendTimeManager("get B")
                tripleDataOpc.c = futures[2].get()
                appendTimeManager("get C")
                tripleDataOpc.a = futures[0].get()
                appendTimeManager("get A")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            isReady = true
        }

        fun reverseOPCMultiThreads() {// create getTripleShort()() with corect size of b and c (complite)  //multy thred
            if (state === AbsModuleOPC.State.Data || isReady)
                return

            val executorService = Executors.newFixedThreadPool(3)
            val futures = ArrayList<Future<Array<ShortArray>>>()

            appendTimeManager("start reOPC")

            futures.add(executorService.submit(Callable { a!!.dataOrigin }))
            futures.add(executorService.submit(Callable { b!!.getDataOrigin() }))
            futures.add(executorService.submit(Callable { c!!.getDataOrigin() }))

            try {
                tripleShort.b = futures[1].get()
                appendTimeManager("get B")
                tripleShort.c = futures[2].get()
                appendTimeManager("get C")
                tripleShort.a = futures[0].get()
                appendTimeManager("get A")
                //            getTripleShort()().setC(futures.get(2).get());
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
                tripleDataOpc.a = a!!.dataOpcOlds
                tripleDataOpc.b = b!!.getDataOpcOlds()
                tripleDataOpc.c = c!!.getDataOpcOlds()
            }
            isReady = true
        }

        fun reverseOPCParallel() {
            if (state === AbsModuleOPC.State.Data || isReady)
                return

            //omp parallel
            run {
                tripleShort.a = a!!.dataOrigin
                tripleShort.b = b!!.getDataOrigin()
                tripleShort.c = c!!.getDataOrigin()
            }
            isReady = true
        }

        fun directOpcGlobalBase(n: Int, m: Int) {
            if (state === AbsModuleOPC.State.OPC || isReady)
                return
            tripleDataOpc.a = a!!.getDataOpcs(n, m) //TODO set a
            tripleDataOpc.b = b!!.getDataOpcs(n, m) //TODO set a
            tripleDataOpc.c = c!!.getDataOpcs(n, m) //TODO set a

            isReady = true
        }


        @Deprecated("")
        fun getMatrix(isAsync: Boolean): TripleShortMatrix {
            if (state !== AbsModuleOPC.State.Data && !isReady) {
                if (isAsync)
                    reverseOPCMultiThreads()
                else
                    reverseOPC()
            }

            return tripleShort
        }

        @Deprecated("")
        fun getBoxOfOpc(isAsync: Boolean): TripleDataOpcMatrix {
            if (state !== AbsModuleOPC.State.OPC && !isReady) {
                if (isAsync)
                    directOPCMultiThreads()
                else
                    directOPC()
            }

            return tripleDataOpc
        }

        private fun appendTimeManager(s: String) {
            //        TimeManager.getInstance().append(s);
        }

        override fun direct(shortMatrix: TripleShortMatrix): TripleDataOpcMatrix {
            return getBoxOfOpc(isAsyn)
        }

        override fun reverce(dataOpcMatrix: TripleDataOpcMatrix): TripleShortMatrix {
            return getMatrix(isAsyn)
        }

    }