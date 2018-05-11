package ImageCompressionLib.Utils.Functions.Dct

import ImageCompressionLib.Constants.Cosine
import ImageCompressionLib.Constants.MAX_QUANTIZATION_VALUE
import ImageCompressionLib.Constants.SIZEOFBLOCK
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Type.Size

class DctUniversalAlgorithm  {

    private val cosinTable:DctCosinUtils
    private val coeficient:DctCoeficientUtil
    private val quantizationTable:DctQuantizationUtils
    private val unitSize:Size
    constructor(unitSize: Size) {
        this.unitSize=unitSize
        cosinTable= DctCosinUtils(unitSize)
        coeficient= DctCoeficientUtil(unitSize)
        quantizationTable= DctQuantizationUtils(unitSize, MAX_QUANTIZATION_VALUE)
    }
        constructor(cosinTable: DctCosinUtils, coeficient: DctCoeficientUtil, quantizationTable: DctQuantizationUtils, unitSize: Size) {
        this.cosinTable = cosinTable
        this.coeficient = coeficient
        this.quantizationTable = quantizationTable
        this.unitSize = unitSize
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
    fun copy():DctUniversalAlgorithm{
        return DctUniversalAlgorithm(cosinTable.copy(),coeficient.copy(),quantizationTable.copy(),unitSize)
    }
}
