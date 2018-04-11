package ImageCompression.ProcessingModules

import ImageCompression.Containers.ByteVectorContainer
import ImageCompression.Containers.Flag
import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Utils.Functions.ByteVectorParser

class ModuleByteVector {
    private enum class State{Data,Vector}
    private val state:State
    private var data:TripleDataOpcMatrix?
    private var vectorContainer:ByteVectorContainer?
    private val flag: Flag
    private var isReady=false
    private val globalW:Int
    private val globalH:Int

    constructor(vectorContainer: ByteVectorContainer, flag: Flag) {
        this.vectorContainer = vectorContainer
        this.flag = flag
        this.data=null
        state=State.Vector
        globalH=1
        globalW=1
    }
    constructor(dataOpcMatrix: TripleDataOpcMatrix, flag: Flag,globalW:Int,globalH: Int) {
        this.vectorContainer = null
        this.flag = flag
        this.data=dataOpcMatrix
        state=State.Data
        this.globalW=globalW
        this.globalH=globalH
    }
    fun getVectorContainer():ByteVectorContainer{
        if(state==State.Data&& !isReady){
            vectorContainer= ByteVectorParser.instance.parseData(data!!,flag,globalW,globalH)
            isReady=true
        }
        return vectorContainer!!
    }
    fun getTripleDataOpc():TripleDataOpcMatrix{
        if(state==State.Vector&&!isReady){
            data= ByteVectorParser.instance.parseVector(vectorContainer!!,flag)
            isReady=true
        }
        return data!!
    }



}