import ImageCompression.Objects.ApplicationOPC
import ImageCompression.Objects.BoxOfDUM
import ImageCompression.Utils.Objects.Flag
import ImageCompression.Objects.MyBufferedImage
import java.io.File
import javax.imageio.ImageIO
import java.util.*


class Convertor() {

    fun FromBmpToBar(pathToBmp: String, flag: Flag) {
        publishProgress(0)
        val bmp = ImageIO.read(File(pathToBmp))
        publishProgress(10)
        val mi = MyBufferedImage(bmp, flag)
        val matrix = mi.yenlMatrix
        publishProgress(30)
        val bodum = BoxOfDUM(matrix)
        val matrixDCT=bodum.getDCTMatrix(true)
        publishProgress(60)
        val AppOPC = ApplicationOPC.getInstance()
        AppOPC.FromMatrixToFile({ x -> publishProgress(x) }, matrixDCT, getPathWithoutType(pathToBmp))
        publishProgress(100)
    }

    fun FromBarToBmp(pathToBar: String): Unit {
        val AppOPC = ApplicationOPC.getInstance();
        publishProgress(10)
        val FFTM = AppOPC.FromFileToMatrix({ x -> publishProgress(x) }, getPathWithoutType(pathToBar))
        publishProgress(50)
        val bodum1 = BoxOfDUM(FFTM)
        publishProgress(50);
        val matrixYBR=bodum1.getYCbCrMatrix(true)
        publishProgress(70);
        val af = MyBufferedImage(matrixYBR);

        val res = af.bufferedImage
        publishProgress(90);

        var file=File(getPathWithoutType(pathToBar) + "res.bmp")
        file.createNewFile()
        try {
            ImageIO.write(res, "bmp", file)
        }catch (e:Exception){
            System.out.println(e.message)
        }

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
}