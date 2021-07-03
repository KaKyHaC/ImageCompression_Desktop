package data_model.containers.types

import utils.isTrue
import java.util.*

data class Flag(
        private val data: MutableMap<Parameter, Boolean> = EnumMap(Parameter::class.java)
) {

    enum class Parameter {
        OneFile,
        Enlargement, DC, LongCode, GlobalBase,
        Password, Steganography, Alignment,
        CompressionUtils, Quantization, Encryption, DCT;

        fun get2Pow() = Math.pow(2.0, values().indexOf(this).toDouble())
    }

    fun isChecked(param: Parameter): Boolean {
        return data[param].isTrue()
    }

    fun setChecked(parameter: Parameter, state: Boolean) {
        data[parameter] = state
    }

    fun setTrue(parameter: Parameter) {
        setChecked(parameter, true)
    }

    fun setFalse(parameter: Parameter) {
        setChecked(parameter, false)
    }

}