package ImageCompressionLib.Containers

import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Utils.Functions.Opc.IStegoMessageUtil


class EncryptParameters {
    interface IFactory{
        fun getStegoBlockKey():IStegoMessageUtil
    }
    var password:String?=null
    var message: ByteVector?=null
    var stegoPosition:Int?=null
    var stegoBlockKeygenFactory:(()->IStegoMessageUtil)?=null
}