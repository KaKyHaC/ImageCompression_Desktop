package ImageCompressionLib.Containers

import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.Flag
import java.io.InputStream
import java.io.OutputStream

data class ByteVectorContainer(val parameters: Parameters,val mainData: ByteVector, val suportData: ByteVector?){
    //TODO !OneFile

    override fun equals(other: Any?): Boolean {
        if(other==null)
            return false
        if(other.javaClass!=this.javaClass)
            return false

        val o = other as ByteVectorContainer
        return parameters.equals(o.parameters)&&
                mainData.equals(o.mainData)
    }

    fun writeToStream(os:OutputStream) {
        val vec=ByteVector();
        val p=parameters.toByteVector(vec)

        os.write(vec.size)
        os.write(vec.toByteArray())

        os.write(mainData.size)
        os.write(mainData.toByteArray())

        os.write(suportData?.size?:0)
        if(suportData!=null)
            os.write(suportData.toByteArray())
    }
    companion object {
        @JvmStatic fun readFromStream(ist:InputStream):ByteVectorContainer{
//            val tmp=ist.readAllBytes()
            var len=ist.read()
            var tmp=ByteArray(len)
            ist.read(tmp)
            val param=Parameters.fromByteVector(ByteVector(tmp.toTypedArray()))
            len=ist.read()
            tmp= ByteArray(len)
            ist.read(tmp)
            val main=ByteVector(tmp.toTypedArray())
            len=ist.read()
            tmp= ByteArray(len)
            ist.read(tmp)
            val sup= if(len>0)
                ByteVector(tmp.toTypedArray())
            else
                null

            return ByteVectorContainer(param,main,sup)
        }
    }
}