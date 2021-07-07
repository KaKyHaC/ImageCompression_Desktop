package data_model.types

data class Size(val width: Int, val height: Int) {
    constructor(side: Int) : this(side, side)
}
