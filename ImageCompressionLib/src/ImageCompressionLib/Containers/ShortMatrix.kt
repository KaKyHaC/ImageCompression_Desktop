package ImageCompressionLib.Containers

import java.util.*

class ShortMatrix {
    private val matrix:Array<Array<Short>>
    val width:Int
        get() = matrix.size
    val height:Int
        get() = matrix[0].size

    constructor(matrix: Array<Array<Short>>) {
        this.matrix = matrix
    }
    constructor(w:Int,h:Int){
        matrix= Array(w){Array(h){(0).toShort()}}
    }
    constructor(w:Int,h:Int,init:(i:Int,j:Int)->Short){
        matrix= Array(w){i->Array(h){j->init.invoke(i,j)}}
    }



    operator fun get(i:Int,j:Int): Short {
        return matrix[i][j]
    }
    operator fun get(i:Int): Array<Short> {
        return matrix[i]
    }
    operator fun set(i: Int,j: Int,value:Short){
        matrix[i][j]=value
    }

    fun forEach(invoke:(i:Int,j:Int,value:Short)->Short?){
        for(i in 0 until width){
            for(j in 0 until height){
                matrix[i][j]=invoke.invoke(i,j,matrix[i][j])?:matrix[i][j]
            }
        }
    }

    fun copy():ShortMatrix{
        return ShortMatrix(width,height){i, j -> matrix[i][j] }
    }
    fun assertInRange(other:ShortMatrix,range:Int): Boolean {
        if(width!=other.width)
            throw Exception("width: $width!=${other.width}")
        if(height!=other.height)
            throw Exception("height: $height!=${other.height}")
        for(i in 0 until width){
            for(j in 0 until height){
                var inRange=false
                for(r in -range..range)
                    if(matrix[i][j]+r==other[i][j].toInt())
                        inRange=true
                if(!inRange)
                    throw Exception("data[$i][$j]: ${matrix[i][j]}!=${other[i][j]}")
            }
        }
        return true
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShortMatrix

        for(i in 0 until width)
            for(j in 0 until height)
                if(matrix[i][j]!=other[i,j])
                    return false

        return true
    }
    override fun hashCode(): Int {
        return Arrays.hashCode(matrix)
    }
    override fun toString(): String {
        val sb=StringBuilder()
        for(j in 0 until height){
            for(i in 0 until width){
                sb.append("${matrix[i][j]},")
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    fun toShortArray(): Array<ShortArray> {
        return Array(width){i->ShortArray(height){j->matrix[i][j]}}
    }
    companion object {
        @JvmStatic fun valueOf(mat:Array<ShortArray>): ShortMatrix {
            return ShortMatrix(mat.size,mat[0].size){i, j -> mat[i][j]}
        }
    }
}