package features.dct.utils

/**
 * Created by Димка on 07.08.2016.
 */
object CosineTableFactory {

    private val map = HashMap<Int, CosineTable>()
    private val mapExperimental = HashMap<Int, ExperimentalCosineTable>()

    fun getTable(sizeOfBlock: Int = 8) = map[sizeOfBlock] ?: let {
        val cosineTableUtils = CosineTable(sizeOfBlock)
        map[sizeOfBlock] = cosineTableUtils
        cosineTableUtils
    }

    fun getExperimentalTable(sizeOfBlock: Int = 8) = mapExperimental[sizeOfBlock] ?: let {
        val cosineTableUtils = ExperimentalCosineTable(sizeOfBlock)
        mapExperimental[sizeOfBlock] = cosineTableUtils
        cosineTableUtils
    }
}