package features.dct.utils

@UseExperimental
class CosineExperimentalTable(
        val bsf: Int = 8
) {
    /**
     * "Генерирование матрицы ДКП."
     */
    val dct = Array(bsf) { FloatArray(bsf) }
    val dctT = Array(bsf) { FloatArray(bsf) }

    fun buildMatDCT() {
        for (x in 0 until bsf) {
            for (y in 0 until bsf) {
                val cc = Math.sqrt((2.0 / bsf)) * Math.cos((2.0 * x + 1) * y * Math.PI / (2 * bsf))
                dct[y][x] = cc.toFloat()
            }
        }
        for (y in 0 until bsf) {
            val cc = 1 / Math.sqrt(bsf.toDouble())
            dct[0][y] = cc.toFloat()
        }
        val dctTC = trans(dct)
        for (x in 0 until bsf) {
            for (y in 0 until bsf) {
                dctT[x][y] = dctTC[x][y]
            }
        }
    }

    fun trans(a: Array<FloatArray>): Array<FloatArray> {
        val ret = Array(a.size) { FloatArray(a[0].size) }
        for (i in a.indices) {
            for (j in i until a[i].size) {
                val temp = a[i][j]
                ret[i][j] = a[j][i]
                ret[j][i] = temp
            }
        }
        return ret
    }
}