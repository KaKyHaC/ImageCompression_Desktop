package ImageCompression.ProcessingModules

import ImageCompression.Containers.ByteVector
import ImageCompression.Containers.ByteVectorContainer
import ImageCompression.Containers.Flag
import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Utils.Functions.ByteVectorParcer

class ModuleByteVector {
    private enum class State{Data,Vector}
    private val state:State
    private var data:TripleDataOpcMatrix?
    private var vectorContainer:ByteVectorContainer?
    private val flag: Flag
    private var isReady=false

    constructor(vectorContainer: ByteVectorContainer, flag: Flag) {
        this.vectorContainer = vectorContainer
        this.flag = flag
        this.data=null
        state=State.Vector
    }
    constructor(dataOpcMatrix: TripleDataOpcMatrix, flag: Flag) {
        this.vectorContainer = null
        this.flag = flag
        this.data=dataOpcMatrix
        state=State.Data
    }
    fun getVectorContainer():ByteVectorContainer{
        if(state==State.Data&& !isReady){
            vectorContainer=ByteVectorParcer.instance.parceData(data!!,flag)
            isReady=true
        }
        return vectorContainer!!
    }
    fun getTripleDataOpc():TripleDataOpcMatrix{
        if(state==State.Vector&&!isReady){
            data=ByteVectorParcer.instance.parceVector(vectorContainer!!,flag)
            isReady=true
        }
        return data!!
    }



}