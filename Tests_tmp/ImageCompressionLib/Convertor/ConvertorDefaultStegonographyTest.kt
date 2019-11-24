package ImageCompressionLib.Convertor

import ImageCompressionLib.Containers.ByteVectorContainer
import ImageCompressionLib.Containers.EncryptParameters
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.MyBufferedImage
import ImageCompressionLib.Containers.Type.Size
import ImageCompressionLib.Utils.Functions.ImageStandardDeviation
import ImageCompressionLib.Utils.Functions.Opc.IStegoMessageUtil
import ImageCompressionLib.Utils.Objects.TimeManager
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*
import kotlin.test.assertFails

@RunWith(Parameterized::class)
class ConvertorDefaultStegonographyTest (val size: Size,val messageLenght:Int, var stegoPos:Int) {
    lateinit var parameters: Parameters
    lateinit var myBufferedImage: MyBufferedImage//= createMyBI(size)
    lateinit var encParam:EncryptParameters
    val range = 10

    val convertor:ConvertorDefault
    init {
        val dao= object : ConvertorDefault.IDao {
            var bvc: ByteVectorContainer?=null
            lateinit var cpy: MyBufferedImage
            override fun onResultByteVectorContainer(vector: ByteVectorContainer) {
                bvc=vector
            }
            override fun onResultImage(image: MyBufferedImage, parameters: Parameters) {
                val dev= ImageStandardDeviation.getDeviation(image,cpy)
                println(dev)
//                assert(dev<range)
            }
            override fun getImage(): Pair<MyBufferedImage, Parameters> {
                cpy=myBufferedImage.copy()
                return Pair(myBufferedImage,parameters)
            }
            override fun getByteVectorContainer(): ByteVectorContainer {
                return bvc!!
            }
        }
        val fac=object :ConvertorDefault.IGuard{
            lateinit var messCpy:ByteVector

            override fun getEncryptProperty(): EncryptParameters? {
                messCpy=encParam.message!!.copy()
                return encParam
            }
            override fun onMessageRead(vector: ByteVector) {
//                assertEquals(messCpy,vector)
                messCpy.assertEquals(vector)
                println(vector)
            }

        }
        convertor=ConvertorDefault(dao,fac)
    }

    @Before
    fun setUp() {
        parameters= Parameters.createParametresForTest(size,unitSize = Size(1,8))
//        parameters.flag.setTrue(Flag.Parameter.Steganography)
        myBufferedImage= createMyBI(size)
        encParam= EncryptParameters()
        encParam.message= createMessage(messageLenght)
    }

    @Test
    fun testTrueGeter(){
        encParam.steganography=EncryptParameters.StegaParameters(stegoPos){TrueGeter() }
        TimeManager.Instance.startNewTrack("convert")
        convertor.FromBmpToBar(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("direct")
        convertor.FromBarToBmp(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("reverce")
        println(TimeManager.Instance.getInfoInSec())
    }
    @Test
    fun testBinaryGeter(){
        encParam.steganography= EncryptParameters.StegaParameters(stegoPos){BinaryGeter()}
        TimeManager.Instance.startNewTrack("convert")
        convertor.FromBmpToBar(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("direct")

        convertor.FromBarToBmp(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("reverce")
        println(TimeManager.Instance.getInfoInSec())
    }
    @Test
    fun testFailed(){
        encParam.steganography= EncryptParameters.StegaParameters(stegoPos){BinaryGeter()}
        TimeManager.Instance.startNewTrack("convert")
        convertor.FromBmpToBar(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("direct")

        encParam.steganography=EncryptParameters.StegaParameters(stegoPos){TrueGeter()}
        assertFails {
            convertor.FromBarToBmp(ConvertorDefault.Computing.MultiThreads)
        }
        TimeManager.Instance.append("reverce")
        println(TimeManager.Instance.getInfoInSec())
    }


    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "stegoPos:{2},messLen:{1},{0}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(Size(16,16),10,0)
                    ,arrayOf(Size(41,12),10,1)
                    ,arrayOf(Size(128,128),50,2)
                    ,arrayOf(Size(119,133),20,3)
                    ,arrayOf(Size(1080,1920),200,4)
                    ,arrayOf(Size(1080,1920),200,0)
            )
        }

        var counter=0
        fun createMyBI(size: Size): MyBufferedImage {
            val res= MyBufferedImage(size.width,size.height)
            res.forEach(){i, j, value ->
                return@forEach (counter++)%0xffffff//Math.abs(Random().nextInt(0xffffff))
            }
            return res
        }

        fun createMessage(length:Int):ByteVector{
            val res=ByteVector()
            for( i in 0 until length)
                res.append(Random().nextBoolean())
            return res
        }
    }

    class TrueGeter:IStegoMessageUtil{
        override fun isUseNextBlock(): Boolean {
            return true
        }
    }
    class BinaryGeter:IStegoMessageUtil{
        var counter=0
        override fun isUseNextBlock(): Boolean {
            return (counter++)%2==0
        }

    }
}