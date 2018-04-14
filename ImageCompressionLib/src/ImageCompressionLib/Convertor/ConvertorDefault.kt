package ImageCompressionLib.Convertor

import ImageCompressionLib.Containers.*
import ImageCompressionLib.ProcessingModules.ModuleByteVector
import ImageCompressionLib.ProcessingModules.ModuleDCT
import ImageCompressionLib.ProcessingModules.ModuleOPC.AbsModuleOPC
import ImageCompressionLib.ProcessingModules.ModuleImage
//import java.awt.image.MyBufferedImage

class ConvertorDefault (val dao: IDao, val factory: IFactory) {
    interface IDao{
        fun onResultByteVectorContainer(vector: ByteVectorContainer, flag: Flag)
        fun onResultImage(image: MyBufferedImage, flag: Flag)
        fun getImage():Pair<MyBufferedImage,Flag>
        fun getByteVectorContainer():Pair<ByteVectorContainer,Flag>
//        fun getFlag():Flag
    }
    interface IFactory{
        fun getModuleOPC(tripleShortMatrix: TripleShortMatrix,flag: Flag):AbsModuleOPC
        fun getModuleOPC(tripleDataOpcMatrix: TripleDataOpcMatrix,flag: Flag):AbsModuleOPC
        fun getModuleVectorParser(tripleDataOpcMatrix: TripleDataOpcMatrix,flag: Flag):ModuleByteVector
        fun getModuleVectorParser(byteVectorContainer: ByteVectorContainer,flag: Flag):ModuleByteVector
    }

    enum class Computing{OneThread,MultiThreads,MultiProcessor}

    fun FromBmpToBar(computing: Computing = Computing.MultiThreads) {
        val isAsync=(computing== Computing.MultiThreads)
        val (bmp,flag)= dao.getImage()
        progressListener?.invoke(10,"RGB to YcBcR")
        onImageReadyListener?.invoke(bmp)
        val mi = ModuleImage(bmp, flag)
        val matrix = mi.getYenlMatrix(isAsync)
        progressListener?.invoke(30,"direct DCT")
        val bodum = ModuleDCT(matrix,flag)
        val matrixDCT=bodum.getDCTMatrix(isAsync)
        progressListener?.invoke(60,"direct OPC")
        val moduleOPC=factory.getModuleOPC(matrixDCT,flag)
        val box=moduleOPC.getTripleDataOpcMatrix()
        progressListener?.invoke(80,"write to file")
        val bvc=factory.getModuleVectorParser(box,flag).getVectorContainer()
        dao.onResultByteVectorContainer(bvc,flag)
        progressListener?.invoke(100,"Ready")
    }

    fun FromBarToBmp(computing: Computing = Computing.MultiThreads): Unit {
        val isAsync=(computing== Computing.MultiThreads)
        progressListener?.invoke(10,"read from file")
        val (bvc,flag)=dao.getByteVectorContainer()
        val box=factory.getModuleVectorParser(bvc,flag).getTripleDataOpc()
        progressListener?.invoke(10,"reverse OPC")
        val mOPC= factory.getModuleOPC(box,flag)
        val FFTM =mOPC.getTripleShortMatrix()
        progressListener?.invoke(50,"reverse DCT")
        val bodum1 = ModuleDCT(FFTM,flag)
        val matrixYBR=bodum1.getYCbCrMatrix(isAsync)
        progressListener?.invoke(70,"YcBcR to BMP");
        val af = ModuleImage(matrixYBR,flag);
        val res = af.bufferedImage
        progressListener?.invoke(90,"Write to BMP");
        dao.onResultImage(res,flag)
        progressListener?.invoke(100,"Ready after");
        onImageReadyListener?.invoke(res)
    }


    var progressListener:((value:Int,text:String)->Unit)?=null
    var onImageReadyListener:((image: MyBufferedImage)->Unit)?=null
}