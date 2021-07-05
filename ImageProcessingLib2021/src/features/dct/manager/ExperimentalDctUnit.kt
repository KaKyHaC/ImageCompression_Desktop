package features.dct.manager

import data_model.types.Size

class ExperimentalDctUnit(
        val imageSize: Size = Size(100, 100),
        val bs: Int = 8,
        val bsf: Int = 8,
        val dct: Array<FloatArray>,
        val dctT: Array<FloatArray>,
        var mas0Y: Array<FloatArray>
) {
    val xs = imageSize.width
    val ys = imageSize.height
    val fxs = rhc(xs, bs)
    val fys = rhc(ys, bs)

    /**
     * ДКП яркостной составляющей.
     */
    fun YtoDCT() {
        val mas1Y = Array(fxs / bsf) { Array(fys / bsf) { Array(bsf) { FloatArray(bsf) } } }
        val mas = Array(fxs / bsf) { Array(fys / bsf) { Array(bsf) { FloatArray(bsf) } } }
        for (x in 0 until fxs / bsf) {
            for (y in 0 until fys / bsf) {
                mas[x][y] = getMasPrt(mas0Y, x, y, bsf)
            }
        }
        for (x in 0 until fxs / bsf) {
            for (y in 0 until fys / bsf) {
                mas1Y[x][y] = DCTtoF(mas[x][y])
            }
        }
    }

    /**
     * "Обратное ДКП."
     */
    fun DCTtoY(mas1Y: Array<Array<Array<FloatArray>>>) {
        mas0Y = Array(fxs) { FloatArray(fys) }
        for (x in 0 until fxs / bsf) {
            for (y in 0 until fys / bsf) {
                val masPrt: Array<FloatArray> = DCTtoS(mas1Y.get(x).get(y))
                for (xx in 0 until bsf) {
                    for (yy in 0 until bsf) {
                        mas0Y[x * bsf + xx][y * bsf + yy] = masPrt[xx][yy]
                    }
                }
            }
        }
    }

    fun rhc(s: Int, bs: Int): Int {
        return if (s % bs == 0) {
            s
        } else {
            ((s / bs) + 1) * bs
        }
    }

    fun getMasPrt(mas: Array<FloatArray>, myX: Int, myY: Int, myBs: Int): Array<FloatArray> {
        val ret = Array(myBs) { FloatArray(myBs) }
        var x = myX * myBs
        var xx = 0
        while (xx < myBs) {
            var y = myY * myBs
            var yy = 0
            while (yy < myBs) {
                ret[xx][yy] = mas[x][y]
                y++
                yy++
            }
            x++
            xx++
        }
        return ret
    }

    fun DCTtoF(`in`: Array<FloatArray>): Array<FloatArray> {
        for (x in `in`.indices) {
            for (y in 0 until `in`[x].size) {
                `in`[x][y] = `in`[x][y] - 128
            }
        }
        var ret = Array(`in`.size) { FloatArray(`in`[0].size) }
        val in_dct: Array<FloatArray> = mulMat(dct, `in`)
        val in_dct_dctT: Array<FloatArray> = mulMat(in_dct, dctT)
        ret = in_dct_dctT
        return ret
    }

    fun DCTtoS(`in`: Array<FloatArray>): Array<FloatArray> {
        var ret = Array(`in`.size) { FloatArray(`in`[0].size) }
        val in_dctT = mulMat(dctT, `in`)
        val in_dctT_dct = mulMat(in_dctT, dct)
        ret = in_dctT_dct
        for (x in `in`.indices) {
            for (y in 0 until `in`[x].size) {
                ret[x][y] = ret[x][y] + 128
            }
        }
        return ret
    }

    fun mulMat(mat0: Array<FloatArray>, mat1: Array<FloatArray>): Array<FloatArray> {
        val ret = Array(mat0.size) { FloatArray(mat1.size) }
        val m = mat0.size
        val n: Int = mat1[0].size
        val o = mat1.size
        for (i in 0 until m) {
            for (j in 0 until n) {
                for (k in 0 until o) {
                    ret[i][j] += mat0[i][k] * mat1[k][j]
                }
            }
        }
        return ret
    }
}
