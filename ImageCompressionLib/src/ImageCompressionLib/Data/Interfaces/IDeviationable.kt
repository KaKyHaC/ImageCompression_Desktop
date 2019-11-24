package ImageCompressionLib.Data.Interfaces

interface IDeviationable<T> {
    fun calculateDeviation(other: T) : Double
}