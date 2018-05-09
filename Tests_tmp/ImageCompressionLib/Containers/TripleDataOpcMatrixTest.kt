package ImageCompressionLib.Containers

import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.Size
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.math.BigInteger

class TripleDataOpcMatrixTest {
    lateinit var tripleDataOpcMatrix: TripleDataOpcMatrix
    @Before
    fun setUp() {
        val p=Parameters.createParametresForTest(Size(256,374))
        p.unitSize= Size(7,6)
        val m=Matrix<DataOpc>(p.unitSize){i, j ->  DataOpc(p)}
        m.forEach(){i, j, value ->
            value.DC=(i+j).toShort()
            value.N= BigInteger.valueOf((i*j).toLong())
            for (r in 0 until value.base.size){
                value.base[r]=255
            }
            return@forEach value
        }
        tripleDataOpcMatrix= TripleDataOpcMatrix(m,m,m,p)
    }

    @Test
    fun testEquals() {
        assertEquals(tripleDataOpcMatrix,tripleDataOpcMatrix)
        assertEquals(tripleDataOpcMatrix,tripleDataOpcMatrix.copy())
    }

    @Test
    fun testByteVector() {
        val cpy=tripleDataOpcMatrix.copy()
        val tmp=tripleDataOpcMatrix.toByteVectorContainer()
        val res=TripleDataOpcMatrix.valueOf(tmp)
        assertEquals(cpy,res)
    }

    @Test
    fun testByteVectorCompressionFlag() {
        tripleDataOpcMatrix.parameters.flag= Flag.createCompressionFlag()
        tripleDataOpcMatrix.parameters.flag.setFalse(Flag.Parameter.LongCode)
        val cpy=tripleDataOpcMatrix.copy()
        val tmp=tripleDataOpcMatrix.toByteVectorContainer()
        val res=TripleDataOpcMatrix.valueOf(tmp)
        assertEquals(cpy,res)
    }
}