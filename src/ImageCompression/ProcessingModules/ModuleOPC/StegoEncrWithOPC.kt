package ImageCompression.ProcessingModules.ModuleOPC

import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Containers.TripleShortMatrix
import ImageCompression.Utils.Functions.Encryption
import ImageCompression.Utils.Functions.Steganography
import ImageCompression.Containers.Flag
import ImageCompression.Utils.Objects.TimeManager

class StegoEncrWithOPC :AbsModuleOPC{
    private var opcs: ModuleOPC

    constructor(tripleShortMatrix: TripleShortMatrix,flag:Flag
                ,sameBaseWidth:Int,sameBaseHeight:Int,message:String?,password:String?):
            super(tripleShortMatrix,flag){
        opcs= ModuleOPC(tripleShortMatrix,flag)
//        getTripleShortMatrix=true
        baseSizeH=sameBaseHeight
        baseSizeW=sameBaseWidth
        this.message=message
        this.password=password
    }
    constructor(tripleDataOpcMatrix: TripleDataOpcMatrix, flag: Flag
                ,sameBaseWidth:Int,sameBaseHeight:Int,password:String?) :
            super(tripleDataOpcMatrix,flag){
        this.opcs= ModuleOPC(tripleDataOpcMatrix, flag)
//        isOPCS=true
        baseSizeH=sameBaseHeight
        baseSizeW=sameBaseWidth
        this.message=null
        this.password=password
    }

    var message:String?
    var password:String?

    var baseSizeW:Int
    var baseSizeH:Int

    var onMessageReadedListener:((message:String)->Unit)?=null
    var isAsync=true

    fun FromMatrixToOpcs(isAsync: Boolean=true,progressListener:((x:Int)->Unit)? = null){
        if(!opcs.isTripleShortMatrix)
            return

        progressListener?.invoke(0)

        if(opcs.flag.isChecked(Flag.Parameter.Steganography)&&message!=null){
            val mat=opcs.getMatrix(isAsync)
            Steganography.WriteMassageFromByteArrayToMatrix(mat,message?.toByteArray())
            TimeManager.Instance.append("Stega")

        }
        progressListener?.invoke(10)

        directOpc(opcs.flag.isChecked(Flag.Parameter.GlobalBase),isAsync,progressListener)
        TimeManager.Instance.append("dirOPC")
        progressListener?.invoke(80)

        if(opcs.flag.isChecked(Flag.Parameter.Password)&&password!=null) {
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

        if(opcs.flag.isChecked(Flag.Parameter.Password)&&password!=null) {
            val box=opcs.getBoxOfOpc(isAsync)
            Encryption.encode(box, password)
            TimeManager.Instance.append("Encr")
        }
        progressListener?.invoke(10)

        reverseOpc(isAsync,progressListener)
        TimeManager.Instance.append("reOPC ")
        progressListener?.invoke(80)

        if(opcs.flag.isChecked(Flag.Parameter.Steganography)) {
            val mat=opcs.getMatrix(isAsync)
            message = String(Steganography.ReadMassageFromMatrix(mat).toByteArray())
            if(message!=null)onMessageReadedListener?.invoke(message!!)
            TimeManager.Instance.append("Stega")
        }
        progressListener?.invoke(100)

        assert(opcs.isTripleShortMatrix)
//        getTripleShortMatrix=true
    }


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
        if(!opcs.isTripleShortMatrix)
            FromOpcToMatrix(isAsync)

        assert(opcs.isTripleShortMatrix)
        return opcs.getMatrix(isAsync)
    }
    fun getBoxOfOpc(isAsync: Boolean=true): TripleDataOpcMatrix {
        if(!opcs.isOpcs)
            FromMatrixToOpcs(isAsync)

        assert(opcs.isOpcs)
        return opcs.getBoxOfOpc(isAsync)
    }
    fun getModuleOPC(): ModuleOPC {
        return opcs
    }

    override fun direct(shortMatrix: TripleShortMatrix): TripleDataOpcMatrix {
        if(!opcs.isOpcs)
            FromMatrixToOpcs(isAsync)

        assert(opcs.isOpcs)
        return opcs.getTripleDataOpcMatrix()
    }

    override fun reverce(dataOpcMatrix: TripleDataOpcMatrix): TripleShortMatrix {
        if(!opcs.isTripleShortMatrix)
            FromOpcToMatrix(isAsync)

        assert(opcs.isTripleShortMatrix)
        return opcs.getTripleShortMatrix()
    }
}