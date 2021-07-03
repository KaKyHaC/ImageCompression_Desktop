package ImageCompressionLib.Containers.Type

data class Size(val width: Int, val height: Int){
    fun toByteVector(vector: ByteVector){
        vector.append(width.toShort())
        vector.append(height.toShort())
    }
    companion object {
        @JvmStatic fun valueOf(vector: ByteVector):Size{
            return Size(vector.getNextShort().toInt(),vector.getNextShort().toInt())
        }
    }
}
