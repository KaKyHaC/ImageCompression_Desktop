package features.enlargement.utils

import data_model.generics.matrix.Matrix
import data_model.types.Size
import kotlin.reflect.full.cast

object EnlargementUtils {

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

    inline fun <reified T : Number> reverse(enlarged: Matrix<T>): Matrix<T> {
        val newSize = Size(enlarged.width * 2, enlarged.height * 2)
        return Matrix.create(newSize) { i, j ->
            enlarged[i / 2, j / 2]
        }
    }
}