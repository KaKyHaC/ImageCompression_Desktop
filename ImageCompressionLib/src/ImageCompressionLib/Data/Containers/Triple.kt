package ImageCompressionLib.Data.Containers

data class Triple<T>(
    val first: T,
    val second: T,
    val third: T
) {
    fun <R> mapIndexed(mapper: ((index: Int, value: T) -> R)) = Triple(
        mapper(0, first),
        mapper(1, second),
        mapper(2, third)
    )
}