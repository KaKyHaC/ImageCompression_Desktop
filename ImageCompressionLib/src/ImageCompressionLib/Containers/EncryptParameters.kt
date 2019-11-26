package ImageCompressionLib.Containers

import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Utils.Functions.Opc.IStegoMessageUtil


class EncryptParameters {
    class StegaParameters {
        interface IFactory {
            fun getStegoBlockKey(): IStegoMessageUtil
        }

        val stegoPosition: Int
        val stegoBlockKeygenFactory: (() -> IStegoMessageUtil)


        constructor(stegoPosition: Int, stegoBlockKeygenFactory: () -> IStegoMessageUtil) {
            this.stegoPosition = stegoPosition
            this.stegoBlockKeygenFactory = stegoBlockKeygenFactory
        }

    }

    var password: String? = null
    var steganography: StegaParameters? = null
    var message: ByteVector? = null

}