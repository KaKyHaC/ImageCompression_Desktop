package ImageCompressionLib.containers.matrix

import ImageCompressionLib.containers.type.DataOpc
import ImageCompressionLib.containers.type.Size
//import com.sun.org.glassfish.gmbal.Description

class DataOpcMatrix : Matrix<DataOpc> {

//    constructor(matrix: Array<Array<DataOpc>>):super(matrix as Array<Array<Any>>) {    }
    private constructor(matrix: Array<Array<Any>>):super(matrix) { }
    constructor(w:Int,h:Int,untiSize: Size):super(w,h,{ i, j -> DataOpc(untiSize) }){}
    constructor(w:Int,h:Int,init:(i:Int,j:Int)-> DataOpc):super(w,h,init){}

    fun copy(): DataOpcMatrix {
        return DataOpcMatrix(width, height) { i, j -> (matrix[i][j] as DataOpc).copy() }
    }

    @Deprecated("Use DataOpcMatrix")
//    @Description("create copy of array")
    fun toDataOpcArray():Array<Array<DataOpc>>{
        return  Array(width){i->Array(height){j->matrix[i][j] as DataOpc }}
    }
    companion object {
        @JvmStatic fun valueOf(mat: Matrix<DataOpc>): DataOpcMatrix {
            return DataOpcMatrix(mat.matrix)
        }
        @JvmStatic fun valueOf(mat: Array<Array<DataOpc>>): DataOpcMatrix {
            return DataOpcMatrix(mat as Array<Array<Any>>)
        }
    }
}