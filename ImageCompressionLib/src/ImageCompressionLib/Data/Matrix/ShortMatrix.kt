package ImageCompressionLib.Data.Matrix

import ImageCompressionLib.Data.Type.Size

class ShortMatrix: Matrix<Short> {
//    constructor(matrix: Array<Array<Short>>):super(matrix as Array<Array<Any>>) { }
    private constructor(matrix: Array<Array<Any>>):super(matrix) { }
    constructor(w:Int,h:Int):super(w,h,{i, j -> (0).toShort() }){    }
    constructor(w:Int,h:Int,init:(i:Int,j:Int)->Short):super(w,h,init){}
    constructor(size: Size, init:(i:Int, j:Int)->Short):super(size,init){}


    fun copy(): ShortMatrix {
        return ShortMatrix(width, height) { i, j -> matrix[i][j] as Short }
    }
    fun assertInRange(other: ShortMatrix, range:Int): Boolean {
        if(width!=other.width)
            throw Exception("width: $width!=${other.width}")
        if(height!=other.height)
            throw Exception("height: $height!=${other.height}")
        for(i in 0 until width){
            for(j in 0 until height){
                var inRange=false
                for(r in -range..range)
                    if(get(i,j)+r==other[i,j].toInt())
                        inRange=true
                if(!inRange)
                    throw Exception("data[$i][$j]: ${matrix[i][j]}!=${other[i,j]}")
            }
        }
        return true
    }
    fun toShortArray(): Array<ShortArray> {
        return Array(width){i->ShortArray(height){j->matrix[i][j] as Short}}
    }
    fun toArrayShort(): Array<Array<Short>> {
        return Array(width){i->Array(height){j->matrix[i][j] as Short}}
    }
    fun toMatrix(): Matrix<Short> {
        return this
    }

    companion object {
        @Deprecated("use Array<Array<Short>>")
        @JvmStatic fun valueOf(mat:Array<ShortArray>): ShortMatrix {
            return ShortMatrix(mat.size, mat[0].size) { i, j -> mat[i][j] }
        }
        @JvmStatic fun valueOf(mat:Array<Array<Short>>): ShortMatrix {
            return ShortMatrix(mat as Array<Array<Any>>)
        }
        @JvmStatic fun valueOf(mat: Matrix<Short>): ShortMatrix {
            return ShortMatrix(mat.matrix)
        }
    }
}