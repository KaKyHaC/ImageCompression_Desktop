package ImageCompressionLib.ProcessingModules.ModuleOPC

import ImageCompressionLib.Containers.TripleDataOpcMatrixOld
import ImageCompressionLib.Containers.TripleShortMatrixOld
import ImageCompressionLib.Utils.Functions.Encryption
import ImageCompressionLib.Utils.Functions.Steganography
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Utils.Objects.TimeManager

class StegoEncrWithOpcOld : ModuleOpcOld {
    val DEFAULT_PASS=" "
//    private var super: ModuleOpcOld

    constructor(tripleShortMatrixOld: TripleShortMatrixOld, flag: Flag
                , sameBaseWidth:Int, sameBaseHeight:Int, message:String?, password:String?, isAsync: Boolean):
            super(tripleShortMatrixOld,flag,isAsync){
//        super= ModuleOpcOld(tripleShortMatrixOld,flag)
//        getTripleShortMatrix=true
        baseSizeH=sameBaseHeight
        baseSizeW=sameBaseWidth
        this.message=message
        this.password=password
    }
    constructor(tripleDataOpcMatrixOld: TripleDataOpcMatrixOld, flag: Flag
                , sameBaseWidth:Int=1, sameBaseHeight:Int=1, password:String?, isAsync: Boolean) :
            super(tripleDataOpcMatrixOld,flag,isAsync){
//        this.super= ModuleOpcOld(tripleDataOpcMatrixOld, flag)
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
//    var isAsync=true
    
//    val flag:Flag

    fun FromMatrixToOpcs(isAsync: Boolean,progressListener:((x:Int)->Unit)? = null){
//        if(super.state==AbsModuleOPC.State.OPC)
//            return

        progressListener?.invoke(0)

        if(super.flag.isChecked(Flag.Parameter.Steganography)&&message!=null){
            val mat=super.getMatrix(isAsync)
            Steganography.WriteMassageFromByteArrayToMatrix(mat,message?.toByteArray())
            TimeManager.Instance.append("Stega")

        }
        progressListener?.invoke(10)

        directOpc(super.flag.isChecked(Flag.Parameter.GlobalBase),isAsync,progressListener)
        TimeManager.Instance.append("dirOPC")
        progressListener?.invoke(80)

        if(super.flag.isChecked(Flag.Parameter.Password)&&password!=null) {
            val box=super.getBoxOfOpc(isAsync)
            Encryption.encode(box, password?:DEFAULT_PASS)
            TimeManager.Instance.append("Encr")
        }
        progressListener?.invoke(100)

//        assert(super.isOpcs)
//        isOPCS=true
    }
    fun FromOpcToMatrix(isAsync: Boolean,progressListener: ((x: Int) -> Unit)?=null){
//        if(!super.isOpcs)
//            return

        progressListener?.invoke(0)

        if(super.flag.isChecked(Flag.Parameter.Password)&&password!=null) {
            val box=super.getBoxOfOpc(isAsync)
            Encryption.encode(box, password?:DEFAULT_PASS)
            TimeManager.Instance.append("Encr")
        }
        progressListener?.invoke(10)

        reverseOpc(isAsync,progressListener)
        TimeManager.Instance.append("reOPC ")
        progressListener?.invoke(80)

        if(super.flag.isChecked(Flag.Parameter.Steganography)) {
            val mat=super.getMatrix(isAsync)
            message = String(Steganography.ReadMassageFromMatrix(mat).toByteArray())
            if(message!=null)onMessageReadedListener?.invoke(message!!)
            TimeManager.Instance.append("Stega")
        }
        progressListener?.invoke(100)

//        assert(super.isTripleShortMatrix)
//        getTripleShortMatrix=true
    }


    private fun directOpc(isGlobalBase:Boolean,isAsync:Boolean,progressListener: ((x: Int) -> Unit)?=null){
        if(isGlobalBase){
            super.directOpcGlobalBase(baseSizeW,baseSizeH)
        }else if(isAsync){
            super.directOPCMultiThreads()
        }else{
            super.directOPC();
        }

    }
    private fun reverseOpc(isAsync:Boolean,progressListener: ((x: Int) -> Unit)?=null){
        if(isAsync){
            super.reverseOPCMultiThreads()
        }else{
            super.reverseOPC();
        }
    }


/*    fun getMatrix(isAsync: Boolean=true): TripleShortMatrixOld {
        if(!super.isTripleShortMatrix)
            FromOpcToMatrix(isAsync)

        assert(super.isTripleShortMatrix)
        return super.getMatrix(isAsync)
    }
    fun getBoxOfOpc(isAsync: Boolean=true): TripleDataOpcMatrixOld {
        if(!super.isOpcs)
            FromMatrixToOpcs(isAsync)

        assert(super.isOpcs)
        return super.getBoxOfOpc(isAsync)
    }*/

    override fun direct(shortMatrixOld: TripleShortMatrixOld): TripleDataOpcMatrixOld {
//        if(!super.isOpcs)
        FromMatrixToOpcs(isAsyn)

//        assert(super.isOpcs)
        return super.tripleDataOpcOld//TODO
    }
    override fun reverce(dataOpcMatrixOld: TripleDataOpcMatrixOld): TripleShortMatrixOld {
//        if(!super.isTripleShortMatrix)
        FromOpcToMatrix(isAsyn)

//        assert(super.isTripleShortMatrix)
        return super.tripleShortOld//TODO
    }
}