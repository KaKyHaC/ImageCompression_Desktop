package ImageCompressionLib.Utils.Objects


import ImageCompressionLib.Data.Generics.Triple
import ImageCompressionLib.Data.Interfaces.IMatrix
import ImageCompressionLib.Data.Primitives.DataOpc
import kotlin.experimental.xor

/**
 * Created by Димка on 30.10.2016.
 */
object EncryptionUtils {

    fun encode(tripleOpcMatrix: Triple<IMatrix<DataOpc>>, key: String): Triple<IMatrix<DataOpc>> {
        val key = keyToArray(key)
        return tripleOpcMatrix.mapIndexed { i, value -> EncryptionUtils.encode(value, key) }
    }

    private fun encode(opcMatrix: IMatrix<DataOpc>, keyArray: ShortArray) = opcMatrix.mapIndexed { i, j, value ->
        EncryptionUtils.encode(value, keyArray)
    }

    private fun encode(dataOpcOld: DataOpc, keyArray: ShortArray) = dataOpcOld.builder().apply {
        base?.let { base = EncryptionUtils.encode(it, keyArray) }
    }.build()


    // метод для шифровки текста с помощью XOR
    private fun encode(secret: ShortArray, keyArray: ShortArray): ShortArray {
        var sS: Short
        var sK: Short
        var sR: Short
        val result = ShortArray(secret.size)
        for (i in secret.indices) {
            sS = secret[i]
            sK = keyArray[i % keyArray.size]
            sR = (sS xor sK).toShort()
            result[i] = sR
        }
        return result
    }

    private fun keyToArray(key: String): ShortArray {
        if (key.length == 0)
            throw Exception("Password length == 0")
        val res = ShortArray(key.length)
        var sK: Short
        for (i in 0 until key.length) {
            sK = key[i % key.length].toShort()
            sK = (sK.toInt() shl (8 - sK % 8)).toShort()
            res[i] = sK
        }
        return res
    }
}
