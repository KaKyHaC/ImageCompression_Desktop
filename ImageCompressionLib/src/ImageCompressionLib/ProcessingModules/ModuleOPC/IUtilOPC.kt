package ImageCompressionLib.ProcessingModules.ModuleOPC

import ImageCompressionLib.Containers.TripleDataOpcMatrixOld
import ImageCompressionLib.Containers.TripleShortMatrixOld

interface IUtilOPC {
    fun getTripleShortMatrix(tripleDataOpcMatrixOld: TripleDataOpcMatrixOld): TripleShortMatrixOld
    fun getTripleDataOpcMatrix(tripleShortMatrixOld: TripleShortMatrixOld): TripleDataOpcMatrixOld
}