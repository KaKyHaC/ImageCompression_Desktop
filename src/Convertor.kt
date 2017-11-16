import ImageCompression.Containers.Matrix
import ImageCompression.Objects.ApplicationOPC
import ImageCompression.Objects.BoxOfDUM
import ImageCompression.Objects.Flag
import ImageCompression.Objects.MyBufferedImage
import ImageCompression.Utils.AndroidBmpUtil
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import ImageCompression.Objects.MyImage
import ImageCompression.Utils.AndroidBmpUtil.Bitmap
import java.util.*


class Convertor() {
    fun FromBarToBmp(pathToBar: String): Unit {
        val AppOPC = ApplicationOPC.getInstance();
        publishProgress(10)
        val FFTM = AppOPC.FromFileToMatrix({ x -> publishProgress(x) }, getPathWithoutType(pathToBar))
        publishProgress(50)
        val bodum1 = BoxOfDUM(FFTM)
        publishProgress(50);
        bodum1.dataProcessing();
        publishProgress(70);
        val af = MyBufferedImage(bodum1.getMatrix());

        val res = af.bufferedImage
        publishProgress(90);

        ImageIO.write(res, "bmp", File(getPathWithoutType(pathToBar) + "res.bmp"))

        publishProgress(100);
    }

    fun FromBmpToBar(pathToBmp: String, flag: Flag) {
        val bmp = ImageIO.read(File(pathToBmp))

        val mi = MyBufferedImage(bmp, flag)

        publishProgress(10)
        val matrix = mi.yenlMatrix

        val bodum = BoxOfDUM(matrix)
        publishProgress(20)
        bodum.dataProcessing()
        publishProgress(50)

        val AppOPC = ApplicationOPC.getInstance()
        //AppOPC.setSizeofblock(n,m);

        AppOPC.FromMatrixToFile({ x -> publishProgress(x) }, bodum.matrix, getPathWithoutType(pathToBmp))
        publishProgress(100)

    }

    private fun getPathWithoutType(path: String): String {
        return path.substring(0, path.length - 4)
    }

    var listeners:Vector<(value:Int)->Unit> = Vector()
    public fun addPublishListener(listener:(value:Int)->Unit){
        listeners.add(listener)
    }
    private fun publishProgress(value: Int) {
        for( l in listeners)
            l(value)
    }
}