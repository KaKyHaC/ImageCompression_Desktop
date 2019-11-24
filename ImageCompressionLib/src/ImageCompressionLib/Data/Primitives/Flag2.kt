package ImageCompressionLib.Data.Primitives

import ImageCompressionLib.Data.Interfaces.IByteVector
import ImageCompressionLib.Data.Interfaces.ISavable

class Flag2 constructor() : ISavable {
    private val data = mutableMapOf<Parameter, Boolean>()

    constructor(vector: IByteVector) : this() {
        Parameter.values().forEach {
            setChecked(it, vector.getNextBoolean() ?: false)
        }
    }

    fun isChecked(param: Parameter) = data.get(param) ?: false

    fun setChecked(parameter: Parameter, state: Boolean) = data.put(parameter, state)

    fun setTrue(parameter: Parameter) = setChecked(parameter, true)

    fun setFalse(parameter: Parameter) = setChecked(parameter, false)

    override fun appendByteVector(vector: IByteVector) {
        Parameter.values().forEach { vector.append(isChecked(it)) }
    }

    enum class Parameter {
        OneFile, Enlargement, DC, LongCode, GlobalBase, Password, Steganography, Alignment, CompressionUtils, Quantization, Encryption, DCT;

        fun getBinaryValue() = Math.pow(2.0, values().indexOf(this).toDouble())

    }


}