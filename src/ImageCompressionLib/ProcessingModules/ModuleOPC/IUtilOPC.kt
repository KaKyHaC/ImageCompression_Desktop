package ImageCompressionLib.ProcessingModules.ModuleOPC

import ImageCompressionLib.Containers.TripleDataOpcMatrix
import ImageCompressionLib.Containers.TripleShortMatrix

interface IUtilOPC {
    fun getTripleShortMatrix(tripleDataOpcMatrix :TripleDataOpcMatrix): TripleShortMatrix
    fun getTripleDataOpcMatrix(tripleShortMatrix: TripleShortMatrix): TripleDataOpcMatrix
}