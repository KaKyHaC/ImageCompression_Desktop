import ImageCompression.Objects.*
import ImageCompression.Utils.Objects.Flag
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import java.util.*


class Convertor() {

    fun FromBmpToBar(pathToBmp: String, flag: Flag) {
        publishProgress(0)
        val bmp = ImageIO.read(File(pathToBmp))
        publishProgress(10)
        view?.invoke(bmp)
        val mi = MyBufferedImage(bmp, flag)
        val matrix = mi.yenlMatrix
        publishProgress(30)
        val bodum = ModuleDCT(matrix)
        val matrixDCT=bodum.getDCTMatrix(true)
        publishProgress(60)
        val StEnOPC= StegoEncrWithOPC(matrixDCT)
        val box=StEnOPC.getBoxOfOpc(true)
        publishProgress(80)
        val fileModule=ModuleFile(pathToBmp)
        fileModule.write(box,flag)
        publishProgress(100)
    }

    fun FromBarToBmp(pathToBar: String): Unit {
        val fileModule=ModuleFile(pathToBar)
        val pair=fileModule.read()
        val box=pair.first
        val flag=pair.second
        publishProgress(10)
        val mOPC= StegoEncrWithOPC(box,flag)
        val FFTM =mOPC.getMatrix(true)
        publishProgress(50)
        val bodum1 = ModuleDCT(FFTM)
        val matrixYBR=bodum1.getYCbCrMatrix(true)
        publishProgress(70);
        val af = MyBufferedImage(matrixYBR);

        val res = af.bufferedImage
        publishProgress(90);
        view?.invoke(res)

        var file=File(getPathWithoutType(pathToBar) + "res.bmp")
        file.createNewFile()
        ImageIO.write(res, "bmp", file)
        publishProgress(100);
    }

    private fun getPathWithoutType(path: String): String {
        return path.substring(0, path.length - 4)
    }

    var listeners:Vector<(value:Int)->Unit> = Vector()
    fun addPublishListener(listener:(value:Int)->Unit){
        listeners.add(listener)
    }
    private fun publishProgress(value: Int) {
        for( l in listeners)
            l(value)
    }
    var view:((image:BufferedImage)->Unit)?=null
}