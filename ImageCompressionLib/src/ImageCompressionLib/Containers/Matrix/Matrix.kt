package ImageCompressionLib.Containers.Matrix

import ImageCompressionLib.Containers.Type.Size
import java.util.*

open class Matrix<T:Any> {
    internal val matrix: Array<Array<Any>>
    val size:Size

    //    constructor(matrix: Array<Array<T>>) {
//        this.matrix = Array(matrix.size){i->Array(matrix[0].size){j-> matrix[i][j] as Any}}
//    }
    constructor(size: Size, init: (i: Int, j: Int) -> Any) {
        matrix = Array(size.width) { i -> Array(size.height) { j -> init(i, j) } }
        this.size=size
    }

    constructor(width: Int, height: Int, init: (i: Int, j: Int) -> Any) {
        matrix = Array(width) { i -> Array(height) { j -> init(i, j) } }
        size= Size(width,height)
    }

    protected constructor(matrix: Array<Array<Any>>) {
        this.matrix = matrix
        size= Size(matrix.size,matrix[0].size)
    }

    open val width: Int
        get() = size.width
    open val height: Int
        get() = size.height


    open operator fun get(i: Int, j: Int): T {
        return matrix[i][j] as T
    }

    open operator fun set(i: Int, j: Int, value: T) {
//        if(i>=width||j>=height)//TODO remove
//            throw Exception("set[$i][$j] out of range $width x $height")
        matrix[i][j] = value
    }

    fun forEach(invoke: (i: Int, j: Int, value: T) -> T?) {
        for (i in 0 until width) {
            for (j in 0 until height) {
                matrix[i][j] = invoke.invoke(i, j, matrix[i][j] as T) ?: matrix[i][j]
            }
        }
    }
//    override fun copy():Matrix<T>{
//        return Matrix(Size(width,height)){i, j -> (matrix[i][j] as T).copy() }
//    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
//        if (javaClass != other?.javaClass) return false

        other as Matrix<T>

        for (i in 0 until width)
            for (j in 0 until height)
                if (matrix[i][j] != other[i, j])
                    return false

        return true
    }
    override fun hashCode(): Int {
        return Arrays.hashCode(matrix)
    }
    override fun toString(): String {
        val sb = StringBuilder()
        for (j in 0 until height) {
            for (i in 0 until width) {
                sb.append("${(get(i,j))},")
            }
            sb.append("\n")
        }
        return sb.toString()
    }


    @Deprecated("use Size")
    fun rectBuffer(wStart: Int, hStart: Int, wEnd: Int, hEnd: Int): Matrix<T> {
        return Matrix(Size(wEnd - wStart, hEnd - hStart)) { i, j -> matrix[i + wStart][j + hStart] as T }
    }
    fun rectBuffer(wStart: Int, hStart: Int, size: Size): Matrix<T> {
        return Matrix(Size(size.width,size.height)) { i, j -> matrix[i + wStart][j + hStart] as T }
    }
    fun rectIterator(wStart: Int, hStart: Int,size: Size): Matrix<T> {
        return IteratorMatrix<T>(matrix,wStart, hStart, size)
    }
    @Deprecated("use Size")
    fun rectBufferSave(wStart: Int, hStart: Int, wEnd: Int, hEnd: Int): Matrix<T> {
        val s=calculataBufferSize(wStart, hStart, Size(wEnd-wStart,hEnd-hStart))
        return Matrix(s.width,s.height) { i, j -> matrix[i + wStart][j + hStart] as T }
    }
    fun rectBufferSave(wStart: Int, hStart: Int, size: Size): Matrix<T> {
        val s=calculataBufferSize(wStart, hStart, size)
        return Matrix(s.width, s.height) { i, j -> matrix[i + wStart][j + hStart] as T }
    }
    fun rectSaveIterator(wStart: Int, hStart: Int,size: Size): Matrix<T> {
        val s=calculataBufferSize(wStart, hStart, size)
        return IteratorMatrix<T>(matrix,wStart,hStart, s)
    }
    fun rectSaveZeroIterator(wStart: Int, hStart: Int,size: Size,defaultVal:T): Matrix<T> {
//        val s=calculataBufferSize(wStart, hStart, size)
        return IteratorZeroMatrix<T>(matrix,wStart,hStart,size,defaultVal)
    }

    private fun calculataBufferSize(wStart: Int, hStart: Int, size: Size):Size{
        var wEnd = wStart+size.width
        var hEnd = hStart+size.height
        var wtmpS = wStart
        var htmpS = hStart
        if (wEnd > width)
            wEnd = width
        if (hEnd > height)
            hEnd = height

        if (wStart < 0)
            wtmpS = 0
        if (hStart < 0)
            htmpS = 0

        val w = wEnd - wtmpS
        val h = hEnd - htmpS
        return Size(w,h)
    }

    @Deprecated("use split")
    fun splitBuffered(horizontalStep: Int, verticalStep: Int): Matrix<Matrix<T>> {
        val s= calculateMatrixOfMatrixSize(horizontalStep, verticalStep)
        val res1 = Matrix<Matrix<T>>(s.width, s.height) { i, j -> rectBufferSave(i * horizontalStep, j * verticalStep, i * horizontalStep + horizontalStep, j * verticalStep + verticalStep) }
        return res1
    }
    fun split(horizontalStep: Int, verticalStep: Int): Matrix<Matrix<T>> {
        val s= calculateMatrixOfMatrixSize(horizontalStep, verticalStep)
        val res1 = Matrix<Matrix<T>>(s) { i, j -> rectSaveIterator(i * horizontalStep, j * verticalStep, Size(horizontalStep,verticalStep)) }
        return res1
    }
    fun splitWithZeroIterator(horizontalStep: Int, verticalStep: Int,defaultValue:T): Matrix<Matrix<T>> {
        val s= calculateMatrixOfMatrixSize(horizontalStep, verticalStep)
        val res1 = Matrix<Matrix<T>>(s) { i, j -> rectSaveZeroIterator(i * horizontalStep, j * verticalStep, Size(horizontalStep,verticalStep),defaultValue) }
        return res1
    }
    private fun calculateMatrixOfMatrixSize(horizontalStep: Int, verticalStep: Int):Size{
        var w = width / horizontalStep
        var h = height / verticalStep
        if (width % horizontalStep != 0) w++
        if (height % verticalStep != 0) h++
        return Size(w,h)
    }

    fun assertEquals(other:Matrix<T>):Boolean{
        if(size!=other.size)
            throw Exception("size: $size!=${other.size}")

        for(i in 0 until width){
            for(j in 0 until height){
                if(get(i,j)!=other[i,j])
                    throw Exception("data[$i][$j]: ${get(i,j)}!=${other[i,j]}")
            }
        }
        return true
    }

    companion object {
        @JvmStatic fun <T:Any>valueOf(mat:Array<Array<T>>): Matrix<T> {
            return Matrix<T>(mat as Array<Array<Any>>)
        }
        @JvmStatic fun <T:Any>glueTogether(mat: Matrix<Matrix<T>>): Matrix<T> {
            return Matrix<T>(mat as Array<Array<Any>>)
        }
    }
}