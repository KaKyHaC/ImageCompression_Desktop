import ImageCompression.ProcessingModules.*
import ImageCompression.Containers.Flag
import ImageCompression.Utils.Objects.TimeManager
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class Convertor() {
    enum class Computing{OneThread,MultiThreads,MultiProcessor}
    var password:String?=null
    var globalBaseW=1
    var globalBaseH=1
    fun FromBmpToBar(pathToBmp: String, flag: Flag, computing: Computing=Computing.MultiThreads) {
        val isAsync=(computing==Computing.MultiThreads)
            val timeManager=TimeManager.Instance
            timeManager.startNewTrack("FromBmpToBar ${isAsync}")
            progressListener?.invoke(0,"read bmp")
        val bmp = ImageIO.read(File(pathToBmp))
            timeManager.append("read bmp")
            progressListener?.invoke(10,"RGB to YcBcR")
            view?.invoke(bmp)
        val mi = MyBufferedImage(bmp, flag)
        val matrix = mi.getYenlMatrix(isAsync)
            timeManager.append("rgb to yenl")
            progressListener?.invoke(30,"direct DCT")
        val bodum = ModuleDCT(matrix)
        val matrixDCT=bodum.getDCTMatrix(isAsync)
            timeManager.append("direct DCT")
            progressListener?.invoke(60,"direct OPC")
        val StEnOPC= StegoEncrWithOPC(matrixDCT)
        StEnOPC.password=password
        StEnOPC.baseSizeW=globalBaseW
        StEnOPC.baseSizeH=globalBaseH
        val box=StEnOPC.getBoxOfOpc(isAsync)
            timeManager.append("direct OPC")
            progressListener?.invoke(80,"write to file")
        val fileModule=ModuleFile(pathToBmp)
        fileModule.globalBaseW=globalBaseW
        fileModule.globalBaseH=globalBaseH
        fileModule.write(box,flag)
            timeManager.append("write to file")
            progressListener?.invoke(100,"Ready after ${timeManager.getTotalTime()} ms")
            System.out.println(timeManager.getInfoInSec())
    }

    fun FromBarToBmp(pathToBar: String,computing: Computing=Computing.MultiThreads): Unit {
        val isAsync=(computing==Computing.MultiThreads)
            val timeManager=TimeManager.Instance
            timeManager.startNewTrack("FromBarToBmp ${isAsync}")
            progressListener?.invoke(10,"read from file")
        val fileModule=ModuleFile(pathToBar)
        val pair=fileModule.read()
        val box=pair.first
        val flag=pair.second
            timeManager.append("read from file")
            progressListener?.invoke(10,"reverse OPC")
        val mOPC= StegoEncrWithOPC(box,flag)
        mOPC.password=password
        val FFTM =mOPC.getMatrix(isAsync)
            timeManager.append("reverse OPC")
            progressListener?.invoke(50,"reverse DCT")
        val bodum1 = ModuleDCT(FFTM)
        val matrixYBR=bodum1.getYCbCrMatrix(isAsync)
            timeManager.append("reverse DCT")
            progressListener?.invoke(70,"YcBcR to BMP");
        val af = MyBufferedImage(matrixYBR);
        val res = af.bufferedImage
            timeManager.append("yenl to bmp")
            progressListener?.invoke(90,"Write to BMP");
//            view?.invoke(res)
        var file=File(getPathWithoutType(pathToBar) + "res.bmp")
        file.createNewFile()
        ImageIO.write(res, "bmp", file)
            timeManager.append("write to bmp")
            progressListener?.invoke(100,"Ready after ${timeManager.getTotalTime()} ms");
            System.out.println(timeManager.getInfoInSec())
            view?.invoke(res)
    }

    private fun getPathWithoutType(path: String): String {
        return path.substring(0, path.length - 4)
    }

    var progressListener:((value:Int,text:String)->Unit)?=null
    var view:((image:BufferedImage)->Unit)?=null
}