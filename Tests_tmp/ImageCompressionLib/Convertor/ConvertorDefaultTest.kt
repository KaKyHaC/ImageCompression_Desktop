package ImageCompressionLib.Convertor

import ImageCompressionLib.Containers.ByteVectorContainer
import ImageCompressionLib.Containers.EncryptParameters
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.MyBufferedImage
import ImageCompressionLib.Containers.Type.Size
import ImageCompressionLib.Convertor.ConvertorDefault.IDao
import ImageCompressionLib.Utils.Functions.ImageStandardDeviation
import ImageCompressionLib.Utils.Objects.TimeManager
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*
import javax.net.ssl.TrustManager

@RunWith(Parameterized::class)
class ConvertorDefaultTest(val size:Size,val range:Int) {
    val parameters=Parameters.createParametresForTest(size)
    val myBufferedImage= createMyBI(size)

    val convertor:ConvertorDefault
    init {
        val dao= object :IDao{
            var bvc:ByteVectorContainer?=null
            lateinit var cpy:MyBufferedImage
            override fun onResultByteVectorContainer(vector: ByteVectorContainer) {
                bvc=vector
            }
            override fun onResultImage(image: MyBufferedImage, parameters: Parameters) {
                println(ImageStandardDeviation.getDeviation(image,cpy))
                image.assertInRange(cpy,range)
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
            override fun getEncryptProperty(): EncryptParameters? {
                return null
            }
            override fun onMessageRead(vector: ByteVector) {
                println(vector)
            }

        }
        convertor=ConvertorDefault(dao,fac)
    }



    @Test
    fun testDirectReverceConvert(){
        TimeManager.Instance.startNewTrack("convert")
        convertor.FromBmpToBar(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("direct")
        convertor.FromBarToBmp(ConvertorDefault.Computing.MultiThreads)
        TimeManager.Instance.append("reverce")
        println(TimeManager.Instance.getInfoInSec())
    }
//    @Test
//    fun testDirectReverceConvertUnitSize7x6(){
////        parameters.unitSize= Size(7,6)
//        TimeManager.Instance.startNewTrack("convert")
//        convertor.FromBmpToBar(ConvertorDefault.Computing.MultiThreads)
//        TimeManager.Instance.append("direct")
//        convertor.FromBarToBmp(ConvertorDefault.Computing.MultiThreads)
//        TimeManager.Instance.append("reverce")
//        println(TimeManager.Instance.getInfoInSec())
//    }
//    @Test
//    fun testDirectReverceConvertMaxCompression(){
////        parameters.flag= Flag.createCompressionFlag()
//        TimeManager.Instance.startNewTrack("convert")
//        convertor.FromBmpToBar(ConvertorDefault.Computing.MultiThreads)
//        TimeManager.Instance.append("direct")
//        convertor.FromBarToBmp(ConvertorDefault.Computing.MultiThreads)
//        TimeManager.Instance.append("reverce")
//        println(TimeManager.Instance.getInfoInSec())
//    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "range:{1},{0}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(Size(8,8),5)
                    ,arrayOf(Size(41,12),10)
                    ,arrayOf(Size(128,128),15)
                    ,arrayOf(Size(119,133),15)
                    ,arrayOf(Size(1080,1920),20)
            )
        }

        fun createMyBI(size: Size): MyBufferedImage {
            val res=MyBufferedImage(size.width,size.height)
            res.forEach(){i, j, value ->
                return@forEach Random().nextInt(0xffffff)
            }
            return res
        }
    }


}