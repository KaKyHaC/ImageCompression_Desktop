package ImageCompression.ProcessingModules.ModuleOPC

import ImageCompression.Containers.Flag
import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Containers.TripleShortMatrix

abstract class AbsModuleOPC {
    protected var tripleDataOpc:TripleDataOpcMatrix?
    protected var tripleShort:TripleShortMatrix?
    val flag: Flag

    constructor(tripleShort: TripleShortMatrix?, flag: Flag) {
        this.tripleShort = tripleShort
        this.flag = flag
        this.tripleDataOpc=null
    }
    constructor(tripleDataOpc: TripleDataOpcMatrix?, flag: Flag) {
        this.tripleShort = null
        this.flag = flag
        this.tripleDataOpc=tripleDataOpc
    }

    abstract fun direct(tripleShort: TripleShortMatrix):TripleDataOpcMatrix
    abstract fun reverce(tripleDataOpc: TripleDataOpcMatrix):TripleShortMatrix

    fun getTripleShortMatrix(): TripleShortMatrix {
        if(tripleShort==null)
            tripleShort=reverce(tripleDataOpc!!)
        return tripleShort!!
    }

    fun getTripleDataOpcMatrix(): TripleDataOpcMatrix {
        if(tripleDataOpc==null)
            tripleDataOpc=direct(tripleShort!!)
        return tripleDataOpc!!
    }
}