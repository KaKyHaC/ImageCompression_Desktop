package data_model.containers.matrix

import data_model.containers.types.Size

class ShortMatrix(matrix: Array<Array<Short>>) : Matrix<Short>(matrix) {

    constructor(
            size: Size, init: (Int, Int) -> Short = { _, _ -> 0 }
    ) : this(Array(size.width) { i -> Array(size.height) { j -> init(i, j) } })
}