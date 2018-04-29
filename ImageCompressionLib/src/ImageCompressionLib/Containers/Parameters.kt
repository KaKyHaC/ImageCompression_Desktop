package ImageCompressionLib.Containers

import ImageCompressionLib.Constants.SIZEOFBLOCK

class Parameters {
    val flag:Flag
    val imageSize:Size
    val unitSize:Size
    val sameBaseSize:Size

    constructor(flag: Flag, imageSize: Size, unitSize: Size = Size(SIZEOFBLOCK, SIZEOFBLOCK), sameBaseSize: Size= Size(1,1)) {
        this.flag = flag
        this.imageSize = imageSize
        this.unitSize = unitSize
        this.sameBaseSize = sameBaseSize
    }

    fun toByteVector(vector: ByteVector){
        vector.append(flag.flag)
        vector.append(imageSize.width.toShort())
        vector.append(imageSize.height.toShort())
        vector.append(unitSize.width.toShort())
        vector.append(unitSize.height.toShort())
        if(flag.isChecked(Flag.Parameter.GlobalBase)) {
            vector.append(sameBaseSize.width.toShort())
            vector.append(sameBaseSize.height.toShort())
        }
    }
    companion object {
        @JvmStatic fun fromByteVector(vector: ByteVector): Parameters {
            val flag=Flag(vector.getNextShort())
            val imageSize=Size(vector.getNextShort().toInt(),vector.getNextShort().toInt())
            val unitSize=Size(vector.getNextShort().toInt(),vector.getNextShort().toInt())
            var sameBaseSize=Size(1,1)
            if(flag.isChecked(Flag.Parameter.GlobalBase)){
                sameBaseSize=Size(vector.getNextShort().toInt(),vector.getNextShort().toInt())
            }
            return Parameters(flag, imageSize, unitSize,sameBaseSize)
        }
    }
}