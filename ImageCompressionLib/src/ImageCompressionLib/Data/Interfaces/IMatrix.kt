package ImageCompressionLib.Data.Interfaces

interface IMatrix<T> {
    operator fun get(i: Int, j: Int): T?
    operator fun set(i: Int, j: Int, value: T)
    fun forEachIndexed(invoke: (i: Int, j: Int, value: T) -> Unit) : IMatrix<T>
    fun <R>mapIndexed(invoke: (i: Int, j: Int, value: T) -> R) : IMatrix<R>
    val width: Int
    val height: Int
}