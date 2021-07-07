package features.quantization.utils

/**
 * by Valera
 */
@UseExperimental
class QuantizationTableExperimental(
        val bsf: Int = 8,
        val stdQK: Float = 8f
) {

    fun genQMat1(k: Int = 1): Array<FloatArray>? {
        val ret = Array(bsf) { FloatArray(bsf) }
        for (x in 0 until bsf) {
            for (y in 0 until bsf) {
                if (x + y <= 2 * bsf / (k / 8.toFloat())) {
                    ret[x][y] = stdQK
                } else {
                    ret[x][y] = 0f
                }
            }
        }
        return ret
    }

    fun genQMat2(kx: Float = 1f, ky: Float= 1f): Array<FloatArray>? {
        val ret = Array(bsf) { FloatArray(bsf) }
        for (x in 0 until bsf) {
            for (y in 0 until bsf) {
                if (x > bsf * kx || y > bsf * ky) {
                    ret[x][y] = 0f
                } else {
                    ret[x][y] = stdQK
                }
            }
        }
        return ret
    }
}