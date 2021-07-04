package ImageCompressionLib.containers

import ImageCompressionLib.containers.type.ByteVector
import ImageCompressionLib.utils.functions.opc.IStegoMessageUtil


class EncryptParameters {
    class StegaParameters{
        @FunctionalInterface
        interface IFactory{
            fun getStegoBlockKey():IStegoMessageUtil
        }

        val stegoPosition:Int
        val stegoBlockKeygenFactory:(()->IStegoMessageUtil)


        constructor(stegoPosition: Int, stegoBlockKeygenFactory: () -> IStegoMessageUtil) {
            this.stegoPosition = stegoPosition
            this.stegoBlockKeygenFactory = stegoBlockKeygenFactory
        }

    }
    var password:String?=null
    var steganography:StegaParameters?=null
    var message: ByteVector?=null

}