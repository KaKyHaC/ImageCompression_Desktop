package features.dct.utils

import features.dct.table.CosineTable
import features.dct.table.CosineTableExperimental

/**
 * Created by Димка on 07.08.2016.
 */
object CosineTableFactory {

    private val map = HashMap<Int, CosineTable>()
    private val mapExperimental = HashMap<Int, CosineTableExperimental>()

    fun getTable(sizeOfBlock: Int = 8) = map[sizeOfBlock] ?: let {
        val cosineTableUtils = CosineTable(sizeOfBlock)
        map[sizeOfBlock] = cosineTableUtils
        cosineTableUtils
    }

    fun getExperimentalTable(sizeOfBlock: Int = 8) = mapExperimental[sizeOfBlock] ?: let {
        val cosineTableUtils = CosineTableExperimental(sizeOfBlock)
        mapExperimental[sizeOfBlock] = cosineTableUtils
        cosineTableUtils
    }
}