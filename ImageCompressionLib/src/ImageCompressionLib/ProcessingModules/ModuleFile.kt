package ImageCompressionLib.ProcessingModules

import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.ByteVectorContainer
import ImageCompressionLib.Containers.Type.Flag
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
    }
    constructor(file: File){
        pathToName=getPathToName(file.absolutePath)
    }
    fun write(vectorContainer: ByteVectorContainer, flag: Flag) {
        TimeManager.Instance.append("box to vector")

        val vw = File(pathToName + typeMain).outputStream()
        vectorContainer.writeToStream(vw)

    }
    fun read():ByteVectorContainer{
        val fr=File(pathToName+ typeMain).inputStream()
        return ByteVectorContainer.readFromStream(fr)
    }

    fun getInfoMainString():String{
        throw Exception("not ready")
//        return ByteVectorFile(pathToName+ typeMain).infoToString()
    }
    fun getMainFileLength():Long{
        throw Exception("not ready")
//        return ByteVectorFile(pathToName+ typeMain).getFileLength()
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