//package Utils
//
//import ImageCompressionLib.ProcessingModules.*
//import ImageCompressionLib.Containers.Type.Flag
//import ImageCompressionLib.Utils.Objects.TimeManager
//import java.awt.image.BufferedImage
//import java.io.File
//import javax.imageio.ImageIO
//
//
//class ConvertorDesktop private constructor(){
//    companion object {
//        @JvmStatic val instance= ConvertorDesktop()
//        @JvmStatic fun getInstance(progressListener:((value:Int,text:String)->Unit)?=null
//                                   ,view:((image:BufferedImage)->Unit)?=null): ConvertorDesktop {
//            instance.view=view
//            instance.progressListener=progressListener
//            return instance
//        }
//    }
//    enum class Computing{OneThread,MultiThreads,MultiProcessor}
//    data class Info(val flag: Flag, val password: String?=null
//                    , val message: String?=null, val sameBaseWidth:Int=1, val sameBaseHeight:Int=1)
//
//    fun FromBmpToBar(pathToBmp: String, info: Info, computing: Computing = Computing.MultiThreads) {
//        val isAsync=(computing== Computing.MultiThreads)
//            val timeManager=TimeManager.Instance
//            timeManager.startNewTrack("FromBmpToBar ${isAsync}")
//            progressListener?.invoke(0,"read bmp")
//        val bmp = ImageIO.read(File(pathToBmp))
//            timeManager.append("read bmp")
//            progressListener?.invoke(10,"RGB to YcBcR")
//            view?.invoke(bmp)
//        val mbi=BuffImConvertor.instance.convert(bmp)
//        val mi = ModuleImage(mbi, info.flag)
//        val matrix = mi.getYenlMatrix(isAsync)
//            timeManager.append("rgb to yenl")
//            progressListener?.invoke(30,"direct DCT")
//        val bodum = ModuleDCT(matrix!!,info.flag)
//        val matrixDCT=bodum.getDCTMatrix(isAsync)
//            timeManager.append("direct DCT")
//            progressListener?.invoke(60,"direct OPC")
//        val StEnOPC= StegoEncrWithOpcOld(matrixDCT!!,info.flag,info.sameBaseWidth,info.sameBaseHeight,info.message,info.password,isAsync)
////        StEnOPC.password=password
////        StEnOPC.baseSizeW=globalBaseW
////        StEnOPC.baseSizeH=globalBaseH
//        val box=StEnOPC.getTripleDataOpcMatrix()
//            timeManager.append("direct OPC")
//            progressListener?.invoke(80,"write to file")
//        val vector=ModuleByteVector(box,info.flag,info.sameBaseWidth,info.sameBaseHeight).getVectorContainer()
//        val fileModule=ModuleFile(pathToBmp)
////        fileModule.globalBaseW=globalBaseW
////        fileModule.globalBaseH=globalBaseH
//        fileModule.write(vector,info.flag)
//            timeManager.append("write to file")
//            progressListener?.invoke(100,"Ready after ${timeManager.getTotalTime()} ms")
//            System.out.println(timeManager.getInfoInSec())
//    }
//
//    fun FromBarToBmp(pathToBar: String,password: String?=null,computing: Computing = Computing.MultiThreads): Unit {
//        val isAsync=(computing== Computing.MultiThreads)
//            val timeManager=TimeManager.Instance
//            timeManager.startNewTrack("FromBarToBmp ${isAsync}")
//            progressListener?.invoke(10,"read from file")
//        val fileModule=ModuleFile(pathToBar)
//        val (cont,flag)=fileModule.read()
//        val box= ByteVectorParser.instance.parseVector(cont,flag)
////        val flag=pair.second
//            timeManager.append("read from file")
//            progressListener?.invoke(10,"reverse OPC")
//        val mOPC= StegoEncrWithOpcOld(box, flag,password=password,isAsync = isAsync)
////        mOPC.password=password
//        val FFTM =mOPC.getTripleShortMatrix()
//            timeManager.append("reverse OPC")
//            progressListener?.invoke(50,"reverse DCT")
//        val bodum1 = ModuleDCT(FFTM,flag)
//        val matrixYBR=bodum1.getYCbCrMatrix(isAsync)
//            timeManager.append("reverse DCT")
//            progressListener?.invoke(70,"YcBcR to BMP");
//        val af = ModuleImage(matrixYBR!!, flag);
//        val res = af.bufferedImage
//        val bmp=BuffImConvertor.instance.convert(res!!)
//            timeManager.append("yenl to bmp")
//            progressListener?.invoke(90,"Write to BMP");
////            view?.invoke(res)
//        val file=File(getPathWithoutType(pathToBar) + "res.bmp")
//        file.createNewFile()
//        ImageIO.write(bmp, "bmp", file)
//            timeManager.append("write to bmp")
//            progressListener?.invoke(100,"Ready after ${timeManager.getTotalTime()} ms");
//            System.out.println(timeManager.getInfoInSec())
//            view?.invoke(bmp)
//    }
//
//    private fun getPathWithoutType(path: String): String {
//        return path.substring(0, path.length - 4)
//    }
//
//    var progressListener:((value:Int,text:String)->Unit)?=null
//    var view:((image:BufferedImage)->Unit)?=null
//}