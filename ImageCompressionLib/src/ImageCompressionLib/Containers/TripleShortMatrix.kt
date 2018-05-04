package ImageCompressionLib.Containers

import ImageCompressionLib.Constants.State
import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import java.util.*

class TripleShortMatrix {
    var state:State
    var a: Matrix<Short>
    var b: Matrix<Short>
    var c: Matrix<Short>
    val parameters: Parameters
    constructor(parameters: Parameters,state:State){
        this.parameters=parameters
        this.state=state
        a= Matrix(parameters.imageSize){i, j ->  0.toShort()}
        b= Matrix(parameters.imageSize){i, j ->  0.toShort()}
        c= Matrix(parameters.imageSize){i, j ->  0.toShort()}
    }

    constructor(a: Matrix<Short>, b: Matrix<Short>, c: Matrix<Short>, parameters: Parameters,state: State) {
        this.a = a
        this.b = b
        this.c = c
        this.parameters=parameters
        this.state=state
    }

    val width:Int
        get() = a.width
    val height:Int
        get() = a.height

    fun assertMatrixInRange(tripleShortMatrix: TripleShortMatrix, range: Int):Boolean{
        ShortMatrix.valueOf(a).assertInRange(ShortMatrix.valueOf(tripleShortMatrix.a),range)
        ShortMatrix.valueOf(b).assertInRange(ShortMatrix.valueOf(tripleShortMatrix.b),range)
        ShortMatrix.valueOf(c).assertInRange(ShortMatrix.valueOf(tripleShortMatrix.c),range)

        return true
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TripleShortMatrix

        if(!a.equals(other.a))return false
        if(!b.equals(other.b))return false
        if(!c.equals(other.c))return false

        return true
    }
    override fun hashCode(): Int {
        var result = parameters.hashCode()
        result = 31 * result + a.hashCode()
        result = 31 * result + b.hashCode()
        result = 31 * result + c.hashCode()
        return result
    }

    fun copy(): TripleShortMatrix {
        val a1=ShortMatrix.valueOf(a).copy().toMatrix()
        val b1=ShortMatrix.valueOf(b).copy().toMatrix()
        val c1=ShortMatrix.valueOf(c).copy().toMatrix()
        return TripleShortMatrix(a1,b1,c1,parameters,state)
    }
}