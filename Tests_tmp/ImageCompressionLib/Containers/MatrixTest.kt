package ImageCompressionLib.Containers

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class MatrixTest {
    val size=2
    val data44:Array<Array<Int>>
    val data22:Array<Array<Int>>
    lateinit var matrix:Matrix<Int>
    lateinit var matrix22:Matrix<Int>

    constructor(){
        data22= Array(size ){i->Array(size){j->(i+1)*j}}
        data44= Array(size*2 ){i->Array(size*2){j->(i+1)*j} }
    }

    @Before
    fun setUp() {
        matrix= Matrix<Int>(data44)
        matrix22= Matrix<Int>(data22)
    }
    @Test
    fun testGet(){
        matrix.forEach(){i, j, value ->
            assertEquals(data44[i][j],value)
            return@forEach null
        }
    }
    @Test
    fun toStringTest(){
        println(matrix.toString())
    }
    @Test
    fun rect(){
        val tmp=matrix.rectSave(0,0,2,2)
        val b=matrix22.equals(tmp)
        assertEquals(matrix22,tmp)
    }
    @Test
    fun split(){
        val tmp=matrix.split(2,2)
        assertEquals(matrix22,tmp[0,0])
    }
}