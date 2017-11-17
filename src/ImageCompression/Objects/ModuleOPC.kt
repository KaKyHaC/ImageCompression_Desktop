package ImageCompression.Objects

import ImageCompression.Containers.BoxOfOPC
import ImageCompression.Containers.Matrix
import ImageCompression.Utils.Functions.Encryption
import ImageCompression.Utils.Functions.Steganography
import ImageCompression.Utils.Objects.Flag
import sun.net.ProgressListener

class ModuleOPC {
    private var matrix:Matrix
    private var opcs:BoxOfOPC
    var isMatrix=false
        private set
    var isOPCS=false
        private set

    constructor(matrix: Matrix){
        this.matrix=matrix
        opcs= BoxOfOPC(matrix.Width,matrix.Height,matrix.f.isEnlargement)
        isMatrix=true
    }
    constructor(boxOfOPC: BoxOfOPC,flag: Flag){
        this.opcs=boxOfOPC
        matrix= Matrix(opcs.width,opcs.height,flag)
        isOPCS=true
    }
    var message:String?=null
    var password:String?=null

    fun FromMatrixToOpcs(progressListener:((x:Int)->Unit)? = null){
        if(!isMatrix)
            return

        progressListener?.invoke(0)

        if(matrix.f.isSteganography&&message!=null)
            matrix=Steganography.WriteMassageFromByteArrayToMatrix(matrix,message?.toByteArray())
        progressListener?.invoke(10)

        directOpc(matrix.f.isGlobalBase,progressListener)
        progressListener?.invoke(80)

        if(matrix.f.isPassword&&password!=null)
            Encryption.encode(opcs,password)
        progressListener?.invoke(100)

        isOPCS=true
    }
    fun FromOpcToMatrix(progressListener: ((x: Int) -> Unit)?=null){
        if(!isOPCS)
            return

        progressListener?.invoke(0)

        if(matrix.f.isPassword&&password!=null)
            Encryption.encode(opcs,password)
        progressListener?.invoke(10)

        reverseOpc(matrix.f.isGlobalBase)
        progressListener?.invoke(80)

        if(matrix.f.isSteganography)
            message=String(Steganography.ReadMassageFromMatrix(matrix).toByteArray())
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
        return matrix
    }
    fun getOPCS():BoxOfOPC{
        if(!isOPCS)
            FromMatrixToOpcs()

        assert(isOPCS)
        return opcs
    }

}