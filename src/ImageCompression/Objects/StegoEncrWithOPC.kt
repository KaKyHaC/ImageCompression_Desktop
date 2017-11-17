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

    fun FromMatrixToOpcs(isAsync: Boolean=true,progressListener:((x:Int)->Unit)? = null){
        if(!isMatrix)
            return

        progressListener?.invoke(0)

        if(opcs.flag.isSteganography&&message!=null)
            opcs.matrix?.let {
                this.opcs.matrix==Steganography.WriteMassageFromByteArrayToMatrix(opcs.matrix,message?.toByteArray())
            }
        progressListener?.invoke(10)

        directOpc(opcs.flag.isGlobalBase,isAsync,progressListener)
        progressListener?.invoke(80)

        if(opcs.flag.isPassword&&password!=null)
            Encryption.encode(opcs,password)
        progressListener?.invoke(100)

        isOPCS=true
    }
    fun FromOpcToMatrix(isAsync: Boolean=true,progressListener: ((x: Int) -> Unit)?=null){
        if(!isOPCS)
            return

        progressListener?.invoke(0)

        if(opcs.flag.isPassword&&password!=null)
            Encryption.encode(opcs,password)
        progressListener?.invoke(10)

        reverseOpc(isAsync,progressListener)
        progressListener?.invoke(80)

        if(opcs.flag.isSteganography)
            message=String(Steganography.ReadMassageFromMatrix(opcs.matrix).toByteArray())
        progressListener?.invoke(100)

        isMatrix=true
    }
    var baseSizeX:Int=1
    var baseSizeY:Int=1

    private fun directOpc(isGlobalBase:Boolean,isAsync:Boolean,progressListener: ((x: Int) -> Unit)?=null){
        if(isGlobalBase){
            opcs.directOpcGlobalBase(baseSizeX,baseSizeY)
        }else if(isAsync){
            opcs.directOPCMultiThreads()
        }else{
            opcs.directOPC();
        }

    }
    private fun reverseOpc(isAsync:Boolean,progressListener: ((x: Int) -> Unit)?=null){
        if(isAsync){
            opcs.reverseOPCMultiThreads()
        }else{
            opcs.reverseOPC();
        }
    }


    fun getMatrix(isAsync: Boolean=true):Matrix{
        if(!isMatrix)
            FromOpcToMatrix(isAsync)

        assert(isMatrix)
        return opcs.matrix
    }
    fun getOPCS(isAsync: Boolean=true): BoxOfOPC {
        if(!isOPCS)
            FromMatrixToOpcs(isAsync)

        assert(isOPCS)
        return opcs
    }

}