package ImageCompressionLib.Containers

import ImageCompressionLib.Containers.Type.ByteVector


class EncryptParameters {
    var password:String?=null
    var message: ByteVector?=null
    var stegoPosition:Int?=null
    var stegoBlockKey:String?=null
}