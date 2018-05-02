package ImageCompressionLib.Containers

import org.junit.Test

import org.junit.Assert.*
import java.util.*

class ByteVectorTest {
    val rand = Random()
    @Test
    fun testTotal() {
        val vector = ByteVector()

        for (q in 0 until 100) {
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

    @Test
    fun testTotal2() {
        val vector = ByteVector()
        val loops = 0 until 100
        for (q in loops) {
            for (w in loops) {
                val i = rand.nextInt().toByte()
                vector.append(i)
                val ri = vector.getNext()
                assertEquals(i, ri)
            }
            for (w in loops) {
                val l = rand.nextLong()
                vector.append(l)
                val rl = vector.getNextLong()
                assertEquals(l, rl)
            }

            for (w in loops) {
                val s = rand.nextInt().toShort()
                val b = rand.nextBoolean()
                vector.append(b)
                vector.append(s)
                val rb = vector.getNextBoolean()
                val rs = vector.getNextShort()
                assertEquals(s, rs)
                assertEquals(b, rb)
            }
        }
    }

    @Test
    fun testBoolean() {
        val vector = ByteVector()
        val size=100
        val tmp=BooleanArray(size){rand.nextBoolean() }
        for(q in 0 until size) {
            vector.append(tmp[q])
        }
        for(q in 0 until size){
            assertEquals(vector.getNextBoolean(),tmp[q])
        }
    }

    val message="21fdsvd dwfwe"
    @Test
    fun StringIOtest(){
        val bytes=message.toByteArray()
        assertEquals(message, String(bytes))

        val vector=ByteVector()
        for(b in bytes)
            vector.append(b)

        val vector2=ByteVector()
        while (vector.hasNextBit())
            vector2.append(vector.getNextBoolean())

        assertEquals(vector,vector2)

        val resB=vector2.toByteArray()
//        assertEquals(bytes,resB)

        val sR= String(resB)
        assertEquals(message,sR)
    }
}