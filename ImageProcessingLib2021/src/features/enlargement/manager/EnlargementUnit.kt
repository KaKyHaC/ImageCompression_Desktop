package features.enlargement.manager

import data_model.generics.matrix.Matrix
import data_model.types.Size
import kotlin.reflect.full.cast

class EnlargementUnit {

    inline fun <reified T : Number> direct(origin: Matrix<T>): Matrix<T> {
        val newSize = Size(origin.width / 2, origin.height / 2)
        return Matrix.create(newSize) { i, j ->
            val newValue = (origin[i * 2, j * 2].toInt() +
                    origin[i * 2, j * 2 + 1].toInt() +
                    origin[i * 2 + 1, j * 2].toInt() +
                    origin[i * 2 + 1, j * 2 + 1].toInt()) / 4
            T::class.cast(newValue)
        }
    }
}