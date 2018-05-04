package ImageCompressionLib.Containers.Matrix

import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Type.Size
import com.sun.org.glassfish.gmbal.Description

class DataOpcMatrix : Matrix<DataOpc> {

    constructor(matrix: Array<Array<DataOpc>>):super(matrix as Array<Array<Any>>) {    }
    constructor(w:Int,h:Int,untiSize: Size):super(w,h,{ i, j -> DataOpc(untiSize) }){}
    constructor(w:Int,h:Int,init:(i:Int,j:Int)-> DataOpc):super(w,h,init){}

    fun copy(): DataOpcMatrix {
        return DataOpcMatrix(width, height) { i, j -> (matrix[i][j] as DataOpc).copy() }
    }

    @Deprecated("Use DataOpcMatrix")
    @Description("create copy of array")
    fun toDataOpcArray():Array<Array<DataOpc>>{
        return  Array(width){i->Array(height){j->matrix[i][j] as DataOpc }}
    }
}