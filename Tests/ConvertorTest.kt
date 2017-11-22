import ImageCompression.Containers.BoxOfOpc
import ImageCompression.Containers.Matrix
import ImageCompression.Constants.State
import ImageCompression.Objects.*
import ImageCompression.Utils.Functions.CompressionUtils
import ImageCompression.Utils.Objects.Flag
import ImageCompression.Utils.Functions.Steganography
import ImageCompression.Utils.Objects.ByteVector
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.test.assertFails
import kotlin.test.assertTrue

class ConvertorTest {
    val pathToBmp:String="/files/desk.bmp"
    val pathToBmpRes:String="/files/desktest.bmp"
    val w=1920
    val h=1080

    @Test
    fun TestReadWrite(){
        val bmp = ImageIO.read(File(pathToBmp))
        File(pathToBmp).createNewFile()
        ImageIO.write(bmp,"bmp",File(pathToBmpRes))
    }
    @Test
    fun TestMyBufferedImageMatrix5(){
        var matrix=getRandomMatrix(w,h, Flag("0"))
        val mi=MyBufferedImage(matrix)
        val bi=mi.bufferedImage
        val mi1=MyBufferedImage(bi,matrix.f)


        var matrix1=mi1.rgbMatrix
        matrix=mi.rgbMatrix
        AssertMatrixInRange(matrix,matrix1,0)

        matrix=mi.yCbCrMatrix
        matrix1=mi1.yCbCrMatrix
        AssertMatrixInRange(matrix,matrix1,0)

        matrix1=mi1.rgbMatrix
        matrix=mi.yCbCrMatrix
        AssertMatrixInRange(matrix1,matrix,0,false)
    }
    @Test
    fun TestBoxOfDUM5(){
        var matrix=getRandomMatrix(w,h,Flag("0"))
        matrix=MyBufferedImage(matrix).yCbCrMatrix
        val cpy=matrix.copy()
        val bo= ModuleDCT(matrix)

        bo.dataProcessingInThreads()
        AssertMatrixInRange(matrix,cpy,5,false)

        bo.dataProcessingInThreads()
        AssertMatrixInRange(matrix,cpy,5)
    }
    @Test
    fun TestTimeBoxofDum(){
        var matrix=getRandomMatrix(w,h,Flag("0"))
        matrix=MyBufferedImage(matrix).yCbCrMatrix
        val bo= ModuleDCT(matrix)

        var t1:Date=Date()
        bo.dataProcessing()
        bo.dataProcessing()
        val d1=(Date().time-t1.time)
        System.out.println("in one thread time: $d1")

        t1=Date()
        bo.dataProcessingInThreads()
        bo.dataProcessingInThreads()
        val d2=(Date().time-t1.time)
        System.out.println("in multi threads time: $d2")

        assertTrue(d2<d1)
    }
    @Test
    fun TestSteganography(){
        var matrix=getRandomMatrix(w,h,Flag("0"))
        val m="afdsfsd"
        Steganography.WriteMassageFromByteArrayToMatrix(matrix,m.toByteArray())
        val res= String(Steganography.ReadMassageFromMatrix(matrix).toByteArray())
        assertEquals(m,res)
    }
    @Test
    fun TestModuleDCT5(){
        val delta=5
        testModuleDCT(delta)
    }
    @Test
    fun TestModuleDCT7(){
        val delta=7
        testModuleDCT(delta)

    }
    @Test
    fun TestFullAlgorithmWithoutFile8(){
        val delta=8
        var matrix=getRandomMatrix(w,h,Flag("0"))
        val t1=Date().time
        matrix.f.isLongCode=true
        matrix.f.isDC=true
        matrix.f.isOneFile=true
        val cpy=matrix.copy()
        AssertMatrixInRange(cpy,matrix,0)

        val myImage=MyBufferedImage(matrix)
        val ybr=myImage.yCbCrMatrix
        val ybrCpy=ybr.copy()
        assertFails { AssertMatrixInRange(cpy,ybr,1) }
        AssertMatrixInRange(ybrCpy,ybr,0)

        val mDCT=ModuleDCT(ybr)
        val dct=mDCT.getDCTMatrix(true)
        val dctCpy=dct.copy()
        assertFails { AssertMatrixInRange(cpy,dct,1) }
        assertFails { AssertMatrixInRange(ybrCpy,dct,1) }

        val seOpc=StegoEncrWithOPC(dct)
        val opcs=seOpc.getModuleOPC()
        val box=opcs.boxOfOpc
        val flag1=opcs.flag
        val vb=ByteVector()
        box.writeToVector(vb,flag1)
        //----
        //----
        val f=opcs.flag
        val rBox=BoxOfOpc()
        rBox.readFromVector(vb,f)
        assertEquals(rBox,box)

        val seOpc2=StegoEncrWithOPC(ModuleOPC(rBox,f),f)
        val dctres=seOpc2.getMatrix(true)
        AssertMatrixInRange(dctres,dctCpy,0)

        val mDCT2=ModuleDCT(dctres)
        val ynlres=mDCT2.getYCbCrMatrix(true)
        AssertMatrixInRange(ynlres,ybrCpy,delta)

        val myIm2=MyBufferedImage(ynlres)
        val rgb=myIm2.rgbMatrix
        AssertMatrixInRange(rgb,cpy,delta)
        val t2=Date().time

        System.out.println("Time direct/reverse FullMode = ${t2-t1}")
    }

    @Test
    fun TestHDImage1(){
        val f=Flag("0")
        f.isOneFile=true
        f.isDC=true
        f.isLongCode=true
        testDirectReverseConverting(1920,1080,f,8)
    }
    @Test
    fun TestHDImage2(){
        val f=Flag("0")
        f.isOneFile=true
        f.isDC=true
        testDirectReverseConverting(1920,1080,f,8)
    }
    @Test
    fun TestHQImage1(){
        val f=Flag("0")
        f.isOneFile=true
        f.isDC=true
        f.isLongCode=true
        testDirectReverseConverting(360,280,f,7,true)
    }
    @Test
    fun TestHQImage2(){
        val f=Flag("0")
        f.isOneFile=true
        f.isDC=true
        testDirectReverseConverting(360,280,f,7)
    }
    @Test
    fun TestHQImageEnlargement(){
        val f=Flag("0")
        f.isOneFile=true
        f.isDC=true
        f.isEnlargement=true
        testDirectReverseConverting(384,240,f,7)
    }
    @Test
    fun TestHQImageAlignment(){
        val f=Flag("0")
        f.isOneFile=true
        f.isDC=true
        f.isAlignment=true
        testDirectReverseConverting(360,280,f,7)
    }

    fun testModuleDCT(delta: Int){
        var matrix=getRandomMatrix(w,h,Flag("0"))
        matrix.f.isLongCode=true
        matrix.f.isDC=true
        matrix.f.isOneFile=true
        val cpy=matrix.copy()
        AssertMatrixInRange(cpy,matrix,0)

        val myImage=MyBufferedImage(matrix)
        val ybr=myImage.yCbCrMatrix
        val ybrCpy=ybr.copy()
        assertFails { AssertMatrixInRange(cpy,ybr,1) }
        AssertMatrixInRange(ybrCpy,ybr,0)

        val mDCT=ModuleDCT(ybr)
        val dct=mDCT.getDCTMatrix(true)
        val dctCpy=dct.copy()
        assertFails { AssertMatrixInRange(cpy,dct,1) }
        assertFails { AssertMatrixInRange(ybrCpy,dct,1) }

        val seOpc=StegoEncrWithOPC(dct)
        val opcs=seOpc.getModuleOPC()
        val box=opcs.boxOfOpc
        val f=opcs.flag

        val seOpc2=StegoEncrWithOPC(ModuleOPC(box,f),f)
        val dctres=seOpc2.getMatrix(true)
        AssertMatrixInRange(dctres,dctCpy,0)

        val mDCT2=ModuleDCT(dctres)
        val ynlres=mDCT2.getYCbCrMatrix(true)
        AssertMatrixInRange(ynlres,ybrCpy,delta)

        val myIm2=MyBufferedImage(ynlres)
        val rgb=myIm2.rgbMatrix
        AssertMatrixInRange(rgb,cpy,delta)
    }
    fun testDirectReverseConverting(w:Int,h:Int,flag: Flag,delta:Int,compareCompression:Boolean=false){
        var matrix=getRandomMatrix(w,h,flag)
        val t1=Date().time
        val cpy=matrix.copy()
        AssertMatrixInRange(cpy,matrix,0)

        val myImage=MyBufferedImage(matrix)
        //
        if(compareCompression) {
            val bv = myImage.byteVector
            val ba = bv.getByteArray()
            val cba = CompressionUtils.compress(ba)
            System.out.println("ComprU size= ${cba.size/1024}Kb")
        }
        //
        val ybr=myImage.yenlMatrix
        val ybrCpy=ybr.copy()
        assertFails { AssertMatrixInRange(cpy,ybr,0) }
        AssertMatrixInRange(ybrCpy,ybr,0)

        val mDCT=ModuleDCT(ybr)
        val dct=mDCT.getDCTMatrix(true)
        val dctCpy=dct.copy()
        assertFails { AssertMatrixInRange(cpy,dct,0) }
        assertFails { AssertMatrixInRange(ybrCpy,dct,0) }

        val seOpc=StegoEncrWithOPC(dct)
        val opcs=seOpc.getModuleOPC()
        val box=opcs.boxOfOpc
        val flag=opcs.flag
        //----
        val file=ModuleFile(pathToBmp)
        file.write(box,flag)
        //======
        val pair=file.read()
        //----
        val f=pair.second
        val rBox=pair.first
        assertEquals("box not equal",rBox,box)
        assertEquals("flag $f!=$flag",f,flag)

        val seOpc2=StegoEncrWithOPC(ModuleOPC(rBox,f),f)
        val dctres=seOpc2.getMatrix(true)
        AssertMatrixInRange(dctres,dctCpy,0)

        val mDCT2=ModuleDCT(dctres)
        val ynlres=mDCT2.getYCbCrMatrix(true)
        AssertMatrixInRange(ynlres,ybrCpy,delta)

        val myIm2=MyBufferedImage(ynlres)
        val rgb=myIm2.rgbMatrix
        AssertMatrixInRange(rgb,cpy,delta)
        assertEquals("${flag.flag}!=${f.flag}",flag.flag,f.flag)
        val t2=Date().time

        System.out.println("Test d/r Image=${w}x${h}. Time= ${t2-t1}. Flag=${f.flag}." +
                " Bar File=${file.getMainFileLength()/1024}kb. Delta=${delta}")
    }
    fun getRandomMatrix(w:Int,h:Int,flag:Flag):Matrix{
        val m =Matrix(w,h,flag,State.RGB)
        val rand=Random()
        forEach(w,h,{x, y ->
//            m.a[x][y]=rand.nextInt(255).toShort()
//            m.c[x][y]=rand.nextInt(255).toShort()
//            m.b[x][y]=rand.nextInt(255).toShort()
            m.a[x][y]=((x+y)%255).toShort()
            m.b[x][y]=((x+y)%255).toShort()
            m.c[x][y]=((x+y)%255).toShort()
        })
        return m
    }

    fun Matrix.copy():Matrix{
        var res=Matrix(this.Width,this.Height, Flag(this.f.flag),state)
        forEach(Width,Height,{x,y->
            res.a[x][y]=a[x][y]
            res.b[x][y]=b[x][y]
            res.c[x][y]=c[x][y]
        })
        return res
    }
    fun AssertMatrixInRange(m:Matrix,m1:Matrix,delta:Int,inRange:Boolean=true){
        if(inRange) {
            assertEquals("m1.State=${m.state} m2.State=${m1.state}"
                    , m.state, m1.state)
            assertEquals("flag ${m.f}!=${m1.f}",m.f,m1.f)
            assertEquals(m.Width, m1.Width)
            assertEquals(m.Height, m1.Height)
        }

        AssertArrayArrayInRange(m.a,m1.a,delta,inRange)
        AssertArrayArrayInRange(m.b,m1.b,delta,inRange)
        AssertArrayArrayInRange(m.c,m1.c,delta,inRange)
    }
    fun AssertArrayArrayInRange(a:Array<ShortArray>,a1:Array<ShortArray>,delta:Int,inRange:Boolean=true){
        assertEquals(a.size,a1.size)
        assertEquals(a[0].size,a1[0].size)

        var totalEqual=true
        forEach(a.size,a[0].size,{x,y->
            var isEqual=false
            for(i in -delta..delta) {
                if (a[x][y] == (a1[x][y] + i).toShort())
                    isEqual = true
            }
            if(inRange)
                assertTrue("in [$x][$y] val ${a[x][y]}!=${a1[x][y]}"
                        , isEqual)

            totalEqual = totalEqual && isEqual


        })
        if(inRange){
            assertTrue("total not equal",totalEqual)
        }else{
            assertFalse("total is equal",totalEqual)
        }
    }

    fun forEach(w:Int,h:Int,let:(x:Int,y:Int)->Unit){
        for(i in 0..w-1){
            for(j in 0..h-1){
                let(i,j)
            }
        }
    }

}