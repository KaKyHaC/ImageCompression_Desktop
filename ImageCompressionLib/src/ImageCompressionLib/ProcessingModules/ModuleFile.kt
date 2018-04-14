package ImageCompressionLib.ProcessingModules

import ImageCompressionLib.Containers.TripleDataOpcMatrix
import ImageCompressionLib.Containers.ByteVector
import ImageCompressionLib.Containers.ByteVectorContainer
import ImageCompressionLib.Utils.Objects.ByteVectorFile
import ImageCompressionLib.Containers.Flag
import ImageCompressionLib.Utils.Objects.TimeManager
import java.io.File

class ModuleFile{
    companion object {
        @JvmStatic val typeMain =".bar"
        @JvmStatic val typeSup =".bas"
    }
    val pathToName:String
    constructor(pathToFile:String){
        pathToName=getPathToName(pathToFile)
//        this.globalBaseH=globalBaseH
//        this.globalBaseW=globalBaseW
    }
    constructor(file: File){
        pathToName=getPathToName(file.absolutePath)
//        this.globalBaseH=globalBaseH
//        this.globalBaseW=globalBaseW
    }

//    var globalBaseW:Int
//    var globalBaseH:Int

    fun write(vectorContainer: ByteVectorContainer, flag: Flag) {
//        val v = ByteVector()

//        tripleDataOpcMatrix.writeToVector(v, flag)
//        if(flag.isChecked(Flag.Parameter.GlobalBase)){
//            tripleDataOpcMatrix.writeBaseToVector(v,flag,globalBaseW,globalBaseH)
//        }
        TimeManager.Instance.append("box to vector")

        val vw = ByteVectorFile(pathToName + typeMain)
        vw.write(vectorContainer.mainData, flag)

        if (!flag.isChecked(Flag.Parameter.OneFile)) {
//            val vbase = ByteVector()
//            tripleDataOpcMatrix.writeBaseToVector(v,flag,globalBaseW,globalBaseH)
            TimeManager.Instance.append("base to vector")
            val bw = ByteVectorFile(pathToName + typeSup)
            if(vectorContainer.suportData==null)
                throw Exception("vectorContainer.suportData==null")

            bw.write(vectorContainer.suportData, flag)
        }

    }
    fun read():Pair<ByteVectorContainer, Flag>{
        val fr=ByteVectorFile(pathToName+ typeMain)
        val p=fr.read()
        val flag=p.second
        val vmain=p.first
//        val opcs= TripleDataOpcMatrix()

//        opcs.readFromVector(vmain,flag)
//        if(flag.isChecked(Flag.Parameter.GlobalBase)){
//            opcs.readBaseFromVector(vmain,flag)
//        }
        var base:ByteVector?=null
        if(!flag.isChecked(Flag.Parameter.OneFile)){
            val br=ByteVectorFile(pathToName+ typeSup)
            val bp=br.read()
            base=bp.first
//            opcs.readBaseFromVector(base,flag)
        }
        return Pair(ByteVectorContainer(vmain,base),flag)
    }

    fun getInfoMainString():String{
        return ByteVectorFile(pathToName+ typeMain).infoToString()
    }
    fun getMainFileLength():Long{
        return ByteVectorFile(pathToName+ typeMain).getFileLength()
    }
    private fun getPathToName(pathToFile: String):String{
        var res=pathToFile
        if(pathToFile.contains('.')) {
            val ss=pathToFile.split('.')
            res=ss[0]
        }
        return res
    }
}