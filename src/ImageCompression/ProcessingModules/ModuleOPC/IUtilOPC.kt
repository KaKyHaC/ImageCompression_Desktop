package ImageCompression.ProcessingModules.ModuleOPC

import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Containers.TripleShortMatrix

interface IUtilOPC {
    fun getTripleShortMatrix(tripleDataOpcMatrix :TripleDataOpcMatrix): TripleShortMatrix
    fun getTripleDataOpcMatrix(tripleShortMatrix: TripleShortMatrix): TripleDataOpcMatrix
}