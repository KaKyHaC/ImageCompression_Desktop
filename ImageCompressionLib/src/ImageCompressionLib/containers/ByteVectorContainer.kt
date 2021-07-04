package ImageCompressionLib.containers

import ImageCompressionLib.containers.type.ByteVector
import java.io.DataInputStream
import java.io.DataOutputStream
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
        val dos=DataOutputStream(os)
        val vec=ByteVector();
        parameters.toByteVector(vec)

        dos.writeInt(vec.size)
        dos.write(vec.toByteArray())

        dos.writeInt(mainData.size)
        dos.write(mainData.toByteArray())

        dos.writeInt(suportData?.size?:0)
        if(suportData!=null)
            os.write(suportData.toByteArray())
    }
    companion object {
        @JvmStatic fun readFromStream(ist:InputStream):ByteVectorContainer{
//            val tmp=ist.readAllBytes()
            val dis=DataInputStream(ist)
            var len=dis.readInt()
            var tmp=ByteArray(len)
            dis.read(tmp)
            val param=Parameters.fromByteVector(ByteVector(tmp))

            len=dis.readInt()
            tmp= ByteArray(len)
            dis.read(tmp)
            val main=ByteVector(tmp)

            len=dis.readInt()
            tmp= ByteArray(len)
            dis.read(tmp)
            val sup= if(len>0)
                ByteVector(tmp)
            else
                null

            return ByteVectorContainer(param,main,sup)
        }
    }
}