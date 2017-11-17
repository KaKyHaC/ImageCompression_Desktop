package ImageCompression.Objects

import ImageCompression.Containers.Matrix
import ImageCompression.Utils.Functions.Encryption
import ImageCompression.Utils.Functions.Steganography
import ImageCompression.Utils.Objects.Flag

class ModuleOPC {
    private var opcs: BoxOfOPC
    var isMatrix=false
        private set
    var isOPCS=false
        private set

    constructor(matrix: Matrix){
        opcs= BoxOfOPC(matrix)
        isMatrix=true
    }
    constructor(boxOfOPC: BoxOfOPC, flag: Flag){
        this.opcs=boxOfOPC
        isOPCS=true
    }
    var message:String?=null
    var password:String?=null

    fun FromMatrixToOpcs(progressListener:((x:Int)->Unit)? = null){
        if(!isMatrix)
            return

        progressListener?.invoke(0)

        if(opcs.flag.isSteganography&&message!=null)
            opcs.matrix?.let {
                this.opcs.matrix==Steganography.WriteMassageFromByteArrayToMatrix(opcs.matrix,message?.toByteArray())
            }
        progressListener?.invoke(10)

        directOpc(opcs.flag.isGlobalBase,progressListener)
        progressListener?.invoke(80)

        if(opcs.flag.isPassword&&password!=null)
            Encryption.encode(opcs,password)
        progressListener?.invoke(100)

        isOPCS=true
    }
    fun FromOpcToMatrix(progressListener: ((x: Int) -> Unit)?=null){
        if(!isOPCS)
            return

        progressListener?.invoke(0)

        if(opcs.flag.isPassword&&password!=null)
            Encryption.encode(opcs,password)
        progressListener?.invoke(10)

        reverseOpc(opcs.flag.isGlobalBase)
        progressListener?.invoke(80)

        if(opcs.flag.isSteganography)
            message=String(Steganography.ReadMassageFromMatrix(opcs.matrix).toByteArray())
        progressListener?.invoke(100)

        isMatrix=true
    }
    private fun directOpc(isGlobalBase:Boolean,progressListener: ((x: Int) -> Unit)?=null){
        throw Exception()
    }
    private fun reverseOpc(isGlobalBase: Boolean,progressListener: ((x: Int) -> Unit)?=null){
        throw Exception()
    }


    fun getMatrix():Matrix{
        if(!isMatrix)
            FromOpcToMatrix()

        assert(isMatrix)
        return opcs.matrix
    }
    fun getOPCS(): BoxOfOPC {
        if(!isOPCS)
            FromMatrixToOpcs()

        assert(isOPCS)
        return opcs
    }

}