package ImageCompressionLib.Containers

import com.sun.java.browser.plugin2.DOM
import java.util.*

class DataOpcMatrix :Matrix<DataOpc>{

    constructor(matrix: Array<Array<DataOpc>>):super(matrix) {    }
    constructor(w:Int,h:Int,untiSize: Size):super(w,h,{i, j -> DataOpc(untiSize) }){}
    constructor(w:Int,h:Int,init:(i:Int,j:Int)->DataOpc):super(w,h,init){}

    fun copy():DataOpcMatrix{
        return DataOpcMatrix(width,height){i, j -> (matrix[i][j] as DataOpc).copy() }
    }

}