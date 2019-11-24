package ImageCompressionLib.Utils.Functions.Dct

import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Type.Size

class DctCosinUtils{
//    private val cosinTableW: Matrix<Double>
//    private val cosinTableH: Matrix<Double>
    private val cosinTableMultiply:Matrix<Matrix<Double>>

    constructor(unitSize: Size) {
        val cosinTableW=Matrix<Double>(unitSize.width,unitSize.width){m, p ->
            Math.cos((2.0 * m + 1.0) * p.toDouble() * Math.PI / (2 * unitSize.width))
        }
        val cosinTableH=Matrix<Double>(unitSize.height,unitSize.height){n, q ->
            Math.cos((2.0 * n + 1.0) * q.toDouble() * Math.PI / (2 * unitSize.height))
        }
        cosinTableMultiply= Matrix(unitSize){ p, q ->
            Matrix<Double>(unitSize){m, n ->
                cosinTableW[m,p]*cosinTableH[n,q]
            }
        }
    }
    private constructor(crossinline :Matrix<Matrix<Double>>){
        cosinTableMultiply=crossinline
    }


    operator fun get(p:Int,q:Int,m:Int,n:Int): Double {
        return cosinTableMultiply[p,q][m,n]
    }

    fun copy():DctCosinUtils{
        return DctCosinUtils(Matrix(cosinTableMultiply.size){ p, q ->
            Matrix<Short>(cosinTableMultiply.size){m, n ->
                cosinTableMultiply[p,q][m,n]
            }
        })
    }
}