package ImageCompressionLib.processing_modules

import ImageCompressionLib.containers.ByteVectorContainer
import ImageCompressionLib.containers.type.ByteVector
import ImageCompressionLib.containers.type.Flag
import ImageCompressionLib.utils.functions.CompressionUtils

class ModuleCompression {
    fun compress(container: ByteVectorContainer,flag: Flag):ByteVectorContainer{
        if(!flag.isChecked(Flag.Parameter.CompressionUtils))
            return container

        val main= ByteVector(CompressionUtils.compress(container.mainData.toByteArray()))
        val sup=if(container.suportData!=null)
            ByteVector(CompressionUtils.compress(container.suportData.toByteArray()))
        else
            null
        return ByteVectorContainer(container.parameters,main,sup)
    }
    fun decompress(container: ByteVectorContainer,flag: Flag):ByteVectorContainer{
        if(!flag.isChecked(Flag.Parameter.CompressionUtils))
            return container

        val main= ByteVector(CompressionUtils.decompress(container.mainData.toByteArray()))
        val sup=if(container.suportData!=null)
            ByteVector(CompressionUtils.decompress(container.suportData.toByteArray()))
        else
            null
        return ByteVectorContainer(container.parameters,main,sup)
    }
}