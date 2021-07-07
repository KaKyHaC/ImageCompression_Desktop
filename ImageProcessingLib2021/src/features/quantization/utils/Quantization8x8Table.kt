package features.quantization.utils

import data_model.generics.matrix.Matrix
import data_model.types.Size
import features.quantization.utils.Quantization8x8Table

/**
 * Created by Димка on 08.08.2016.
 */
object Quantization8x8Table {
    private val luminosity = Matrix(arrayOf(
            shortArrayOf(1, 2, 3, 4, 5, 6, 7, 8).toTypedArray(),
            shortArrayOf(2, 2, 3, 4, 5, 8, 10, 15).toTypedArray(),
            shortArrayOf(3, 4, 5, 6, 7, 9, 16, 20).toTypedArray(),
            shortArrayOf(4, 6, 8, 19, 21, 27, 28, 36).toTypedArray(),
            shortArrayOf(5, 12, 27, 36, 48, 59, 69, 77).toTypedArray(),
            shortArrayOf(7, 35, 35, 44, 51, 69, 79, 82).toTypedArray(),
            shortArrayOf(9, 44, 47, 48, 49, 59, 69, 79).toTypedArray(),
            shortArrayOf(12, 49, 55, 68, 79, 80, 99, 99).toTypedArray()
    ), Short::class)

    /*        luminosity = new short[][]{
                    {1,1,1,2,3,40,51,61},
                    {1,1,2,19,26,58,60,55},
                    {1,2,16,24,40,57,69,56},
                    {2,17,22,29,51,87,80,62},
                    {18,22,37,56,68,99,99,77},
                    {24,35,55,64,81,99,99,92},
                    {49,64,78,87,99,99,99,99},
                    {72,92,95,98,99,90,99,99}};*/

    private val chromaticity = Matrix(arrayOf(
            shortArrayOf(1, 1, 2, 4, 9, 99, 99, 99).toTypedArray(),
            shortArrayOf(1, 2, 2, 6, 99, 99, 99, 99).toTypedArray(),
            shortArrayOf(2, 2, 56, 99, 99, 99, 99, 99).toTypedArray(),
            shortArrayOf(4, 66, 99, 99, 99, 99, 99, 99).toTypedArray(),
            shortArrayOf(99, 99, 99, 99, 99, 99, 99, 99).toTypedArray(),
            shortArrayOf(99, 99, 99, 99, 99, 99, 99, 99).toTypedArray(),
            shortArrayOf(99, 99, 99, 99, 99, 99, 99, 99).toTypedArray(),
            shortArrayOf(99, 99, 99, 99, 99, 99, 99, 99).toTypedArray()
    ), Short::class)

    fun getLuminosity(x: Int, y: Int) = luminosity[x,y]

    fun getChromaticity(x: Int, y: Int) = chromaticity[x,y]
}