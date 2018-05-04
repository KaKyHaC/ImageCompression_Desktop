package ImageCompressionLib.Convertor.Implementations

import ImageCompressionLib.Containers.*
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.Size
import ImageCompressionLib.Convertor.ConvertorDefault
import ImageCompressionLib.ProcessingModules.ModuleByteVector
import ImageCompressionLib.ProcessingModules.ModuleOPC.AbsModuleOPC
import ImageCompressionLib.ProcessingModules.ModuleOPC.StegoEncrWithOpcOld

abstract class AbsFactoryDefault :ConvertorDefault.IFactory {
    abstract fun getSameBaseSize(): Size
    abstract fun getMesasge():String?
    abstract fun getPassword():String?
    abstract fun onMessageListener(message:String?)

    override fun getModuleOPC(tripleShortMatrixOld: TripleShortMatrixOld, flag: Flag): AbsModuleOPC {
        val s=getSameBaseSize()
        val m=getMesasge()
        val p=getPassword()
        val r= StegoEncrWithOpcOld(tripleShortMatrixOld,flag,s.width,s.height,m,p,true)
        r.onMessageReadedListener=this::onMessageListener
        return r
    }

    override fun getModuleOPC(tripleDataOpcMatrixOld: TripleDataOpcMatrixOld, flag: Flag): AbsModuleOPC {
        val p=getPassword()
        val s=getSameBaseSize()
        val r= StegoEncrWithOpcOld(tripleDataOpcMatrixOld,flag,s.width,s.height,p,true)
        r.onMessageReadedListener=this::onMessageListener
        return r
    }

    override fun getModuleVectorParser(tripleDataOpcMatrixOld: TripleDataOpcMatrixOld, flag: Flag): ModuleByteVector {
        val s=getSameBaseSize()
        return ModuleByteVector(tripleDataOpcMatrixOld,flag,s.width,s.height)
    }

    override fun getModuleVectorParser(byteVectorContainer: ByteVectorContainer, flag: Flag): ModuleByteVector {
        return ModuleByteVector(byteVectorContainer,flag)
    }
}