package ImageCompressionLib.Containers

import ImageCompressionLib.Constants.SIZEOFBLOCK
import ImageCompressionLib.Containers.Matrix.DataOpcMatrix
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Type.Flag
import java.util.*

class TripleDataOpcMatrix {
    val a: Matrix<DataOpc>
    val b: Matrix<DataOpc>
    val c: Matrix<DataOpc>
    val parameters:Parameters

    constructor(a: Matrix<DataOpc>, b: Matrix<DataOpc>, c: Matrix<DataOpc>,parameters: Parameters) {
        this.a = a
        this.b = b
        this.c = c
        this.parameters=parameters
    }

    fun toByteVectorContainer():ByteVectorContainer{
        val v1=ByteVector()
        parameters.toByteVector(v1)

    }
    fun Matrix<DataOpc>.toByteVectorContainer()
    //TODO add flag



    //TODO global base





    fun copy(): TripleDataOpcMatrix {
        val a=a.copy()
        val b=b.copy()
        val c=c.copy()
        return TripleDataOpcMatrix(a,b,c)
    }
    fun Matrix<DataOpc>.copy():Matrix<DataOpc>{
        return DataOpcMatrix.valueOf(this).copy()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TripleDataOpcMatrix
        if(!(this.a.equals(other.a)?:true))
            return false
        if(!(this.b.equals(other.b)?:true))
            return false
        if(!(this.c.equals(other.c)?:true))
            return false

        return true
    }
    override fun hashCode(): Int {
        var result = a?.let { a.hashCode() } ?: 0
        result = 31 * result + (b?.let { b.hashCode() } ?: 0)
        result = 31 * result + (c?.let { c.hashCode()} ?: 0)
        return result
    }

    companion object {
        @JvmStatic fun valueOf(container: ByteVectorContainer){

        }
    }
}