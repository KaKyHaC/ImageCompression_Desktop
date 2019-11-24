package ImageCompressionLib.Utils.Dct

import ImageCompressionLib.Data.Matrix.Matrix
import ImageCompressionLib.Data.Primitives.Size

class DctCoefficients(val unitSize: Size) {

    private val  coefficients:Matrix<Double>

    init {
        val OneDivideMathsqrtW = 1.0 / Math.sqrt(unitSize.width.toDouble())
        val OneDivideMathsqrtH = 1.0 / Math.sqrt(unitSize.height.toDouble())
        val TwoDivideMathsqrtW = Math.sqrt(2.0 / unitSize.width.toDouble())
        val TwoDivideMathsqrtH = Math.sqrt(2.0 / unitSize.width.toDouble())

        coefficients= Matrix(2,2){ i, j ->
            val ap=if(i==0)OneDivideMathsqrtW else TwoDivideMathsqrtW
            val aq=if(j==0)OneDivideMathsqrtH else TwoDivideMathsqrtH
            ap*aq
        }
    }

    operator fun get(p:Int,q:Int):Double{
        val tp=if(p>0) 1 else p
        val tq=if(q>0) 1 else q
        return coefficients[tp,tq]
    }
}