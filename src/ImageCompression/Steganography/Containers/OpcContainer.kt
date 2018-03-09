package ImageCompression.Steganography.Containers

import java.math.BigInteger
import java.util.*

data class OpcContainer<T> (val code:BigInteger,val base:Array<T>){
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
}