package ImageCompressionLib.Containers

import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Type.DataOpc
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class TripleDataOpcMatrixTest {
    lateinit var tripleDataOpcMatrix: TripleDataOpcMatrix
    @Before
    fun setUp() {
        val p=Parameters.createParametresForTest()
        val m=Matrix<DataOpc>(p.unitSize){i, j ->  DataOpc(p)}
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
}