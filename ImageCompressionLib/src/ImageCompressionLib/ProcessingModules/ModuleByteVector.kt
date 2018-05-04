//package ImageCompressionLib.ProcessingModules
//
//import ImageCompressionLib.Containers.ByteVectorContainer
//import ImageCompressionLib.Containers.TripleDataOpcMatrix
//import ImageCompressionLib.Containers.Type.Flag
//
//class ModuleByteVector {
//    private enum class State{Data,Vector}
//    private val state:State
//    private var dataOld: TripleDataOpcMatrix?
//    private var vectorContainer:ByteVectorContainer?
//    private val flag: Flag
//    private var isReady=false
//    private val globalW:Int
//    private val globalH:Int
//
//    constructor(vectorContainer: ByteVectorContainer, flag: Flag) {
//        this.vectorContainer = vectorContainer
//        this.flag = flag
//        this.dataOld =null
//        state=State.Vector
//        globalH=1
//        globalW=1
//    }
//    constructor(dataOpcMatrixOld: TripleDataOpcMatrix, flag: Flag, globalW:Int, globalH: Int) {
//        this.vectorContainer = null
//        this.flag = flag
//        this.dataOld = dataOpcMatrixOld
//        state=State.Data
//        this.globalW=globalW
//        this.globalH=globalH
//    }
//    fun getVectorContainer():ByteVectorContainer{
//        if(state==State.Data&& !isReady){
//            vectorContainer= ByteVectorParser.instance.parseData(dataOld!!,flag,globalW,globalH)
//            isReady=true
//        }
//        return vectorContainer!!
//    }
//    fun getTripleDataOpc(): TripleDataOpcMatrix {
//        if(state==State.Vector&&!isReady){
//            dataOld = ByteVectorParser.instance.parseVector(vectorContainer!!,flag)
//            isReady=true
//        }
//        return dataOld!!
//    }
//
//
//
//}