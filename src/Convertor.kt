import ImageCompression.Objects.*
import ImageCompression.Utils.Objects.Flag
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import java.util.*


class Convertor() {
    fun FromBmpToBar(pathToBmp: String, flag: Flag) {
        progressListener?.invoke(0,"read bmp")
        val bmp = ImageIO.read(File(pathToBmp))
        progressListener?.invoke(10,"RGB to YcBcR")
        view?.invoke(bmp)
        val mi = MyBufferedImage(bmp, flag)
        val matrix = mi.yenlMatrix
        progressListener?.invoke(30,"direct DCT")
        val bodum = ModuleDCT(matrix)
        val matrixDCT=bodum.getDCTMatrix(true)
        progressListener?.invoke(60,"direct OPC")
        val StEnOPC= StegoEncrWithOPC(matrixDCT)
        val box=StEnOPC.getBoxOfOpc(true)
        progressListener?.invoke(80,"write to file")
        val fileModule=ModuleFile(pathToBmp)
        fileModule.write(box,flag)
        progressListener?.invoke(100,"Ready")
    }

    fun FromBarToBmp(pathToBar: String): Unit {
        progressListener?.invoke(10,"read from file")
        val fileModule=ModuleFile(pathToBar)
        val pair=fileModule.read()
        val box=pair.first
        val flag=pair.second
        progressListener?.invoke(10,"reverse OPC")
        val mOPC= StegoEncrWithOPC(box,flag)
        val FFTM =mOPC.getMatrix(true)
        progressListener?.invoke(50,"reverse DCT")
        val bodum1 = ModuleDCT(FFTM)
        val matrixYBR=bodum1.getYCbCrMatrix(true)
        progressListener?.invoke(70,"YcBcR to BMP");
        val af = MyBufferedImage(matrixYBR);
        val res = af.bufferedImage
        progressListener?.invoke(90,"Write to BMP");
        view?.invoke(res)

        var file=File(getPathWithoutType(pathToBar) + "res.bmp")
        file.createNewFile()
        ImageIO.write(res, "bmp", file)
        progressListener?.invoke(100,"Ready");
    }

    private fun getPathWithoutType(path: String): String {
        return path.substring(0, path.length - 4)
    }

    var progressListener:((value:Int,text:String)->Unit)?=null
    var view:((image:BufferedImage)->Unit)?=null
}