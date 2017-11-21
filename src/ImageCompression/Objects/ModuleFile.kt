package ImageCompression.Objects

import ImageCompression.Containers.BoxOfOpc
import ImageCompression.Utils.Objects.ByteVector
import ImageCompression.Utils.Objects.ByteVectorFile
import ImageCompression.Utils.Objects.Flag
import java.io.File

class ModuleFile{
    companion object {
        @JvmStatic val typeMain =".bar"
        @JvmStatic val typeSup =".bas"
    }
    val pathToName:String
    constructor(pathToFile:String){
        pathToName=getPathToName(pathToFile)
    }
    constructor(file: File){
        pathToName=getPathToName(file.absolutePath)
    }
    fun write(boxOfOpc: BoxOfOpc,flag: Flag) {
        val v = ByteVector()
        boxOfOpc.writeToVector(v, flag)
        val vw = ByteVectorFile(pathToName + typeMain)
        vw.write(v, flag)

        if (!flag.isOneFile) {
            val vbase = ByteVector()
            boxOfOpc.writeBaseToVector(vbase, flag)
            val bw = ByteVectorFile(pathToName + typeSup)
            bw.write(vbase, flag)
        }
    }
    fun read():Pair<BoxOfOpc,Flag>{
        val fr=ByteVectorFile(pathToName+ typeMain)
        val p=fr.read()
        val flag=p.second
        val vmain=p.first
        val opcs=BoxOfOpc()
        opcs.readFromVector(vmain,flag)
        if(!flag.isOneFile){
            val br=ByteVectorFile(pathToName+ typeSup)
            val bp=br.read()
            val base=bp.first
            opcs.readBaseFromVector(base,flag)
        }
        return Pair(opcs,flag)
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