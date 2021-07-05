package desktop

import org.junit.Test
import java.util.*
import kotlin.test.assertTrue

class PerformanceTests {
    @Test
    fun testMethodCallSpeed(){
        val loop=100000
        val t1=Calendar.getInstance().timeInMillis
        for (i in 0 .. loop) {
            localMethod()
        }
        val t2=Calendar.getInstance().timeInMillis
        for (i in 0 .. loop) {
            staticMethod()
        }
        val t3=Calendar.getInstance().timeInMillis
        val local = t2-t1
        val satic = t3-t2
        println("local ${local}")
        println("static ${satic}")
        assertTrue { local>satic }
    }
    fun localMethod(){}
    companion object {
        @JvmStatic
        fun staticMethod(){}
    }
}