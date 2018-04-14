package ImageCompressionLib.Convertor.Implementations

import ImageCompressionLib.Containers.*
import ImageCompressionLib.Convertor.ConvertorDefault
import ImageCompressionLib.ProcessingModules.ModuleByteVector
import ImageCompressionLib.ProcessingModules.ModuleOPC.AbsModuleOPC
import ImageCompressionLib.ProcessingModules.ModuleOPC.StegoEncrWithOPC

abstract class AbsFactoryDefault :ConvertorDefault.IFactory {
    abstract fun getSameBaseSize():Size
    abstract fun getMesasge():String?
    abstract fun getPassword():String?
    abstract fun onMessageListener(message:String?)

    override fun getModuleOPC(tripleShortMatrix: TripleShortMatrix, flag: Flag): AbsModuleOPC {
        val s=getSameBaseSize()
        val m=getMesasge()
        val p=getPassword()
        val r= StegoEncrWithOPC(tripleShortMatrix,flag,s.width,s.height,m,p,true)
        r.onMessageReadedListener=this::onMessageListener
        return r
    }

    override fun getModuleOPC(tripleDataOpcMatrix: TripleDataOpcMatrix, flag: Flag): AbsModuleOPC {
        val p=getPassword()
        val s=getSameBaseSize()
        val r= StegoEncrWithOPC(tripleDataOpcMatrix,flag,s.width,s.height,p,true)
        r.onMessageReadedListener=this::onMessageListener
        return r
    }

    override fun getModuleVectorParser(tripleDataOpcMatrix: TripleDataOpcMatrix, flag: Flag): ModuleByteVector {
        val s=getSameBaseSize()
        return ModuleByteVector(tripleDataOpcMatrix,flag,s.width,s.height)
    }

    override fun getModuleVectorParser(byteVectorContainer: ByteVectorContainer, flag: Flag): ModuleByteVector {
        return ModuleByteVector(byteVectorContainer,flag)
    }
}