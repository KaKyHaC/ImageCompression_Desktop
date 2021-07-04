package ImageCompressionLib.utils.objects

import ImageCompressionLib.containers.EncryptParameters
import ImageCompressionLib.containers.matrix.DataOpcMatrix
import ImageCompressionLib.containers.matrix.ShortMatrix
import ImageCompressionLib.containers.Parameters
import ImageCompressionLib.containers.type.ByteVector
import ImageCompressionLib.containers.type.Flag
import ImageCompressionLib.containers.type.Size
import ImageCompressionLib.utils.functions.opc.IStegoMessageUtil
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(Parameterized::class)
class OpcConvertorStegonographyTest(val size: Size,val lenght:Int,val pos:Int) {
    lateinit var convertor:OpcConvertor
    lateinit var param:Parameters
    lateinit var cpy:ShortMatrix
    lateinit var message:ByteVector
    lateinit var enPar:EncryptParameters
    @Before
    fun setUp() {
        val matrix=ShortMatrix(size){i, j -> Math.abs(Random().nextInt(255)).toShort() }
        cpy=matrix.copy()
        val flag=Flag.createDefaultFlag()
//        flag.setTrue(Flag.Parameter.Steganography)
        param=Parameters.createParametresForTest(size,flag = flag,unitSize = Size(1,8))
        param.flag.setFalse(Flag.Parameter.DC)
        convertor=OpcConvertor(matrix,param)
        message=createMessage(lenght)
        enPar=EncryptParameters()

    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "pos:{2},len:{1},{0}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(Size(8,8),8,0),
                    arrayOf(Size(8,32),16,0),
                    arrayOf(Size(16,8),1,1),
                    arrayOf(Size(128,128),11,0),
                    arrayOf(Size(128,128),11,1)
            )
        }
    }

    @Test
    fun testMessage(){
        val mesCpy=message.copy()
        enPar.message=message
        enPar.steganography=EncryptParameters.StegaParameters(pos) {
            object : IStegoMessageUtil {
                override fun isUseNextBlock(): Boolean {
                    return true
                }
            }
        }
        val opcs=convertor.getDataOpcs(enPar,message)
        val con1=OpcConvertor(DataOpcMatrix.valueOf(opcs),param)
        val res=con1.getDataOrigin(enPar)
//        cpy.assertInRange(ShortMatrix.valueOf(res.first),2)
        res.second!!.assertEquals(mesCpy)
    }
    @Test
    fun testStegoHidenCode(){
        println("not work")
        enPar.message= ByteVector(lenght)
        enPar.steganography=EncryptParameters.StegaParameters(0) {
            object : IStegoMessageUtil {
                override fun isUseNextBlock(): Boolean {
                    return true
                }
            }
        }
        val cpy=ShortMatrix.valueOf(convertor.getDataOrigin(null).first).copy()
        val opcs=convertor.getDataOpcs(enPar,enPar.message)
        val con1=OpcConvertor(DataOpcMatrix.valueOf(opcs),param)
        val (res,mess)=con1.getDataOrigin(null)
        println(cpy)
        println(res)
        assertEquals(cpy,res)

    }

    fun createMessage(len:Int): ByteVector {
        val res=ByteVector()
        res.append(true)
        for(i in 0 until len-1)
            res.append(Random().nextBoolean())
        return res
    }
}