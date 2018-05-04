package ImageCompressionLib.ProcessingModules.ModuleOPC

import ImageCompressionLib.Constants.SIZEOFBLOCK
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.TripleDataOpcMatrixOld
import ImageCompressionLib.Containers.TripleShortMatrixOld

abstract class AbsModuleOPC {
    protected enum class State {
        OPC, Data
    }

    protected var tripleDataOpcOld: TripleDataOpcMatrixOld
//        private set
    protected var tripleShortOld: TripleShortMatrixOld
//        private set
    protected val flag: Flag
    protected val state:State
    var isReady:Boolean=false
        private set

    constructor(tripleShortOld: TripleShortMatrixOld, flag: Flag) {
        this.flag = flag
        this.tripleShortOld = tripleShortOld
        this.tripleDataOpcOld = TripleDataOpcMatrixOld(); //TODO remove this line
        this.state=State.Data
    }
    constructor(tripleDataOpcOld: TripleDataOpcMatrixOld, flag: Flag) {
        this.flag = flag
        this.tripleDataOpcOld = tripleDataOpcOld
        this.state=State.OPC

        val widthOPC = tripleDataOpcOld.a!!.size
        val heightOPC = tripleDataOpcOld.a!![0].size
        this.tripleShortOld = TripleShortMatrixOld(widthOPC * SIZEOFBLOCK, heightOPC * SIZEOFBLOCK, ImageCompressionLib.Constants.State.DCT)

    }

    abstract protected fun direct(tripleShortOld: TripleShortMatrixOld): TripleDataOpcMatrixOld
    abstract protected fun reverce(tripleDataOpcOld: TripleDataOpcMatrixOld): TripleShortMatrixOld

    fun getTripleShortMatrix(): TripleShortMatrixOld {
        if(state==State.OPC&&!isReady) {
            tripleShortOld = reverce(tripleDataOpcOld)
            isReady=true
        }

        return tripleShortOld
    }
    fun getTripleDataOpcMatrix(): TripleDataOpcMatrixOld {
        if(state==State.Data&&!isReady) {
            tripleDataOpcOld = direct(tripleShortOld)
            isReady=true
        }
        return tripleDataOpcOld
    }
}