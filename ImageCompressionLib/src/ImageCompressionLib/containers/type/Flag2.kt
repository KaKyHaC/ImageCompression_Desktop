package ImageCompressionLib.containers.type

class Flag2 {
    //TODO remove ImageCompressionLib.steganography
    enum class Parameter  constructor(internal val value: Int) {
        // empty value 8
        OneFile(16),
        Enlargement(32), DC(64), LongCode(128), GlobalBase(256),
        Password(512), Steganography(1024), Alignment(2048),
        CompressionUtils(4096), Quantization(1), Encryption(4), DCT(2)
    }
    val data:MutableMap<Parameter,Boolean>

    constructor(){
        data=HashMap<Parameter,Boolean>()
        for(p in Parameter.values())
            data.put(p,false)
    }
    constructor(vector: ByteVector){
        data=HashMap<Parameter,Boolean>()
        for(p in Parameter.values())
            data.put(p,vector.getNextBoolean())
    }
    fun toByteVector(vector: ByteVector){
        for(p in Parameter.values())
            vector.append(data.get(p)!!)
    }

    fun isChecked(param: Parameter): Boolean {
        return data.get(param)!!
    }
    fun setChecked(parameter: Parameter, state: Boolean) {
        data.set(parameter,state)
    }
    fun setTrue(parameter: Parameter) {
        setChecked(parameter, true)
    }
    fun setFalse(parameter: Parameter) {
        setChecked(parameter, false)
    }


}