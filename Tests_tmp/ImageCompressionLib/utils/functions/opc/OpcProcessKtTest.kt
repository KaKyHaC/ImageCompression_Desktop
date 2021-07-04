package ImageCompressionLib.utils.functions.opc

import ImageCompressionLib.containers.*
import ImageCompressionLib.containers.matrix.ShortMatrix
import ImageCompressionLib.containers.type.DataOpc
import ImageCompressionLib.containers.type.Flag
import ImageCompressionLib.containers.type.Size
import ImageCompressionLib.utils.functions.opc.OpcProcess.Companion.afterReverceOpcProcess
import ImageCompressionLib.utils.functions.opc.OpcProcess.Companion.directOPC
import ImageCompressionLib.utils.functions.opc.OpcProcess.Companion.preDirectOpcProcess
import ImageCompressionLib.utils.functions.opc.OpcProcess.Companion.reverseOPC
import org.junit.Test

import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*
import kotlin.test.assertEquals

@RunWith(value = Parameterized::class)
class OpcProcessKtTest(val size: Size, val flag: Flag) {
    lateinit var shortMatrix: ShortMatrix
    lateinit var dataOpc: DataOpc
    lateinit var parameters: Parameters
    lateinit var emptyMatrix: ShortMatrix
    val rand=Random()
    @Before
    fun setUp() {
        shortMatrix= ShortMatrix(size.width, size.height) { i, j -> ((i + 1) * (j + 1) % 255).toShort() }
        emptyMatrix= ShortMatrix(size.width, size.height)
        dataOpc= DataOpc(size)
        parameters= Parameters(flag,size,size)
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0},{1}")
        fun data(): Collection<Array<Any>> {
            val size = Size(8, 8)
            val flag = Flag()
            val flag1 = Flag()
            val flag2 = Flag()
            val flag3 = Flag()

            flag1.setTrue(Flag.Parameter.DCT)
            flag2.setTrue(Flag.Parameter.DC)
            flag3.setTrue(Flag.Parameter.LongCode)
            return listOf(
                    arrayOf(Size(7, 6), flag)
                    ,arrayOf(size, flag)
            ,arrayOf(size, flag1)
            ,arrayOf(size, flag2)
            ,arrayOf(size, flag3)
            ,arrayOf(Size(23, 11), flag)
            ,arrayOf(Size(40, 40), flag3)
            ,arrayOf(Size(1, 1), flag2)
            ,arrayOf(Size(23, 12), flag1))
        }
    }

    @Test
    fun OpcDefault() {
        val cpy=shortMatrix.copy()
        assertEquals(cpy,shortMatrix)

        preDirectOpcProcess(parameters,shortMatrix,dataOpc)
        directOPC(parameters,shortMatrix,dataOpc)

        reverseOPC(parameters,dataOpc,emptyMatrix)
        afterReverceOpcProcess(parameters,dataOpc,emptyMatrix)
        assertEquals(cpy,emptyMatrix)
    }
    @Test
    fun OpcDefaultRandomIntiWithDCTflag() {
        shortMatrix= ShortMatrix(size.width, size.height) { i, j -> (rand.nextInt() % 255).toShort() }
        parameters.flag.setTrue(Flag.Parameter.DCT)
        val cpy=shortMatrix.copy()
        assertEquals(cpy,shortMatrix)

        preDirectOpcProcess(parameters,shortMatrix,dataOpc)
        directOPC(parameters,shortMatrix,dataOpc)

        reverseOPC(parameters,dataOpc,emptyMatrix)
        afterReverceOpcProcess(parameters,dataOpc,emptyMatrix)
        assertEquals(cpy,emptyMatrix)
    }
}