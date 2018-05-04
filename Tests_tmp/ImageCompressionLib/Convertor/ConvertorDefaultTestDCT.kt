//package ImageCompressionLib.Convertor
//
//import ConvertorDesktopTest.Companion.AssertMatrixInRange
//import ImageCompressionLib.Containers.Type.Flag
//import ImageCompressionLib.Containers.Type.Size
//import ImageCompressionLib.Containers.TripleShortMatrixOldTest.Companion.getRandomMatrix
//import ImageCompressionLib.ProcessingModules.ModuleDCT
//import ImageCompressionLib.ProcessingModules.ModuleImage
//import org.junit.Before
//
//import org.junit.Assert.*
//import org.junit.Test
//import java.util.*
//import kotlin.test.assertFails
//
//class ConvertorDefaultTestDCT {
//
//    @Before
//    fun setUp() {
//    }
//
//    @Test
//    fun testConvertorWithoutDCT(){
//        val f= Flag.createDefaultFlag()
//        f.setFalse(Flag.Parameter.DCT)
//        f.setFalse(Flag.Parameter.Enlargement)
//        testDirectReverseConverting(256,256,f,5)
//    }
//    fun testDirectReverseConverting(w:Int, h:Int, flag: Flag, delta:Int, compareCompression:Boolean=false, sameBase: Size = Size(1, 1)){
//        var matrix=getRandomMatrix(w,h)
//        val t1= Date().time
//        val cpy=matrix.copy()
////        AssertMatrixInRange(cpy,matrix,0)
//
//        val myImage= ModuleImage(matrix, flag)
//
//        val ybr=myImage.getYenlMatrix(true)
//        val ybrCpy=ybr.copy()
//        assertFails { AssertMatrixInRange(cpy,ybr,0) }
//        AssertMatrixInRange(ybrCpy,ybr,0)
//
//        val mDCT= ModuleDCT(ybr,flag)
//        val dct=mDCT.getDCTMatrix(true)
//        val dctCpy=dct.copy()
//        assertFails { AssertMatrixInRange(cpy,dct,0)}
////        AssertMatrixInRange(ybrCpy,dct,0)
//
//        val seOpc= StegoEncrWithOpcOld(dct,flag,sameBase.width,sameBase.height,null,null,true)
////        val opcs=seOpc.getModuleOPC()
//        val box=seOpc.getBoxOfOpc(true)
//        val vec= ByteVectorParser.instance.parseData(box,flag,sameBase.width,sameBase.height)
////        val param=opcs.param
//        //----
////        val file= ModuleFile(pathToBmp)
////        file.write(vec,param)
//        //======
////        val pair=file.read()
//        //----
//        val f=flag
//        val cont=vec
//        val rBox= ByteVectorParser.instance.parseVector(cont,f)
//
//        assertEquals("box not equal",rBox,box)
//        assertEquals("param $f!=$flag",f,flag)
//
//        val seOpc2= StegoEncrWithOpcOld(rBox, f,1,1,null,true)
//        val dctres=seOpc2.getMatrix(true)
////        AssertMatrixInRange(dctres,dctCpy,0)
//
//        val mDCT2= ModuleDCT(dctres,flag)
//        val ynlres=mDCT2.getYCbCrMatrix(true)
////        AssertMatrixInRange(ynlres,ybrCpy,delta)
//        AssertMatrixInRange(ynlres,ybrCpy,delta)
//
//        val myIm2= ModuleImage(ynlres, flag)
//        val rgb=myIm2.rgbMatrixOld
//        AssertMatrixInRange(rgb,cpy,delta)
//        assertEquals("${flag.flag}!=${f.flag}",flag.flag,f.flag)
//        val t2= Date().time
//
////        System.out.println("Test d/r Image=${w}x${h}. Time= ${t2-t1}. Flag=${f.param}." +
////                " Bar File=${file.getMainFileLength()/1024}kb. Delta=${delta}")
//    }
//}
