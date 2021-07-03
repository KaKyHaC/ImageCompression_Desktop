package ImageCompressionLib.Utils.Functions.Dct

import ImageCompressionLib.Constants.MAX_QUANTIZATION_VALUE
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
    private constructor(cosinTable: DctCosinUtils, coeficient: DctCoeficientUtil, quantizationTable: DctQuantizationUtils, unitSize: Size) {
        this.cosinTable = cosinTable
        this.coeficient = coeficient
        this.quantizationTable = quantizationTable
        this.unitSize = unitSize
    }


    fun directDCT(data: Matrix<Short>): Matrix<Short> {
        val dataRes=Matrix<Short>(data.size){i, j ->  0.toShort()}
        return directDct(data,dataRes)
    }

    fun reverseDCT(data: Matrix<Short>): Matrix<Short> {
        val dataRes=Matrix<Short>(data.size){i, j ->  0.toShort()}
        return reverseDct(data,dataRes)
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
                target[p,q]=(coeficient[p,q]*sum).toShort()
            }
        }
        return target
    }

    private fun reverseDct(data: Matrix<Short>, target: Matrix<Short>): Matrix<Short> {
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
                target[m,n]=sum.toShort() //FromDoubleToShort(sum)//todo DoubleToShort()
            }
        }
        return target
    }
    fun directQuantization(data: Matrix<Short>):Matrix<Short>{
        data.forEach{ i, j, value ->
            (value/quantizationTable[i,j]).toShort()
        }
        return data
    }
    fun reverseQuantization(data: Matrix<Short>):Matrix<Short>{
        data.forEach{ i, j, value ->
            (value*quantizationTable[i,j]).toShort()
        }
        return data
    }
    fun copy():DctUniversalAlgorithm{
        return DctUniversalAlgorithm(cosinTable.copy(),coeficient.copy(),quantizationTable.copy(),unitSize)
    }
    companion object {
        @JvmStatic
        private fun FromDoubleToShort(d: Double): Short {
            if (d >= 255)
                return 255.toShort()
            var res = d.toShort()
            if (d % 1 > 0.5)
                res++
            return res
        }
    }
}
