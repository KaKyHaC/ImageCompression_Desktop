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
                sb.append("${(matrix[i][j] as T)},")
            }
            sb.append("\n")
        }
        return sb.toString()
    }


    @Deprecated("use Size")
    fun rect(wStart: Int, hStart: Int, wEnd: Int, hEnd: Int): Matrix<T> {
        return Matrix(Size(wEnd - wStart, hEnd - hStart)) { i, j -> matrix[i + wStart][j + hStart] as T }
    }
    fun rect(wStart: Int, hStart: Int,size: Size): Matrix<T> {
        return Matrix(Size(size.width,size.height)) { i, j -> matrix[i + wStart][j + hStart] as T }
    }
    fun rectIterator(wStart: Int, hStart: Int,size: Size): Matrix<T> {
        return IteratorMatrix<T>(matrix,wStart, hStart, size)
    }
    @Deprecated("use Size")
    fun rectSave(wStart: Int, hStart: Int, wEnd: Int, hEnd: Int): Matrix<T> {
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
        return Matrix(w, h) { i, j -> matrix[i + wStart][j + hStart] as T }
    }
    fun rectSave(wStart: Int, hStart: Int,size: Size): Matrix<T> {
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
        return Matrix(w, h) { i, j -> matrix[i + wStart][j + hStart] as T }
    }
    fun rectSaveIterator(wStart: Int, hStart: Int,size: Size): Matrix<T> {
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
        return IteratorMatrix<T>(matrix,wStart,hStart, Size(w,h))
    }

    @Deprecated("use splitBuffer")
    fun split(horizontalStep: Int, verticalStep: Int): Matrix<Matrix<T>> {
        var w = width / horizontalStep
        var h = height / verticalStep
        if (width % horizontalStep != 0) w++
        if (height % verticalStep != 0) h++
        val res1 = Matrix<Matrix<T>>(w, h) { i, j -> rectSave(i * horizontalStep, j * verticalStep, i * horizontalStep + horizontalStep, j * verticalStep + verticalStep) }
        return res1
    }
    fun splitBuffer(horizontalStep: Int, verticalStep: Int): Matrix<Matrix<T>> {
        var w = width / horizontalStep
        var h = height / verticalStep
        if (width % horizontalStep != 0) w++
        if (height % verticalStep != 0) h++
        val res1 = Matrix<Matrix<T>>(w, h) { i, j -> rectSaveIterator(i * horizontalStep, j * verticalStep, Size(horizontalStep,verticalStep)) }
        return res1
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