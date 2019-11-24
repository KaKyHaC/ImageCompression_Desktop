package ImageCompressionLib.Data.Interfaces

interface ITriple<T> {
    val first: T
    val second: T
    val third: T

    fun forEachIndexed(invoke: (i: Int, value: T) -> Unit): ITriple<T>
    fun <R> mapIndexed(invoke: (i: Int, value: T) -> R): ITriple<R>
}