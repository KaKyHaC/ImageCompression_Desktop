package ImageCompressionLib.Containers

import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.Flag
import java.io.InputStream
import java.io.OutputStream

data class ByteVectorContainer(val parameters: Parameters,val mainData: ByteVector, val suportData: ByteVector?){
    //TODO !OneFile
    fun writeToStream(os:OutputStream) {
        val vec=ByteVector();
        val p=parameters.toByteVector(vec)

        os.write(vec.size)
        os.write(vec.toByteArray())

        os.write(mainData.size)
        os.write(mainData.toByteArray())

        os.write(suportData?.size?:0)
        os.write(suportData?.toByteArray())
    }
    companion object {
        @JvmStatic fun readFromStream(ist:InputStream):ByteVectorContainer{
            var len=ist.read()
            val param=Parameters.fromByteVector(ByteVector(ist.readBytes(len).toTypedArray()))
            len=ist.read()
            val main=ByteVector(ist.readBytes(len).toTypedArray())
            len=ist.read()
            val sup= if(len>0)
                ByteVector(ist.readBytes(len).toTypedArray())
            else
                null

            return ByteVectorContainer(param,main,sup)
        }
    }
}