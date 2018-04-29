package ImageCompressionLib.Containers

import org.junit.Test

import org.junit.Assert.*
import java.util.*

class ByteVectorTest {
    val rand=Random()
    @Test
    fun testTotal() {
        val vector=ByteVector()

        for(q in 0 until 10) {
            val i = rand.nextInt().toByte()
            val l = rand.nextLong()
            val s = rand.nextInt().toShort()
            val b = rand.nextBoolean()


            vector.append(i)
            vector.append(l)
            vector.append(b)
            vector.append(s)

            val ri = vector.getNext()
            val rl = vector.getNextLong()
            val rb = vector.getNextBoolean()
            val rs = vector.getNextShort()

            assertEquals(i, ri)
            assertEquals(l, rl)
            assertEquals(s, rs)
            assertEquals(b, rb)
        }
    }
}