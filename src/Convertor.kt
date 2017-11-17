import ImageCompression.Objects.BoxOfOPC
import ImageCompression.Objects.ModuleDCT
import ImageCompression.Objects.ModuleOPC
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
        val bodum = ModuleDCT(matrix)
        val matrixDCT=bodum.getDCTMatrix(true)
        publishProgress(60)
        val mOpc=ModuleOPC(matrixDCT)
        val box=mOpc.getOPCS(true)
        publishProgress(80)
        //TODO write to bar
        publishProgress(100)
    }

    fun FromBarToBmp(pathToBar: String): Unit {
        //TODO read from bar
        val box= BoxOfOPC(null)//read from file
        val flag=Flag("0")
        publishProgress(10)
        val mOPC=ModuleOPC(box,flag)
        val FFTM =mOPC.getMatrix(true)
        publishProgress(50)
        val bodum1 = ModuleDCT(FFTM)
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