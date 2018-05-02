package ImageCompressionLib.Containers

import ImageCompressionLib.Constants.ICopyble
import java.util.*

interface IElement:ICopyble
class Matrix<T:IElement>  :IElement{
    val matrix:Array<Array<Any>>

    constructor(matrix: Array<Array<T>>) {
        this.matrix = Array(matrix.size){i->Array(matrix[0].size){j-> matrix[i][j].to(Any()).second}}
    }
    constructor(size: Size,init:(i:Int,j:Int)->Any){
        matrix= Array(size.width){ i -> Array(size.height){j-> init(i,j)} }
    }
    constructor(width:Int,height:Int,init:(i:Int,j:Int)->Any){
        matrix= Array(width){ i -> Array(height){j-> init(i,j)} }
    }

    val width:Int
        get() = matrix.size
    val height:Int
        get() = matrix[0].size


    operator fun get(i:Int,j:Int): T {
        return matrix[i][j] as T
    }
    operator fun set(i: Int,j: Int,value:T){
        matrix[i][j]=value
    }

    fun forEach(invoke:(i:Int,j:Int,value:T)->T?){
        for(i in 0 until width){
            for(j in 0 until height){
                matrix[i][j]=invoke.invoke(i,j,matrix[i][j] as T)?:matrix[i][j]
            }
        }
    }
    override fun copy():Matrix<T>{
        return Matrix(Size(width,height)){i, j -> (matrix[i][j] as T).copy() }
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


    fun rect(wStart:Int,hStart:Int,wEnd:Int,hEnd:Int):Matrix<T>{
        return Matrix(Size(wEnd-wStart,hEnd-hStart)){ i, j ->matrix[i+wStart][j+hStart] as T}
    }
    fun rectSave(wStart:Int,hStart:Int,wEnd:Int,hEnd:Int):Matrix<T>{
        var wtmp=wEnd
        var htmp=hEnd
        var wtmpS=wStart
        var htmpS=hStart
        if(wEnd>=width)
            wtmp=width-1
        if(hEnd>=height)
            htmp=height-1

        if(wStart<0)
            wtmpS=0
        if(hStart<0)
            htmpS=0

        val w=wtmp-wtmpS
        val h=htmp-htmpS
        return Matrix(w,h){ i, j ->matrix[i+wStart][j+hStart] as T}
    }
    fun split(horizontalStep:Int,verticalStep:Int):Matrix<Matrix<T>>{
        var w=width/horizontalStep
        var h=height/verticalStep
        if(width%horizontalStep!=0)w++
        if(height%verticalStep!=0)h++
        val res1=Matrix<Matrix<T>>(w,h){i, j ->  rectSave(i*horizontalStep,j*verticalStep,i*horizontalStep+horizontalStep,j*verticalStep+verticalStep)}
        return res1
    }

}