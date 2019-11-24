package ImageCompressionLib.Data.Primitives

import ImageCompressionLib.Data.Type.ByteVector

class Flag2 constructor() {
    val data = mutableMapOf<Parameter, Boolean>()

    constructor(vector: ByteVector) : this() {
        for (p in Parameter.values())
            data.put(p, vector.getNextBoolean())
    }

    fun toByteVector(vector: ByteVector) {
        for (p in Parameter.values())
            vector.append(data.get(p)!!)
    }

    fun isChecked(param: Parameter) = data.get(param) ?: false

    fun setChecked(parameter: Parameter, state: Boolean) = data.set(parameter, state)

    fun setTrue(parameter: Parameter) = setChecked(parameter, true)

    fun setFalse(parameter: Parameter) = setChecked(parameter, false)

    enum class Parameter {
        OneFile, Enlargement, DC, LongCode, GlobalBase, Password, Steganography, Alignment, CompressionUtils, Quantization, Encryption, DCT;

        fun getBinaryValue() = Math.pow(2.0, values().indexOf(this).toDouble())

    }


}