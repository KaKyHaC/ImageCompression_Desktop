package ImageCompressionLib.Utils.Objects

import ImageCompressionLib.Containers.ByteVector
import ImageCompressionLib.Containers.Flag
import ImageCompressionLib.Utils.Functions.CompressionUtils
import java.io.File

//import sun.misc.IOUtils



class ByteVectorFile(var file: File) {
    constructor(pathToFile: String) : this(File(pathToFile)) {}

    init {
        if (file.isDirectory)
            throw Exception("not a file")
        if (!file.exists())
            file.createNewFile()
    }
    fun write(vector: ByteVector, flag: Flag){
        if(!file.canWrite())
            throw Exception("cann't write")

        val os=file.outputStream()

        //flag
        val f=flag.flag
        os.write(f.toInt())
        os.write(f.toInt() shr 8)
        //ByteAray
        var ba=vector.toByteArray()
        if(flag.isChecked(Flag.Parameter.CompressionUtils)) {
            ba = CompressionUtils.compress(ba)
            TimeManager.Instance.append("CompressionUtils")
        }
        os.write(ba)
        os.close()
    }
    fun read():Pair<ByteVector, Flag>{
        if(!file.canRead())
            throw Exception("can't read")

        val ins=file.inputStream()

        //flag
        val lwf=ins.read()
        val hwf=ins.read()
        val flag= Flag((lwf or (hwf shl 8)).toShort())

        //bByteArray
        var bs=ins.readBytes()
        if(flag.isChecked(Flag.Parameter.CompressionUtils)) {
            bs = CompressionUtils.decompress(bs)
            TimeManager.Instance.append("deCompressionUtils")
        }
        val vec= ByteVector(bs)

        ins.close()
        return Pair(vec,flag)
    }
    fun infoToString():String{
        return "File : ${file.absolutePath} \n" +
                "size : ${file.length()/1024} kb"
    }
    fun getFileLength():Long{
        return file.length()
    }
}