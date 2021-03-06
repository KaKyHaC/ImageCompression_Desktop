package ImageCompressionLib.containers

import ImageCompressionLib.containers.matrix.DataOpcMatrix
import ImageCompressionLib.containers.matrix.Matrix
import ImageCompressionLib.containers.type.ByteVector
import ImageCompressionLib.containers.type.DataOpc
import ImageCompressionLib.containers.type.Flag
import ImageCompressionLib.containers.type.Size

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
        val v2=if(parameters.flag.isChecked(Flag.Parameter.OneFile))v1 else ByteVector()
//        parameters.toByteVector(v1)
        a.size.toByteVector(v1)
        b.size.toByteVector(v1)
        c.size.toByteVector(v1)

        a.writeBaseToByteVector(v2)
        b.writeBaseToByteVector(v2)
        c.writeBaseToByteVector(v2)

        a.writeToByteVector(v1)
        b.writeToByteVector(v1)
        c.writeToByteVector(v1)

        val v2r=if(parameters.flag.isChecked(Flag.Parameter.OneFile))null else v2
        return ByteVectorContainer(parameters,v1,v2r)
    }
    fun Matrix<DataOpc>.writeToByteVector(vector: ByteVector){
        this.forEach(){i, j, value ->
            value.toByteVector(vector,parameters)
            return@forEach null
        }
    }
    fun Matrix<DataOpc>.writeBaseToByteVector(vector: ByteVector){
        if(parameters.flag.isChecked(Flag.Parameter.GlobalBase)) {
            val tmp = this.split(parameters.sameBaseSize.width, parameters.sameBaseSize.height)
            tmp.forEach(){i, j, value ->
                value[0,0].FromBaseToVector(vector,parameters.flag)
                return@forEach null
            }
        }else if(!parameters.flag.isChecked(Flag.Parameter.OneFile)){
            this.forEach() { i, j, value ->
                value.FromBaseToVector(vector, parameters.flag)
                return@forEach null
            }
        }
    }

    fun copy(): TripleDataOpcMatrix {
        val a=a.copy()
        val b=b.copy()
        val c=c.copy()
        return TripleDataOpcMatrix(a,b,c,parameters)
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
    fun assertEquals(other: TripleDataOpcMatrix): Boolean {
        a.assertEquals(other.a)
        b.assertEquals(other.b)
        c.assertEquals(other.c)
        return true
    }

    companion object {

        @JvmStatic fun valueOf(container: ByteVectorContainer): TripleDataOpcMatrix {
//            val parameters=Parameters.fromByteVector(container.mainData)
            val parameters=container.parameters
            val sizeA= Size.valueOf(container.mainData)
            val sizeB= Size.valueOf(container.mainData)
            val sizeC= Size.valueOf(container.mainData)

            val a=Matrix<DataOpc>(sizeA){i, j ->  DataOpc(parameters)}
            val b=Matrix<DataOpc>(sizeB){i, j ->  DataOpc(parameters)}
            val c=Matrix<DataOpc>(sizeC){i, j ->  DataOpc(parameters)}

            val v2=if(parameters.flag.isChecked(Flag.Parameter.OneFile))container.mainData else container.suportData
            a.readBaseFromByteVector(v2!!,parameters)
            b.readBaseFromByteVector(v2,parameters)
            c.readBaseFromByteVector(v2,parameters)

            a.readFromByteVector(container.mainData,parameters)
            b.readFromByteVector(container.mainData,parameters)
            c.readFromByteVector(container.mainData,parameters)

            return TripleDataOpcMatrix(a,b,c,parameters)
        }
        fun Matrix<DataOpc>.readFromByteVector(vector: ByteVector,parameters: Parameters){
            this.forEach() { i, j, value ->
                value.setFrom(vector, parameters)
                return@forEach null
            }
        }

        fun Matrix<DataOpc>.readBaseFromByteVector(vector: ByteVector,parameters: Parameters){
            if(parameters.flag.isChecked(Flag.Parameter.GlobalBase)){
                val tmp=this.split(parameters.sameBaseSize.width,parameters.sameBaseSize.height)
                tmp.forEach { i, j, value ->
                    value.forEach{ x, y, valueDopc ->
                        if(x==0&&y==0)
                            valueDopc.FromVectorToBase(vector,parameters.flag)
                        else
                            valueDopc.base=value[0,0].base
                        return@forEach null
                    }
                    return@forEach null
                }
                //todo test it ^
            } else if(!parameters.flag.isChecked(Flag.Parameter.OneFile)) {
                this.forEach() { i, j, value ->
                    value.FromVectorToBase(vector, parameters.flag)
                    return@forEach null
                }
            }
        }
    }
}