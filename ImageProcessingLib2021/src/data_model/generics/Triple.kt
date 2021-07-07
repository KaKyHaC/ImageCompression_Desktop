package data_model.generics

data class Triple<T>(
        val first: T,
        val second: T,
        val third: T
) {

    inline fun <reified R> map(mapper: (T) -> R) = Triple(
            mapper(first),
            mapper(second),
            mapper(third)
    )
}