package ImageCompressionLib.Steganography.Containers

import java.math.BigInteger
import java.util.*

data class OpcContainer<T> (var code:BigInteger,var base:Array<T>){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OpcContainer<*>

        if (code != other.code) return false
        if (!Arrays.equals(base, other.base)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + Arrays.hashCode(base)
        return result
    }

    override fun toString(): String {
        return "OpcContainer(code=$code, base=${Arrays.toString(base)})"
    }

}