package ImageCompressionLib.Convertor

import ImageCompressionLib.Data.ByteVectorContainer
import ImageCompressionLib.Data.EncryptParameters
import ImageCompressionLib.Data.Parameters
import ImageCompressionLib.Data.Type.ByteVector
import ImageCompressionLib.Data.Type.Flag
import ImageCompressionLib.Data.Type.MyBufferedImage
import ImageCompressionLib.Data.Type.Size
import ImageCompressionLib.Convertor.ConvertorDefault.IDao
import ImageCompressionLib.Utils.Functions.ImageStandardDeviation
import ImageCompressionLib.Utils.Objects.TimeManager
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class ConvertorDefaultTest(val size:Size,var range:Int) {
    var parameters=Parameters.createParametresForTest(size)
    lateinit var myBufferedImage:MyBufferedImage//= createMyBI(size)
    val file= File("convertorfile.txt")

    val convertor:ConvertorDefault
    init {
        file.createNewFile()
        val dao= object :IDao{
            var bvc:ByteVectorContainer?=null
            lateinit var cpy:MyBufferedImage
            override fun onResultByteVectorContainer(vector: ByteVectorContainer) {
//                bvc=vector
                vector.writeToStream(file.outputStream())
            }
            override fun onResultImage(image: MyBufferedImage, parameters: Parameters) {
                val dev=ImageStandardDeviation.getDeviation(image,cpy)
                println(dev)
//                image.assertInRange(cpy,range)
                assert(dev<range)
            }
            override fun getImage(): Pair<MyBufferedImage, Parameters> {
                cpy=myBufferedImage.copy()
                return Pair(myBufferedImage,parameters)
            }
            override fun getByteVectorContainer(): ByteVectorContainer {
//                return bvc!!
                return ByteVectorContainer.readFromStream(file.inputStream())
            }
        }
        val fac=object :ConvertorDefault.IGuard{
            override fun getEncryptProperty(): EncryptParameters? {
                return null
            }
            override fun onMessageRead(vector: ByteVector) {
                println(vector)
            }

        }
        convertor=ConvertorDefault(dao,fac)
    }

    @Before
    fun setUp() {
        parameters=Parameters.createParametresForTest(size)
        myBufferedImage= createMyBI(size)
    }

    @Test
    fun DirectReverceConvert(){
        TimeManager.Instance.startNewTrack("convert")
        convertor.FromBmpToBar(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("direct")
        convertor.FromBarToBmp(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("reverce")
        println(TimeManager.Instance.getInfoInSec())
    }
    @Test
    fun DirectReverceConvertUnitSize7x6(){
        parameters=Parameters(parameters.flag,parameters.imageSize, Size(7,6))
        TimeManager.Instance.startNewTrack("convert")
        convertor.FromBmpToBar(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("direct")
        convertor.FromBarToBmp(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("reverce")
        println(TimeManager.Instance.getInfoInSec())
    }
    @Test
    fun DirectReverceConvertMaxCompression(){
        parameters=Parameters(Flag.createCompressionFlag(),parameters.imageSize)
        range*=2
        TimeManager.Instance.startNewTrack("convert")
        convertor.FromBmpToBar(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("direct")
        convertor.FromBarToBmp(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("reverce")
        println(TimeManager.Instance.getInfoInSec())
    }
    @Test
    fun DirectReverceConvertGlobalBase(){
        parameters=Parameters(Flag.createDefaultFlag(),parameters.imageSize,sameBaseSize = Size(2,2))
        parameters.flag.setTrue(Flag.Parameter.GlobalBase)
        TimeManager.Instance.startNewTrack("convert")
        convertor.FromBmpToBar(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("direct")
        convertor.FromBarToBmp(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("reverce")
        println(TimeManager.Instance.getInfoInSec())
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "range:{1},{0}")
        fun data(): Collection<Array<Any>> {
            return listOf(
//                    arrayOf(Size(8,8),5)
                    arrayOf(Size(16,16),10)
                    ,arrayOf(Size(41,12),10)
                    ,arrayOf(Size(128,128),15)
                    ,arrayOf(Size(119,133),15)
                    ,arrayOf(Size(1080,1920),20)
            )
        }

        var counter=0
        fun createMyBI(size: Size): MyBufferedImage {
            val res=MyBufferedImage(size.width,size.height)
            res.forEach(){i, j, value ->
                return@forEach (counter++)%0xffffff//Math.abs(Random().nextInt(0xffffff))
            }
            return res
        }
    }


}