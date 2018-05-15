package ImageCompressionLib.ProcessingModules

import ImageCompressionLib.Constants.DEFAULT_PASSWORD
import ImageCompressionLib.Containers.*
import ImageCompressionLib.Containers.Matrix.DataOpcMatrix
import ImageCompressionLib.Constants.State
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Utils.Functions.Encryption
import ImageCompressionLib.Utils.Objects.OpcConvertor
import java.util.ArrayList
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ModuleOpc {
    private var a: OpcConvertor
    private var b: OpcConvertor
    private var c: OpcConvertor
    val parameters: Parameters
    var isAsyn: Boolean = false

    var tripleShortMatrix:TripleShortMatrix?=null
    var tripleDataOpcMatrix:TripleDataOpcMatrix?=null

    constructor(tripleShortMatrix: TripleShortMatrix, isAsyn: Boolean = true) {
        this.isAsyn = isAsyn
        this.parameters = tripleShortMatrix.parameters
        this.tripleShortMatrix=tripleShortMatrix

        a = OpcConvertor(ShortMatrix.valueOf(tripleShortMatrix.a), parameters)
        b = OpcConvertor(ShortMatrix.valueOf(tripleShortMatrix.b), parameters)
        c = OpcConvertor(ShortMatrix.valueOf(tripleShortMatrix.c), parameters)
    }

    constructor(tripleDataOpcMatrix: TripleDataOpcMatrix, isAsyn: Boolean = true) {
        this.isAsyn = isAsyn
        this.parameters = tripleDataOpcMatrix.parameters
        this.tripleDataOpcMatrix= tripleDataOpcMatrix

        a = OpcConvertor(DataOpcMatrix.valueOf(tripleDataOpcMatrix.a), parameters)
        b = OpcConvertor(DataOpcMatrix.valueOf(tripleDataOpcMatrix.b), parameters)
        c = OpcConvertor(DataOpcMatrix.valueOf(tripleDataOpcMatrix.c), parameters)
    }


    private fun directOPCMultiThreads(encryptParameters: EncryptParameters?=null): TripleDataOpcMatrix { //multy thred
        val executorService = Executors.newFixedThreadPool(3)
        val futures = ArrayList<Future<Matrix<DataOpc>>>()

        appendTimeManager("direct OPC")
        var mes= arrayOfNulls<ByteVector>(3)
        if(encryptParameters!=null){
            if(encryptParameters.message!=null) {
                val tmp = encryptParameters.message!!/(3)
                mes=tmp as Array<ByteVector?>
            }
        }
        futures.add(executorService.submit(Callable { a.getDataOpcs(encryptParameters,mes[0]) }))
        futures.add(executorService.submit(Callable { b.getDataOpcs(encryptParameters,mes[1]) }))
        futures.add(executorService.submit(Callable { c.getDataOpcs(encryptParameters,mes[2]) }))


        val b = (futures[1].get())
        appendTimeManager("get B")
        val c = (futures[2].get())
        appendTimeManager("get C")
        val a = (futures[0].get())
        appendTimeManager("get A")

        val res = TripleDataOpcMatrix(a, b, c, parameters)
        return res

    }

    private fun reverseOPCMultiThreads(encryptParameters: EncryptParameters?=null): Pair<TripleShortMatrix, ByteVector?> {
        val executorService = Executors.newFixedThreadPool(3)
        val futures = ArrayList<Future<Pair<Matrix<Short>, ByteVector?>>>()

        appendTimeManager("start reOPC")

        futures.add(executorService.submit(Callable { a.getDataOrigin(encryptParameters) }))
        futures.add(executorService.submit(Callable { b.getDataOrigin(encryptParameters) }))
        futures.add(executorService.submit(Callable { c.getDataOrigin(encryptParameters) }))

        val (a1, v1) = futures[0].get()
        val (b1, v2) = futures[1].get()
        val (c1, v3) = futures[2].get()
        var resM:ByteVector?=null
        if(v1!=null&&v2!=null&&v3!=null)
            resM=v1.trim()+v2.trim()+v3.trim()
//        val resM1=v1?.concat(v2?:ByteVector())?:ByteVector().concat(v3?:ByteVector())
//        val resM = v1?.concat(v2?.concat(v3 ?: ByteVector(0)) ?: ByteVector(0))
        return Pair(TripleShortMatrix(a1, b1, c1, parameters, State.DCT), resM)

    }

    private fun appendTimeManager(s: String) {}

    private fun encode(encryptParameters: EncryptParameters){
        if(parameters.flag.isChecked(Flag.Parameter.Encryption)) {
            Encryption.encode(tripleDataOpcMatrix,encryptParameters.password ?: DEFAULT_PASSWORD)
        }
    }

    fun getTripleDataOpcMatrix(encryptParameters: EncryptParameters?): TripleDataOpcMatrix {
        if(tripleDataOpcMatrix==null) {
            tripleDataOpcMatrix = directOPCMultiThreads(encryptParameters)
            encryptParameters?.let { encode(encryptParameters) }
        }
        return tripleDataOpcMatrix!!
    }

    var m:ByteVector?=null
        private set
    fun getTripleShortMatrix(encryptParameters: EncryptParameters?): Pair<TripleShortMatrix, ByteVector?> {
        if(tripleShortMatrix==null) {
            encryptParameters?.let { encode(encryptParameters) }
            val (tripleShortMatrix, mes) = reverseOPCMultiThreads(encryptParameters)
            this.tripleShortMatrix = tripleShortMatrix
            m=mes
        }
        return Pair(tripleShortMatrix!!,m)
    }
}

