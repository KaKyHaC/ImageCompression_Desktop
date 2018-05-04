package ImageCompressionLib.Containers.Matrix

import ImageCompressionLib.Containers.Type.Size

class IteratorMatrix<T:Any>(matrix: Array<Array<Any>>,val wStart:Int,val hStart:Int,val size: Size) : Matrix<T>(matrix) {
    override val width: Int
        get() = size.width
    override val height: Int
        get() = size.height

    override fun get(i: Int, j: Int): T {
        return super.get(i+wStart, j+hStart)
    }

    override fun set(i: Int, j: Int, value: T) {
        super.set(i+wStart, j+hStart, value)
    }
}