package ImageCompression.Convertor.Implementations

import ImageCompression.Containers.*
import ImageCompression.Convertor.ConvertorDefault
import ImageCompression.ProcessingModules.ModuleByteVector
import ImageCompression.ProcessingModules.ModuleOPC.AbsModuleOPC
import ImageCompression.ProcessingModules.ModuleOPC.StegoEncrWithOPC

abstract class AbsFactoryDefault :ConvertorDefault.IFactory {
    abstract fun getSameBaseSize():Size
    abstract fun getMesasge():String?
    abstract fun getPassword():String?
    override fun getModuleOPC(tripleShortMatrix: TripleShortMatrix, flag: Flag): AbsModuleOPC {
        val s=getSameBaseSize()
        val m=getMesasge()
        val p=getPassword()
        return StegoEncrWithOPC(tripleShortMatrix,flag,s.width,s.height,m,p,true)
    }

    override fun getModuleOPC(tripleDataOpcMatrix: TripleDataOpcMatrix, flag: Flag): AbsModuleOPC {
        val p=getPassword()
        val s=getSameBaseSize()
        return StegoEncrWithOPC(tripleDataOpcMatrix,flag,s.width,s.height,p,true)
    }

    override fun getModuleVectorParser(tripleDataOpcMatrix: TripleDataOpcMatrix, flag: Flag): ModuleByteVector {
        val s=getSameBaseSize()
        return ModuleByteVector(tripleDataOpcMatrix,flag,s.width,s.height)
    }

    override fun getModuleVectorParser(byteVectorContainer: ByteVectorContainer, flag: Flag): ModuleByteVector {
        return ModuleByteVector(byteVectorContainer,flag)
    }
}