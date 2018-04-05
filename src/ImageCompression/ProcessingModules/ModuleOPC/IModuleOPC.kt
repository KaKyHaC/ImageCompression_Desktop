package ImageCompression.ProcessingModules.ModuleOPC

import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Containers.TripleShortMatrix

interface IModuleOPC {
    fun getTripleShortMatrix(): TripleShortMatrix
    fun getTripleDataOpcMatrix(): TripleDataOpcMatrix
}