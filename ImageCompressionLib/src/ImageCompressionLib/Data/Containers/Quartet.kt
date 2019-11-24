package ImageCompressionLib.Data.Containers

data class Quartet<T>(
    val first: T,
    val second: T,
    val third: T,
    val fourth: T
) {
    fun <R> mapIndexed(mapper: ((index: Int, value: T) -> R)) = Quartet(
        mapper(0, first),
        mapper(1, second),
        mapper(2, third),
        mapper(3, fourth)
    )

    fun values() = arrayListOf(first, second, third, fourth)
}