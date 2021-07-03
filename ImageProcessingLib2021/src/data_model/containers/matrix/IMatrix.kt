package data_model.containers.matrix

interface IMatrix<T> {
    operator fun get(i: Int, j: Int): T
    operator fun set(i: Int, j: Int, value: T)
    val width: Int
    val height: Int
}