package features.dct.utils

import data_model.generics.matrix.Matrix
import data_model.types.Size

/**
 * Created by Димка on 07.08.2016.
 */
object CosineTableFactory {

    private val map = HashMap<Int, CosineTableUtils>()

    fun getTable(sizeOfBlock: Int = 8) = map[sizeOfBlock] ?: let {
        val cosineTableUtils = CosineTableUtils(sizeOfBlock)
        map[sizeOfBlock] = cosineTableUtils
        cosineTableUtils
    }
}