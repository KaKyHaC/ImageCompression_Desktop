package ImageCompressionLib.utils.functions.dct

import ImageCompressionLib.containers.matrix.Matrix
import ImageCompressionLib.containers.matrix.ShortMatrix
import ImageCompressionLib.containers.type.Size

class DctQuantizationUtils{
    val table:Matrix<Short>
    constructor(size:Size,maxVal:Int) {
        table= Matrix<Short>(size){ i, j ->
            var r=Math.exp((i+j)/2.0)
            if(r>maxVal)
                r=maxVal.toDouble()
            r.toShort()
        }
    }
    private constructor(table:Matrix<Short>){
        this.table=table
    }
    operator fun get(i:Int,j:Int): Short {
        return table[i,j]
    }
    fun copy():DctQuantizationUtils{
        return DctQuantizationUtils(ShortMatrix.valueOf(table).copy())
    }

}