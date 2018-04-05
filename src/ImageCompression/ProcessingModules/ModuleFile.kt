package ImageCompression.ProcessingModules

import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Containers.ByteVector
import ImageCompression.Utils.Objects.ByteVectorFile
import ImageCompression.Containers.Flag
import ImageCompression.Utils.Objects.TimeManager
import java.io.File

class ModuleFile{
    companion object {
        @JvmStatic val typeMain =".bar"
        @JvmStatic val typeSup =".bas"
    }
    val pathToName:String
    constructor(pathToFile:String,globalBaseW:Int=1,globalBaseH:Int=1){
        pathToName=getPathToName(pathToFile)
        this.globalBaseH=globalBaseH
        this.globalBaseW=globalBaseW
    }
    constructor(file: File,globalBaseW:Int=1,globalBaseH:Int=1){
        pathToName=getPathToName(file.absolutePath)
        this.globalBaseH=globalBaseH
        this.globalBaseW=globalBaseW
    }

    var globalBaseW:Int
    var globalBaseH:Int

    fun write(tripleDataOpcMatrix: TripleDataOpcMatrix, flag: Flag) {
        val v = ByteVector()

        tripleDataOpcMatrix.writeToVector(v, flag)
        if(flag.isChecked(Flag.Parameter.GlobalBase)){
            tripleDataOpcMatrix.writeBaseToVector(v,flag,globalBaseW,globalBaseH)
        }
        TimeManager.Instance.append("box to vector")

        val vw = ByteVectorFile(pathToName + typeMain)
        vw.write(v, flag)

        if (!flag.isChecked(Flag.Parameter.OneFile)) {
            val vbase = ByteVector()
            tripleDataOpcMatrix.writeBaseToVector(v,flag,globalBaseW,globalBaseH)
            TimeManager.Instance.append("base to vector")
            val bw = ByteVectorFile(pathToName + typeSup)
            bw.write(vbase, flag)
        }

    }
    fun read():Pair<TripleDataOpcMatrix, Flag>{
        val fr=ByteVectorFile(pathToName+ typeMain)
        val p=fr.read()
        val flag=p.second
        val vmain=p.first
        val opcs= TripleDataOpcMatrix()

        opcs.readFromVector(vmain,flag)
        if(flag.isChecked(Flag.Parameter.GlobalBase)){
            opcs.readBaseFromVector(vmain,flag)
        }

        if(!flag.isChecked(Flag.Parameter.OneFile)){
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