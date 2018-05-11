package ImageCompressionLib.Utils.Functions.Dct

import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Type.Size

class DctCoeficientUtil (val unitSize:Size){
    private val  coeficients:Matrix<Double>
    init {
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

    operator fun get(p:Int,q:Int):Double{
        var tp=if(p>0) 1 else p
        var tq=if(q>0) 1 else q
        return coeficients[tp,tq]
    }
}