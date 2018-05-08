package ImageCompressionLib.Convertor

import ImageCompressionLib.Containers.*
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.MyBufferedImage
//import ImageCompressionLib.ProcessingModules.ModuleByteVector
import ImageCompressionLib.ProcessingModules.ModuleDCT
import ImageCompressionLib.ProcessingModules.ModuleImage
import ImageCompressionLib.ProcessingModules.ModuleOpc

//import java.awt.image.MyBufferedImage

class ConvertorDefault (val dao: IDao,val guard:IGuard) {
    interface IDao{
        fun onResultByteVectorContainer(vector: ByteVectorContainer)
        fun onResultImage(image: MyBufferedImage, parameters: Parameters)
        fun getImage():Pair<MyBufferedImage, Parameters>
        fun getByteVectorContainer():ByteVectorContainer
//        fun getFlag():Flag
    }
    interface IGuard{
        fun getEncryptProperty():EncryptParameters?
        fun onMessageRead(vector: ByteVector)
    }
    //TODO compression utils


    enum class Computing{OneThread,MultiThreads,MultiProcessor}

    fun FromBmpToBar(computing: Computing = Computing.MultiThreads) {
        val isAsync=(computing== Computing.MultiThreads)
        val (bmp,parameters)= dao.getImage()
        progressListener?.invoke(10,"RGB to YcBcR")
        onImageReadyListener?.invoke(bmp)
        val mi = ModuleImage(bmp, parameters)
        val matrix = mi.getYenlMatrix(isAsync)
        progressListener?.invoke(30,"direct DCT")
        val bodum = ModuleDCT(matrix!!,parameters.flag)
        val matrixDCT=bodum.getDCTMatrix(isAsync)
        progressListener?.invoke(60,"direct OPC")
        val moduleOPC= ModuleOpc(matrixDCT!!, parameters)
        val box=moduleOPC.getTripleDataOpcMatrix(guard.getEncryptProperty())
        progressListener?.invoke(80,"write to file")
        val bvc=box.toByteVectorContainer()
        dao.onResultByteVectorContainer(bvc)
        progressListener?.invoke(100,"Ready")
    }

    fun FromBarToBmp(computing: Computing = Computing.MultiThreads): Unit {
        val isAsync=(computing== Computing.MultiThreads)
        progressListener?.invoke(10,"read from file")
        val bvc=dao.getByteVectorContainer()
        val box=TripleDataOpcMatrix.valueOf(bvc)
        val parameters=box.parameters
        progressListener?.invoke(10,"reverse OPC")
        val mOPC= ModuleOpc(box, parameters)
        val (FFTM,message) =mOPC.getTripleShortMatrix(guard.getEncryptProperty())
        message?.let { guard.onMessageRead(message)}
        progressListener?.invoke(50,"reverse DCT")
        val bodum1 = ModuleDCT(FFTM,parameters.flag)
        val matrixYBR=bodum1.getYCbCrMatrix(isAsync)
        progressListener?.invoke(70,"YcBcR to BMP");
        val af = ModuleImage(matrixYBR);
        val res = af.getBufferedImage()
        progressListener?.invoke(90,"Write to BMP");
        dao.onResultImage(res,parameters)
        progressListener?.invoke(100,"Ready after");
        onImageReadyListener?.invoke(res)
    }


    var progressListener:((value:Int,text:String)->Unit)?=null
    var onImageReadyListener:((image: MyBufferedImage)->Unit)?=null
}