package ImageCompressionLib.Utils.Functions.Dct

import ImageCompressionLib.Constants.Cosine
import ImageCompressionLib.Constants.SIZEOFBLOCK
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Type.Size

class DctUniversalAlgorithm (val unitSize:Size) {

    private val cosinTable:DctCosinUtils
    private val coeficient:DctCoeficientUtil
    init {
        cosinTable= DctCosinUtils(unitSize)
        coeficient= DctCoeficientUtil(unitSize)
    }


    fun directDCT(data: Matrix<Short>): Matrix<Short> {
        val dataRes=Matrix<Short>(data.size){i, j ->  0.toShort()}
        return directDct(data,dataRes)
    }

    fun reverceDCT(data: Matrix<Short>): Matrix<Short> {
        val dataRes=Matrix<Short>(data.size){i, j ->  0.toShort()}
        return reverceDct(data,dataRes)
    }

    private fun directDct(data: Matrix<Short>, target: Matrix<Short>): Matrix<Short> {
        val w = unitSize.width
        val h = unitSize.height
        for (p in 0 until w) {
            for (q in 0 until h) {
                var sum = 0.0
                for (m in 0 until w) {
                    for (n in 0 until h) {
                        sum += data[m,n] * cosinTable[p,q,m,n]
                    }
                }
                target[p,q]=(coeficient[p,q]*sum).toShort()//todo DoubleToShort()
            }
        }
        return target
    }

    private fun reverceDct(data: Matrix<Short>, target: Matrix<Short>): Matrix<Short> {
        val w = unitSize.width
        val h = unitSize.height
        for(m in 0 until w){
            for(n in 0 until h){
                var sum=0.0
                for(p in 0 until w){
                    for(q in 0 until h){
                        sum+=coeficient[p,q]*data[p,q]*cosinTable[p,q, m, n]
                    }
                }
                target[m,n]=sum.toShort()
            }
        }
        return target
    }
}
