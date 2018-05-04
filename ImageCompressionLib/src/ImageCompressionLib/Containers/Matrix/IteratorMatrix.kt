package ImageCompressionLib.Containers.Matrix

import ImageCompressionLib.Containers.Type.Size

class IteratorMatrix<T:Any>(matrix: Array<Array<Any>>,val wStart:Int,val hStart:Int,val sizeBuffer: Size) : Matrix<T>(matrix) {

    override fun get(i: Int, j: Int): T {
        return super.get(i+wStart, j+hStart)
    }

    override fun set(i: Int, j: Int, value: T) {
        super.set(i+wStart, j+hStart, value)
    }

    override val width: Int
        get() = sizeBuffer.width
    override val height: Int
        get() = sizeBuffer.height
}