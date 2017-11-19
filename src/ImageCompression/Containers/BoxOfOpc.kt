package ImageCompression.Containers

import ImageCompression.Utils.Objects.DataOPC
import ImageCompression.Utils.Objects.Flag
import java.awt.image.DataBuffer
import java.io.*

class BoxOfOpc {
    var a: Array<Array<DataOPC>>?=null
    var b: Array<Array<DataOPC>>?=null
    var c: Array<Array<DataOPC>>?=null

    constructor(){    }
    constructor(a:Array<Array<DataOPC>>,b:Array<Array<DataOPC>>,c:Array<Array<DataOPC>>){
        this.a=a
        this.b=b
        this.c=c
    }

    fun writeToOutStream(outputStream: OutputStream,flag: Flag){
        var dio=DataOutputStream(outputStream)

    }
    private fun writeMatrixOpcsToStream(a:Array<Array<DataOPC>>,dos:DataOutputStream,flag: Flag){

    }
    private fun writeDataOpcToStream(dataOPC: DataOPC,dos: DataOutputStream,flag: Flag){

    }
}