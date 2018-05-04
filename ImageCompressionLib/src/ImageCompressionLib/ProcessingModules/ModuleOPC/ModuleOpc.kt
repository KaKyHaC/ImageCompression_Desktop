package ImageCompressionLib.ProcessingModules.ModuleOPC

import ImageCompressionLib.Containers.*
import ImageCompressionLib.Containers.Matrix.DataOpcMatrix
import ImageCompressionLib.Constants.State
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Utils.Objects.OpcConvertor
import java.util.ArrayList
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ModuleOpc {
    private var a: OpcConvertor
    private var b: OpcConvertor
    private var c: OpcConvertor
    val parameters: Parameters
    var isAsyn: Boolean = false

    constructor(tripleShortMatrix: TripleShortMatrix, parameters: Parameters, isAsyn: Boolean = true) {
        this.isAsyn = isAsyn
        this.parameters = parameters

        a = OpcConvertor(ShortMatrix.valueOf(tripleShortMatrix.a), parameters)
        b = OpcConvertor(ShortMatrix.valueOf(tripleShortMatrix.b), parameters)
        c = OpcConvertor(ShortMatrix.valueOf(tripleShortMatrix.c), parameters)
    }

    constructor(tripleDataOpcOld: TripleDataOpcMatrix, parameters: Parameters, isAsyn: Boolean = true) {
        this.isAsyn = isAsyn
        this.parameters = parameters

        a = OpcConvertor(DataOpcMatrix.valueOf(tripleDataOpcOld.a), parameters)
        b = OpcConvertor(DataOpcMatrix.valueOf(tripleDataOpcOld.b), parameters)
        c = OpcConvertor(DataOpcMatrix.valueOf(tripleDataOpcOld.c), parameters)
    }


    fun directOPCMultiThreads(encryptParameters: EncryptParameters): TripleDataOpcMatrix { //multy thred
        val executorService = Executors.newFixedThreadPool(3)
        val futures = ArrayList<Future<Matrix<DataOpc>>>()

        appendTimeManager("direct OPC")
        futures.add(executorService.submit(Callable { a.getDataOpcs(encryptParameters.stegoPosition, encryptParameters.message) }))
        futures.add(executorService.submit(Callable { b.getDataOpcs(encryptParameters.stegoPosition, encryptParameters.message) }))
        futures.add(executorService.submit(Callable { c.getDataOpcs(encryptParameters.stegoPosition, encryptParameters.message) }))


        val b = (futures[1].get())
        appendTimeManager("get B")
        val c = (futures[2].get())
        appendTimeManager("get C")
        val a = (futures[0].get())
        appendTimeManager("get A")

        val res = TripleDataOpcMatrix(a, b, c, parameters)
        return res

    }

    fun reverseOPCMultiThreads(encryptParameters: EncryptParameters): Pair<TripleShortMatrix, ByteVector?> {
        val executorService = Executors.newFixedThreadPool(3)
        val futures = ArrayList<Future<Pair<Matrix<Short>, ByteVector?>>>()

        appendTimeManager("start reOPC")

        futures.add(executorService.submit(Callable { a.getDataOrigin(encryptParameters.stegoPosition) }))
        futures.add(executorService.submit(Callable { b.getDataOrigin(encryptParameters.stegoPosition) }))
        futures.add(executorService.submit(Callable { c.getDataOrigin(encryptParameters.stegoPosition) }))

        val (a1, v1) = futures[0].get()
        val (b1, v2) = futures[1].get()
        val (c1, v3) = futures[2].get()
        val resM = v1?.concat(v2?.concat(v3 ?: ByteVector(0)) ?: ByteVector(0))
        return Pair(TripleShortMatrix(a1, b1, c1, parameters, State.DCT), resM)

    }

    private fun appendTimeManager(s: String) {}
}

