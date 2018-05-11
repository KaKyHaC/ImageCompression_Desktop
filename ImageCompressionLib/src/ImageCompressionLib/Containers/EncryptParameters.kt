package ImageCompressionLib.Containers

import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Utils.Functions.Opc.IStegoMessageUtil


class EncryptParameters {
    var password:String?=null
    var message: ByteVector?=null
    var stegoPosition:Int?=null
    var stegoBlockKey:IStegoMessageUtil?=null
}