package ImageCompressionLib.Data.Containers

data class Quartet<T>(
    val first: T,
    val second: T,
    val third: T,
    val fourth: T
) {
    fun values() = arrayListOf(first, second, third, fourth)
}