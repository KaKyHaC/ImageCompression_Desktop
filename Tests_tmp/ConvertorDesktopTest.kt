import ImageCompressionLib.Constants.State
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.TripleDataOpcMatrix
import ImageCompressionLib.Containers.TripleShortMatrix
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.Size
import ImageCompressionLib.ProcessingModules.*
//import ImageCompressionLib.Utils.Functions.Steganography

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.test.assertFails
import kotlin.test.assertTrue

class ConvertorDesktopTest {
    val pathToBmp: String = "/files/desk.bmp"
    val pathToBmpRes: String = "/files/desktest.bmp"
    val w = 1920
    val h = 1080
//    val param = Flag()

    @Test
    fun TestReadWrite() {
        val bmp = ImageIO.read(File(pathToBmp))
        File(pathToBmp).createNewFile()
        ImageIO.write(bmp, "bmp", File(pathToBmpRes))
    }

    @Test
    fun TestMyBufferedImageMatrix5() {
        var matrix = getRandomMatrix(w, h)
        val mi = ModuleImage(matrix)
        val bi = mi.getBufferedImage(true)
        val mi1 = ModuleImage(bi, matrix.parameters)


//        var matrix1 = mi1.getRgbMatrixOld()
//        matrix = mi.getRgbMatrixOld()
//        matrix.assertMatrixInRange(matrix1, 0)

        matrix = mi.getTripleShortMatrix(true)
        var matrix1 = mi1.getTripleShortMatrix(true)
        matrix.assertMatrixInRange(matrix1, 0)

//        matrix1 = mi1.getRgbMatrixOld()
//        matrix = mi.getYenlMatrix(true)
//        assertFails { matrix.assertMatrixInRange(matrix1, 0) }
    }

    @Test
    fun TestBoxOfDUM5() {
        var matrix = getRandomMatrix(w, h)
        matrix = ModuleImage(matrix).getTripleShortMatrix(true)
        matrix.parameters.flag.setTrue(Flag.Parameter.DCT)
        val cpy = matrix.copy()
        val bo = ModuleDCT(matrix)

        val dct = bo.getDCTMatrix(true)
        assertFails { dct.assertMatrixInRange(cpy, 5) }

        val res = bo.getYCbCrMatrix(true)
        res.assertMatrixInRange(cpy, 5)
    }

    @Test
    fun TestTimeBoxofDum() {
//        param.setTrue(Flag.Parameter.DCT)
        var matrix = getRandomMatrix(w, h)
        matrix = ModuleImage(matrix).getTripleShortMatrix(true)
        val bo = ModuleDCT(matrix)

        var t1: Date = Date()
        bo.getDCTMatrix(false)
        bo.getYCbCrMatrix(false)
        val d1 = (Date().time - t1.time)
        System.out.println("in one thread time: $d1")

        t1 = Date()
        bo.getDCTMatrix(true)
        bo.getYCbCrMatrix(true)
        val d2 = (Date().time - t1.time)
        System.out.println("in multi threads time: $d2")

        assertTrue(d2 < d1)
    }

    //    @Test
//    fun TestSteganography(){
//        val matrix=getRandomMatrix(w,h)
//        val m="afdsfsd"
//        Steganography.WriteMassageFromByteArrayToMatrix(matrix,m.toByteArray())
//        val res= String(Steganography.ReadMassageFromMatrix(matrix).toByteArray())
//        assertEquals(m,res)
//    }
    @Test
    fun TestModuleDCT10() {
        val delta = 10
        testModuleDCT(delta)
    }

    @Test
    fun TestModuleDCT7() {
        val delta = 7
        testModuleDCT(delta)

    }

    @Test
    fun TestFullAlgorithmWithoutFile10() {
        val delta = 10
        val matrix = getRandomMatrix(21, 12)
        val t1 = Date().time
        val cpy = matrix.copy()
        cpy.assertMatrixInRange(matrix, 0)

        val myImage = ModuleImage(matrix)
//        myImage.getBufferedImage()
        val ybr = myImage.getTripleShortMatrix(true)
        val ybrCpy = ybr.copy()
        assertFails { cpy.assertMatrixInRange(ybr, 3) }
//        AssertMatrixInRange(ybrCpy,ybr,0)

        val mDCT = ModuleDCT(ybr)
        val dct = mDCT.getDCTMatrix(true)
        val dctCpy = dct.copy()
        //---
//        val mdct1=ModuleDCT(dct)
//        val mdctres=mdct1.getYCbCrMatrix(true)
//        ybrCpy.assertMatrixInRange(mdctres,10)
        //---
//        assertFails { AssertMatrixInRange(cpy,dct,1) }
//        assertFails { AssertMatrixInRange(ybrCpy,dct,1) }

        val seOpc = ModuleOpc(dct, true)//StegoEncrWithOpcOld(dct,f,1,1,null,null,true)
        val box = seOpc.getTripleDataOpcMatrix(null)
        val bvc = box.toByteVectorContainer()
        //----
        //----
        val rBox = TripleDataOpcMatrix.valueOf(bvc)
        rBox.assertEquals(box)
        assertEquals(rBox, box)

        val seOpc2 = ModuleOpc(rBox, true)
        val dctres = seOpc2.getTripleShortMatrix(null).first
        (dctres.assertMatrixInRange(dctCpy, 0))

        val mDCT2 = ModuleDCT(dctres)
        val ynlres = mDCT2.getYCbCrMatrix(true)
        ynlres.assertMatrixInRange(ybrCpy, delta)

//        val myIm2 = ModuleImage(ynlres)
//        val rgb = myIm2.getRgbMatrixOld()
//        rgb.assertMatrixInRange(cpy, delta)
        val t2 = Date().time

        System.out.println("Time direct/reverse FullMode = ${t2 - t1}")
    }

    @Test
    fun TestHDImage1() {
        val f = Flag("0")
        f.setChecked(Flag.Parameter.LongCode, true)
        f.setChecked(Flag.Parameter.DC, true)
        f.setChecked(Flag.Parameter.OneFile, true)
        f.setTrue(Flag.Parameter.DCT)
        testDirectReverseConverting(1920, 1080, 10,f)
    }

    @Test
    fun TestHDImage2() {
        val f = Flag("0")
//        f.setChecked(Flag.Parameter.LongCode,true)
        f.setChecked(Flag.Parameter.DC, true)
        f.setChecked(Flag.Parameter.OneFile, true)
        f.setTrue(Flag.Parameter.DCT)
        testDirectReverseConverting(1920, 1080, 10,f)
    }

    @Test
    fun TestHQImage1() {
        val f = Flag("0")
        f.setChecked(Flag.Parameter.LongCode, true)
        f.setChecked(Flag.Parameter.DC, true)
        f.setTrue(Flag.Parameter.DCT)
        f.setChecked(Flag.Parameter.OneFile, true)
        testDirectReverseConverting(360, 280, 10,f, true)
    }

    @Test
    fun TestHQImage2() {
        val f = Flag("0")
//        f.setChecked(Flag.Parameter.LongCode,true)
        f.setChecked(Flag.Parameter.DC, true)
        f.setChecked(Flag.Parameter.OneFile, true)
        f.setTrue(Flag.Parameter.DCT)
        testDirectReverseConverting(360, 280, 10,f)
    }

    @Test
    fun TestHQImageEnlargement() {
        val f = Flag("0")
//        f.setChecked(Flag.Parameter.LongCode,true)
        f.setChecked(Flag.Parameter.DC, true)
        f.setChecked(Flag.Parameter.OneFile, true)
        f.setChecked(Flag.Parameter.Enlargement, true)
        f.setTrue(Flag.Parameter.DCT)
        testDirectReverseConverting(384, 240, 10,f)
    }

    @Test
    fun TestHQImageAlignment() {
        val f = Flag("0")
        f.setChecked(Flag.Parameter.DC, true)
        f.setChecked(Flag.Parameter.OneFile, true)
        f.setChecked(Flag.Parameter.Alignment, true)
        testDirectReverseConverting(360, 280, 7,f)
    }

    fun testModuleDCT(delta: Int) {
        val matrix = getRandomMatrix(w, h)
        var f = Flag()
        f.setChecked(Flag.Parameter.LongCode, true)
        f.setChecked(Flag.Parameter.DC, true)
        f.setChecked(Flag.Parameter.OneFile, true)
        f.setTrue(Flag.Parameter.DCT)
        val cpy = matrix.copy()
        cpy.assertMatrixInRange(matrix, 0)

        val myImage = ModuleImage(matrix)
        val ybr = myImage.getTripleShortMatrix(true)
        val ybrCpy = ybr.copy()
//        assertFails { AssertMatrixInRange(cpy,ybr,1) }
        ybrCpy.assertMatrixInRange(ybr, 0)

        val mDCT = ModuleDCT(ybr)
        val dct = mDCT.getDCTMatrix(true)
        val dctCpy = dct.copy()
//        assertFails { AssertMatrixInRange(cpy,dct,1) }
//        assertFails { AssertMatrixInRange(ybrCpy,dct,1) }

        val seOpc = ModuleOpc(dct, true)
//        val opcs=seOpc.getModuleOPC()
        val box = seOpc.getTripleDataOpcMatrix(null)
//        f=opcs.param

        val seOpc2 = ModuleOpc(box, true)
        val dctres = seOpc2.getTripleShortMatrix(null).first
        dctres.assertMatrixInRange(dctCpy, 0)

        val mDCT2 = ModuleDCT(dctres)
        val ynlres = mDCT2.getYCbCrMatrix(true)
        ynlres.assertMatrixInRange(ybrCpy, delta)

//        val myIm2 = ModuleImage(ynlres)
//        val rgb = myIm2.getRgbMatrixOld()
//        rgb.assertMatrixInRange(cpy, delta)
    }

    fun testDirectReverseConverting(w: Int, h: Int, delta: Int,flag: Flag, compareCompression: Boolean = false, sameBase: Size = Size(1, 1)) {
        var matrix = getRandomMatrix(w, h,flag)
        val t1 = Date().time
        val cpy = matrix.copy()
        cpy.assertMatrixInRange(matrix, 0)

        val myImage = ModuleImage(matrix)
        //

        //
        val ybr = myImage.getTripleShortMatrix(true)
        val ybrCpy = ybr.copy()
//        assertFails { AssertMatrixInRange(cpy,ybr,0) }
        ybrCpy.assertMatrixInRange(ybr, 0)

        val mDCT = ModuleDCT(ybr)
        val dct = mDCT.getDCTMatrix(true)
        val dctCpy = dct.copy()
//        assertFails { AssertMatrixInRange(cpy,dct,0) }
//        assertFails { AssertMatrixInRange(ybrCpy,dct,0) }

        val seOpc = ModuleOpc(dct, true)
//        val opcs=seOpc.getModuleOPC()
        val box = seOpc.getTripleDataOpcMatrix(null)
        val vec = box.toByteVectorContainer()
//        val vec=ByteVectorParser.instance.parseData(box,param,sameBase.width,sameBase.height)
//        val param=opcs.param
        //----
        val file = ModuleFile(pathToBmp)
        file.write(vec, box.parameters.flag)
        //======
        val pair = file.read()
        //----
//        val f=pair.second
//        val cont=pair.mainData
        val rBox = TripleDataOpcMatrix.valueOf(pair)

        assertEquals("box not equal", rBox, box)
//        assertEquals("param $f!=$param",f,param)

        val seOpc2 = ModuleOpc(rBox, true)
        val dctres = seOpc2.getTripleShortMatrix(null).first
        dctres.assertMatrixInRange(dctCpy, 0)

        val mDCT2 = ModuleDCT(dctres)
        val ynlres = mDCT2.getYCbCrMatrix(true)
        ynlres.assertMatrixInRange(ybrCpy, delta)

//        val myIm2 = ModuleImage(ynlres)
//        val rgb = myIm2.getRgbMatrixOld()
//        rgb.assertMatrixInRange(cpy, delta)
//        assertEquals("${param.param}!=${f.param}",param.param,f.param)
        val t2 = Date().time

        System.out.println("Test d/r Image=${w}x${h}. Time= ${t2 - t1}. Flag=$." +
                " Bar File=${file.getMainFileLength() / 1024}kb. Delta=${delta}")
    }

    fun getRandomMatrix(w: Int, h: Int, flag: Flag = Flag.createDefaultFlag()): TripleShortMatrix {
        val m = TripleShortMatrix(Parameters.createParametresForTest(Size(w, h),flag = flag), State.Origin)
        val rand = Random()
        forEach(w, h, { x, y ->
//            m.a[x, y] = ((x + y) % 255).toShort()
            m.a[x, y] = (Math.abs(Random().nextInt(255))).toShort()
            m.b[x, y] = (Math.abs(Random().nextInt(255))).toShort()
            m.c[x, y] = (Math.abs(Random().nextInt(255))).toShort()
        })
//        m.growMatrix()
        return m
    }


    fun forEach(w: Int, h: Int, let: (x: Int, y: Int) -> Unit) {
        for (i in 0..w - 1) {
            for (j in 0..h - 1) {
                let(i, j)
            }
        }
    }
}


