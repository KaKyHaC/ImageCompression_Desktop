package ImageCompression.ProcessingModules.ModuleOPC

import ImageCompression.Constants.SIZEOFBLOCK
import ImageCompression.Containers.Flag
import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Containers.TripleShortMatrix

abstract class AbsModuleOPC {
    protected enum class State {
        OPC, Data
    }

    protected var tripleDataOpc:TripleDataOpcMatrix
//        private set
    protected var tripleShort:TripleShortMatrix
//        private set
    protected val flag: Flag
    protected val state:State
    var isReady:Boolean=false
        private set

    constructor(tripleShort: TripleShortMatrix, flag: Flag) {
        this.flag = flag
        this.tripleShort = tripleShort
        this.tripleDataOpc= TripleDataOpcMatrix();
        this.state=State.Data
    }
    constructor(tripleDataOpc: TripleDataOpcMatrix, flag: Flag) {
        this.flag = flag
        this.tripleDataOpc=tripleDataOpc
        this.state=State.OPC

        val widthOPC = tripleDataOpc.a!!.size
        val heightOPC = tripleDataOpc.a!![0].size
        this.tripleShort = TripleShortMatrix(widthOPC * SIZEOFBLOCK, heightOPC * SIZEOFBLOCK, ImageCompression.Constants.State.DCT)

    }

    abstract protected fun direct(tripleShort: TripleShortMatrix):TripleDataOpcMatrix
    abstract protected fun reverce(tripleDataOpc: TripleDataOpcMatrix):TripleShortMatrix

    fun getTripleShortMatrix(): TripleShortMatrix {
        if(state==State.OPC&&!isReady) {
            tripleShort = reverce(tripleDataOpc)
            isReady=true
        }

        return tripleShort
    }
    fun getTripleDataOpcMatrix(): TripleDataOpcMatrix {
        if(state==State.Data&&!isReady) {
            tripleDataOpc = direct(tripleShort)
            isReady=true
        }
        return tripleDataOpc
    }
}