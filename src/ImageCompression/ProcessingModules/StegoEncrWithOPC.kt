package ImageCompression.ProcessingModules

import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Containers.TripleShortMatrix
import ImageCompression.Utils.Functions.Encryption
import ImageCompression.Utils.Functions.Steganography
import ImageCompression.Utils.Objects.Flag
import ImageCompression.Utils.Objects.TimeManager

class StegoEncrWithOPC {
    private var opcs: ModuleOPC

    constructor(tripleShortMatrix: TripleShortMatrix){
        opcs= ModuleOPC(tripleShortMatrix)
//        getTripleShortMatrix=true
    }
    constructor(tripleDataOpcMatrix: TripleDataOpcMatrix, flag: Flag){
        this.opcs= ModuleOPC(tripleDataOpcMatrix, flag)
//        isOPCS=true
    }
    var message:String?=null
    var password:String?=null

    fun FromMatrixToOpcs(isAsync: Boolean=true,progressListener:((x:Int)->Unit)? = null){
        if(!opcs.tripleShortMatrix)
            return

        progressListener?.invoke(0)

        if(opcs.flag.isSteganography&&message!=null){
            val mat=opcs.getMatrix(isAsync)
            Steganography.WriteMassageFromByteArrayToMatrix(mat,message?.toByteArray())
            TimeManager.Instance.append("Stega")

        }
        progressListener?.invoke(10)

        directOpc(opcs.flag.isGlobalBase,isAsync,progressListener)
        TimeManager.Instance.append("dirOPC")
        progressListener?.invoke(80)

        if(opcs.flag.isPassword&&password!=null) {
            val box=opcs.getBoxOfOpc(isAsync)
            Encryption.encode(box, password)
            TimeManager.Instance.append("Encr")
        }
        progressListener?.invoke(100)

        assert(opcs.isOpcs)
//        isOPCS=true
    }
    fun FromOpcToMatrix(isAsync: Boolean=true,progressListener: ((x: Int) -> Unit)?=null){
        if(!opcs.isOpcs)
            return

        progressListener?.invoke(0)

        if(opcs.flag.isPassword&&password!=null) {
            val box=opcs.getBoxOfOpc(isAsync)
            Encryption.encode(box, password)
            TimeManager.Instance.append("Encr")
        }
        progressListener?.invoke(10)

        reverseOpc(isAsync,progressListener)
        TimeManager.Instance.append("reOPC ")
        progressListener?.invoke(80)

        if(opcs.flag.isSteganography) {
            val mat=opcs.getMatrix(isAsync)
            message = String(Steganography.ReadMassageFromMatrix(mat).toByteArray())
            TimeManager.Instance.append("Stega")
        }
        progressListener?.invoke(100)

        assert(opcs.tripleShortMatrix)
//        getTripleShortMatrix=true
    }
    var baseSizeW:Int=1
    var baseSizeH:Int=1

    private fun directOpc(isGlobalBase:Boolean,isAsync:Boolean,progressListener: ((x: Int) -> Unit)?=null){
        if(isGlobalBase){
            opcs.directOpcGlobalBase(baseSizeW,baseSizeH)
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


    fun getMatrix(isAsync: Boolean=true): TripleShortMatrix {
        if(!opcs.tripleShortMatrix)
            FromOpcToMatrix(isAsync)

        assert(opcs.tripleShortMatrix)
        return opcs.getMatrix(isAsync)
    }
    fun getBoxOfOpc(isAsync: Boolean=true): TripleDataOpcMatrix {
        if(!opcs.isOpcs)
            FromMatrixToOpcs(isAsync)

        assert(opcs.isOpcs)
        return opcs.getBoxOfOpc(isAsync)
    }
    fun getModuleOPC():ModuleOPC{
        return opcs
    }

}