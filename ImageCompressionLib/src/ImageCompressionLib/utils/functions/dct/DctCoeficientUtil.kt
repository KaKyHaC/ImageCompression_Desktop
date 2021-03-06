package ImageCompressionLib.utils.functions.dct

import ImageCompressionLib.containers.matrix.Matrix
import ImageCompressionLib.containers.type.Size

class DctCoeficientUtil {
    private val  coeficients:Matrix<Double>
    constructor(unitSize: Size){
        val OneDivideMathsqrtW = 1.0 / Math.sqrt(unitSize.width.toDouble())
        val OneDivideMathsqrtH = 1.0 / Math.sqrt(unitSize.height.toDouble())
        val TwoDivideMathsqrtW = Math.sqrt(2.0 / unitSize.width.toDouble())
        val TwoDivideMathsqrtH = Math.sqrt(2.0 / unitSize.width.toDouble())

        coeficients= Matrix(2,2){ i, j ->
            val ap=if(i==0)OneDivideMathsqrtW else TwoDivideMathsqrtW
            val aq=if(j==0)OneDivideMathsqrtH else TwoDivideMathsqrtH
            ap*aq
        }
    }
    private constructor(coeficientU: Matrix<Double>){
        coeficients=coeficientU
    }

    operator fun get(p:Int,q:Int):Double{
        var tp=if(p>0) 1 else p
        var tq=if(q>0) 1 else q
        return coeficients[tp,tq]
    }
    fun copy(): DctCoeficientUtil {
        return DctCoeficientUtil(Matrix(coeficients.size){ i, j -> coeficients[i,j] })
    }
}