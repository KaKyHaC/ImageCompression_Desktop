import ImageCompression.Objects.*
import ImageCompression.Utils.Functions.CompressionUtils
import ImageCompression.Utils.Objects.Flag
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import java.util.*


class Convertor() {
    enum class Computing{OneThread,MultiThreads,MultiProcessor}
    var password:String?=null
    var globalBaseW=1
    var globalBaseH=1
    fun FromBmpToBar(pathToBmp: String, flag: Flag,computing: Computing=Computing.MultiThreads) {
        val isAsync=(computing==Computing.MultiThreads)

        val t1=Date().time
        progressListener?.invoke(0,"read bmp")
        val bmp = ImageIO.read(File(pathToBmp))
        progressListener?.invoke(10,"RGB to YcBcR")
        view?.invoke(bmp)
        val mi = MyBufferedImage(bmp, flag)
        val matrix = mi.yenlMatrix
        progressListener?.invoke(30,"direct DCT")
        val bodum = ModuleDCT(matrix)
        val matrixDCT=bodum.getDCTMatrix(isAsync)
        progressListener?.invoke(60,"direct OPC")
        val StEnOPC= StegoEncrWithOPC(matrixDCT)
        StEnOPC.password=password
        StEnOPC.baseSizeW=globalBaseW
        StEnOPC.baseSizeH=globalBaseH
        val box=StEnOPC.getBoxOfOpc(isAsync)
        progressListener?.invoke(80,"write to file")
        val fileModule=ModuleFile(pathToBmp)
        fileModule.globalBaseW=globalBaseW
        fileModule.globalBaseH=globalBaseH
        fileModule.write(box,flag)
        val t2=Date().time
        progressListener?.invoke(100,"Ready after ${t2-t1} ms")
    }

    fun FromBarToBmp(pathToBar: String,computing: Computing=Computing.MultiThreads): Unit {
        val isAsync=(computing==Computing.MultiThreads)

        val t1=Date().time
        progressListener?.invoke(10,"read from file")
        val fileModule=ModuleFile(pathToBar)
        val pair=fileModule.read()
        val box=pair.first
        val flag=pair.second
        progressListener?.invoke(10,"reverse OPC")
        val mOPC= StegoEncrWithOPC(box,flag)
        mOPC.password=password
        val FFTM =mOPC.getMatrix(isAsync)
        progressListener?.invoke(50,"reverse DCT")
        val bodum1 = ModuleDCT(FFTM)
        val matrixYBR=bodum1.getYCbCrMatrix(isAsync)
        progressListener?.invoke(70,"YcBcR to BMP");
        val af = MyBufferedImage(matrixYBR);
        val res = af.bufferedImage
        progressListener?.invoke(90,"Write to BMP");
        view?.invoke(res)

        var file=File(getPathWithoutType(pathToBar) + "res.bmp")
        file.createNewFile()
        ImageIO.write(res, "bmp", file)
        val t2=Date().time
        progressListener?.invoke(100,"Ready after ${t2-t1} ms");
    }

    private fun getPathWithoutType(path: String): String {
        return path.substring(0, path.length - 4)
    }

    var progressListener:((value:Int,text:String)->Unit)?=null
    var view:((image:BufferedImage)->Unit)?=null
}