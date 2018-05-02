package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Containers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.text.FieldPosition

@RunWith(Parameterized::class)
class OpcProcessWithMessageTest(val size: Size,val position: Int,val message:Boolean) {
    lateinit var shortMatrix: ShortMatrix
    lateinit var dataOpc: DataOpc
    lateinit var parameters: Parameters
    lateinit var emptyMatrix: ShortMatrix
    @Before
    fun setUp() {
        shortMatrix = ShortMatrix(size.width, size.height) { i, j -> ((i + 1) * (j + 1) % 255).toShort() }
        emptyMatrix = ShortMatrix(size.width, size.height)
        dataOpc = DataOpc(size)
        val flag = Flag()
        parameters = Parameters(flag, size, size)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0},pos={1},mess={2}")
        fun data(): Collection<Array<Any>> {
            val size=Size(8,8)
            return listOf(
                    arrayOf(size,0,true)
                    ,arrayOf(size,0,false)
                    ,arrayOf(size,1,true)
                    ,arrayOf(size,1,false)
                    ,arrayOf(size,2,true)
                    ,arrayOf(size,2,false)
            )
        }
    }

    @Test
    fun OpcDefault() {
        val cpy = shortMatrix.copy()
        kotlin.test.assertEquals(cpy, shortMatrix)

        preDirectOpcProcess(parameters, shortMatrix, dataOpc)
        directOpcWithMessageAt(parameters, shortMatrix, dataOpc, message, position)

        val res = reverseOpcWithMessageAt(parameters, dataOpc, emptyMatrix, position)
        afterReverceOpcProcess(parameters, dataOpc, emptyMatrix)
        assertEquals(message, res)
        kotlin.test.assertEquals(cpy, emptyMatrix)
    }
}