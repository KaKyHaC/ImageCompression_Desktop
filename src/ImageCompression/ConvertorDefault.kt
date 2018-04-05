package ImageCompression

import ImageCompression.Containers.ByteVector
import ImageCompression.Containers.Flag
import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Containers.TripleShortMatrix
import ImageCompression.ProcessingModules.ModuleDCT
import ImageCompression.ProcessingModules.ModuleFile
import ImageCompression.ProcessingModules.ModuleOPC.AbsModuleOPC
import ImageCompression.ProcessingModules.ModuleOPC.StegoEncrWithOPC
import ImageCompression.ProcessingModules.MyBufferedImage
import ImageCompression.Utils.Objects.TimeManager
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ConvertorDefault (val dao:IDao,val moduleOPC: AbsModuleOPC) {
    interface IDao{
        fun onResultTripleData(vector: TripleDataOpcMatrix)
        fun onResultImage(image: BufferedImage,flag: Flag)
    }

    enum class Computing{OneThread,MultiThreads,MultiProcessor}
    data class Info(val flag: Flag, val password: String?=null
                    , val message: String?=null, val sameBaseWidth:Int=1, val sameBaseHeight:Int=1)

    fun FromBmpToBar(image: BufferedImage,info: Info, computing: Computing = Computing.MultiThreads) {
        val isAsync=(computing== Computing.MultiThreads)
        val timeManager= TimeManager.Instance
        val bmp = image
        timeManager.append("read bmp")
        progressListener?.invoke(10,"RGB to YcBcR")
        view?.invoke(bmp)
        val mi = MyBufferedImage(bmp, info.flag)
        val matrix = mi.getYenlMatrix(isAsync)
        timeManager.append("rgb to yenl")
        progressListener?.invoke(30,"direct DCT")
        val bodum = ModuleDCT(matrix,info.flag)
        val matrixDCT=bodum.getDCTMatrix(isAsync)
        timeManager.append("direct DCT")
        progressListener?.invoke(60,"direct OPC")
        val box=moduleOPC.getTripleDataOpcMatrix()
        timeManager.append("direct OPC")
        progressListener?.invoke(80,"write to file")
        timeManager.append("write to file")
        dao.onResultTripleData(box)
        progressListener?.invoke(100,"Ready after ${timeManager.getTotalTime()} ms")
        System.out.println(timeManager.getInfoInSec())
    }

    fun FromBarToBmp(box: TripleDataOpcMatrix,flag: Flag,password: String?=null,computing: Computing = Computing.MultiThreads): Unit {
        val isAsync=(computing== Computing.MultiThreads)
        val timeManager= TimeManager.Instance
        timeManager.startNewTrack("FromBarToBmp ${isAsync}")
        progressListener?.invoke(10,"read from file")
        timeManager.append("read from file")
        progressListener?.invoke(10,"reverse OPC")
        val mOPC= StegoEncrWithOPC(box, flag)
        mOPC.password=password
        val FFTM =mOPC.getMatrix(isAsync)
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
        view?.invoke(res)
    }

    private fun getPathWithoutType(path: String): String {
        return path.substring(0, path.length - 4)
    }

    var progressListener:((value:Int,text:String)->Unit)?=null
    var view:((image: BufferedImage)->Unit)?=null

    class DaoFileModule(val path: String):IDao{
        override fun onResultImage(image: BufferedImage, flag: Flag) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onResultTripleData(vector: TripleDataOpcMatrix) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}