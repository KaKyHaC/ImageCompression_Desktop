package ImageCompressionLib.Convertor

import ImageCompressionLib.Containers.*
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.MyBufferedImage
import ImageCompressionLib.ProcessingModules.ModuleCompression
//import ImageCompressionLib.ProcessingModules.ModuleByteVector
import ImageCompressionLib.ProcessingModules.ModuleDCT
import ImageCompressionLib.ProcessingModules.ModuleImage
import ImageCompressionLib.ProcessingModules.ModuleOpc
import ImageCompressionLib.Utils.Objects.TimeManager

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
        TimeManager.Instance.startNewTrack("direct")
        val isAsync=(computing== Computing.MultiThreads)
        val (bmp,parameters)= dao.getImage()
        progressListener?.invoke(10,"RGB to YcBcR")
        onImageReadyListener?.invoke(bmp)
        val mi = ModuleImage(bmp, parameters)
        val matrix = mi.getYenlMatrix(isAsync)
        progressListener?.invoke(30,"direct DCT")
        val bodum = ModuleDCT(matrix)
        val matrixDCT=bodum.getDCTMatrix(isAsync)
        progressListener?.invoke(60,"direct OPC")
        val moduleOPC= ModuleOpc(matrixDCT)
        val box=moduleOPC.getTripleDataOpcMatrix(guard.getEncryptProperty())
        progressListener?.invoke(80,"Compress")
        val bvc=box.toByteVectorContainer();
        val bvcComp=ModuleCompression().compress(bvc,parameters.flag);
        progressListener?.invoke(80,"write to file")
        dao.onResultByteVectorContainer(bvcComp)
        TimeManager.Instance.append("Finish")
        progressListener?.invoke(100,"Ready after ${TimeManager.Instance.getInfoInSec()}")
    }

    fun FromBarToBmp(computing: Computing = Computing.MultiThreads): Unit {
        TimeManager.Instance.startNewTrack("reverce")
        val isAsync=(computing== Computing.MultiThreads)
        progressListener?.invoke(10,"read from file")
        val bvcComp=dao.getByteVectorContainer()
        val bvc=ModuleCompression().decompress(bvcComp,bvcComp.parameters.flag);
        progressListener?.invoke(15,"decompress")
        val box=TripleDataOpcMatrix.valueOf(bvc)
        val parameters=box.parameters
        progressListener?.invoke(20,"reverse OPC")
        val mOPC= ModuleOpc(box)
        val (FFTM,message) =mOPC.getTripleShortMatrix(guard.getEncryptProperty())
        message?.let { guard.onMessageRead(message)}
        progressListener?.invoke(50,"reverse DCT")
        val bodum1 = ModuleDCT(FFTM)
        val matrixYBR=bodum1.getYCbCrMatrix(isAsync)
        progressListener?.invoke(70,"YcBcR to BMP");
        val af = ModuleImage(matrixYBR);
        val res = af.getBufferedImage()
        progressListener?.invoke(90,"Write to BMP");
        dao.onResultImage(res,parameters)
        TimeManager.Instance.append("Finish")
        progressListener?.invoke(100,"Ready after ${TimeManager.Instance.getInfoInSec()}");
        onImageReadyListener?.invoke(res)
    }


    var progressListener:((value:Int,text:String)->Unit)?=null
    var onImageReadyListener:((image: MyBufferedImage)->Unit)?=null
}