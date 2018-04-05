package ImageCompression.Convertor

import ImageCompression.Containers.Flag
import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Containers.TripleShortMatrix
import ImageCompression.ProcessingModules.ModuleDCT
import ImageCompression.ProcessingModules.ModuleOPC.AbsModuleOPC
import ImageCompression.ProcessingModules.MyBufferedImage
import ImageCompression.Utils.Objects.TimeManager
import java.awt.image.BufferedImage

class ConvertorDefault (val dao: IDao, val factory: IFactory) {
    interface IDao{
        fun onResultTripleData(vector: TripleDataOpcMatrix,flag: Flag)
        fun onResultImage(image: BufferedImage,flag: Flag)
        fun getImage():Pair<BufferedImage,Flag>
        fun getTripleDataOpc():Pair<TripleDataOpcMatrix,Flag>
//        fun getFlag():Flag
    }
    interface IFactory{
        fun getModuleOPC(tripleShortMatrix: TripleShortMatrix,flag: Flag):AbsModuleOPC
        fun getModuleOPC(tripleDataOpcMatrix: TripleDataOpcMatrix,flag: Flag):AbsModuleOPC
    }

    enum class Computing{OneThread,MultiThreads,MultiProcessor}

    fun FromBmpToBar(computing: Computing = Computing.MultiThreads) {
        val isAsync=(computing== Computing.MultiThreads)
        val timeManager= TimeManager.Instance
        val (bmp,flag)= dao.getImage()
        timeManager.append("read bmp")
        progressListener?.invoke(10,"RGB to YcBcR")
        onImageReadyListener?.invoke(bmp)
        val mi = MyBufferedImage(bmp, flag)
        val matrix = mi.getYenlMatrix(isAsync)
        timeManager.append("rgb to yenl")
        progressListener?.invoke(30,"direct DCT")
        val bodum = ModuleDCT(matrix,flag)
        val matrixDCT=bodum.getDCTMatrix(isAsync)
        timeManager.append("direct DCT")
        progressListener?.invoke(60,"direct OPC")
        val moduleOPC=factory.getModuleOPC(matrixDCT,flag)
        val box=moduleOPC.getTripleDataOpcMatrix()
        timeManager.append("direct OPC")
        progressListener?.invoke(80,"write to file")
        timeManager.append("write to file")
        dao.onResultTripleData(box,flag)
        progressListener?.invoke(100,"Ready after ${timeManager.getTotalTime()} ms")
        System.out.println(timeManager.getInfoInSec())
    }

    fun FromBarToBmp(computing: Computing = Computing.MultiThreads): Unit {
        val isAsync=(computing== Computing.MultiThreads)
        val timeManager= TimeManager.Instance
        timeManager.startNewTrack("FromBarToBmp ${isAsync}")
        progressListener?.invoke(10,"read from file")
        val (box,flag)=dao.getTripleDataOpc()
        timeManager.append("read from file")
        progressListener?.invoke(10,"reverse OPC")
        val mOPC= factory.getModuleOPC(box,flag)
        val FFTM =mOPC.getTripleShortMatrix()
        timeManager.append("reverse OPC")
        progressListener?.invoke(50,"reverse DCT")
        val bodum1 = ModuleDCT(FFTM,flag)
        val matrixYBR=bodum1.getYCbCrMatrix(isAsync)
        timeManager.append("reverse DCT")
        progressListener?.invoke(70,"YcBcR to BMP");
        val af = MyBufferedImage(matrixYBR,flag);
        val res = af.bufferedImage
        timeManager.append("yenl to bmp")
        progressListener?.invoke(90,"Write to BMP");
        dao.onResultImage(res,flag)
        timeManager.append("write to bmp")
        progressListener?.invoke(100,"Ready after ${timeManager.getTotalTime()} ms");
        System.out.println(timeManager.getInfoInSec())
        onImageReadyListener?.invoke(res)
    }


    var progressListener:((value:Int,text:String)->Unit)?=null
    var onImageReadyListener:((image: BufferedImage)->Unit)?=null
}