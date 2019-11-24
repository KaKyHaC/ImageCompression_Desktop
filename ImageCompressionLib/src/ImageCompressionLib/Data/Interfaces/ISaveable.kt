package ImageCompressionLib.Data.Interfaces

interface ISaveable {
    fun toByteVector() : IByteVector
    fun appendByteVector(vector: IByteVector)
}