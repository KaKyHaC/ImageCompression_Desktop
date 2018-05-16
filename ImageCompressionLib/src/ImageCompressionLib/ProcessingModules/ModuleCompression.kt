package ImageCompressionLib.ProcessingModules

import ImageCompressionLib.Containers.ByteVectorContainer
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Utils.Functions.CompressionUtils

class ModuleCompression {
    fun compress(container: ByteVectorContainer,flag: Flag):ByteVectorContainer{
        if(!flag.isChecked(Flag.Parameter.CompressionUtils))
            return container
        
        val main= ByteVector(CompressionUtils.compress(container.mainData.toByteArray()).toTypedArray())
        val sup=if(container.suportData!=null)
            ByteVector(CompressionUtils.compress(container.suportData.toByteArray()).toTypedArray())
        else
            null
        return ByteVectorContainer(main,sup)
    }
    fun decompress(container: ByteVectorContainer,flag: Flag):ByteVectorContainer{
        if(!flag.isChecked(Flag.Parameter.CompressionUtils))
            return container

        val main= ByteVector(CompressionUtils.decompress(container.mainData.toByteArray()).toTypedArray())
        val sup=if(container.suportData!=null)
            ByteVector(CompressionUtils.decompress(container.suportData.toByteArray()).toTypedArray())
        else
            null
        return ByteVectorContainer(main,sup)
    }
}