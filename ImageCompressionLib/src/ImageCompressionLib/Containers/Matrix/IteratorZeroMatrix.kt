package ImageCompressionLib.Containers.Matrix

import ImageCompressionLib.Containers.Type.Size

class IteratorZeroMatrix<T:Any>(matrix: Array<Array<Any>>, wStart: Int, hStart: Int, sizeBuffer: Size,val defaultValue:T) : IteratorMatrix<T>(matrix, wStart, hStart, sizeBuffer) {
    override fun get(i: Int, j: Int): T {
        if(i>=sizeBuffer.width||j>=sizeBuffer.height)
            return defaultValue
        return super.get(i, j)
    }
}