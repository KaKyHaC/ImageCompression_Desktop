package features.dct.utils

/**
 * Created by Димка on 07.08.2016.
 */
object CosineTableFactory {

    private val map = HashMap<Int, CosineTable>()

    fun getTable(sizeOfBlock: Int = 8) = map[sizeOfBlock] ?: let {
        val cosineTableUtils = CosineTable(sizeOfBlock)
        map[sizeOfBlock] = cosineTableUtils
        cosineTableUtils
    }
}