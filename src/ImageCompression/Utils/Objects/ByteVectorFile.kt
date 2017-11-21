package ImageCompression.Utils.Objects

import java.io.DataOutputStream
import java.io.File
import java.io.OutputStreamWriter
import sun.misc.IOUtils



class ByteVectorFile(var file: File) {
    constructor(pathToFile: String) : this(File(pathToFile)) {}

    init {
        if (file.isDirectory)
            throw Exception("not a file")
        if (!file.exists())
            file.createNewFile()
    }
    fun write(vector: ByteVector,flag: Flag){
        if(!file.canWrite())
            throw Exception("cann't write")

        val os=file.outputStream()

        os.write(flag.flag.toInt())
        val ba=vector.getByteArray()
        os.write(ba)
        os.close()
    }
    fun read():Pair<ByteVector,Flag>{
        if(!file.canRead())
            throw Exception("can't read")

        val ins=file.inputStream()

        val bf=ins.read()
        val flag=Flag(bf.toShort())
        val bs=ins.readBytes()
        val vec=ByteVector(bs)
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