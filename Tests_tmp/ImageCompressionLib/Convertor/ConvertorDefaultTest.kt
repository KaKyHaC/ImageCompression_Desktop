package ImageCompressionLib.Convertor

import ImageCompressionLib.Containers.ByteVectorContainer
import ImageCompressionLib.Containers.EncryptParameters
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.MyBufferedImage
import ImageCompressionLib.Convertor.ConvertorDefault.IDao
import org.junit.Before

import org.junit.Assert.*

class ConvertorDefaultTest {
//    var
    val convertor:ConvertorDefault
    init {
        val dao= object :IDao{
            override fun onResultByteVectorContainer(vector: ByteVectorContainer) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onResultImage(image: MyBufferedImage, parameters: Parameters) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun getImage(): Pair<MyBufferedImage, Parameters> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun getByteVectorContainer(): ByteVectorContainer {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
    }
}