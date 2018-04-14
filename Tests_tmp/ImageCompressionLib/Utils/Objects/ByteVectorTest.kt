package ImageCompressionLib.Utils.Objects

import ImageCompressionLib.Containers.ByteVector
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class ByteVectorTest {
    var bv= ByteVector()
    val rand = Random()
    val size=100

    @Before
    fun randomize(){
        for(i in 0..size)
            bv.append(rand.nextInt().toByte())
    }
    @Test
    fun TestEqualSame(){
        assertEquals(bv,bv)
    }
    @Test
    fun TestCopy(){
        val cpy=bv.copy()
        assertEquals(bv,cpy)
    }
    @Test
    fun TestNotEqual(){
        val bv1= ByteVector(43)
        assertNotEquals(bv,bv1)

        val bv2= ByteVector(33)
        assertNotEquals(bv,bv1)
    }
    @Test
    fun TestLong(){
        val bv1= ByteVector()
        val l1=rand.nextLong()
        val l2=rand.nextLong()
        bv1.append(l1)
        bv1.append(l2)
        val r1=bv1.getNextLong()
        val r2=bv1.getNextLong()

        assertEquals(l1,r1)
        assertEquals(l2,r2)
    }
    @Test
    fun TestByte(){
        val bv1= ByteVector()
        val l1=rand.nextLong().toByte()
        val l2=rand.nextLong().toByte()
        bv1.append(l1)
        bv1.append(l2)
        val r1=bv1.getNext()
        val r2=bv1.getNext()

        assertEquals(l1,r1)
        assertEquals(l2,r2)
    }
    @Test
    fun TestShort(){
        val bv1= ByteVector()
        val l1=rand.nextLong().toShort()
        val l2=rand.nextLong().toShort()
        bv1.append(l1)
        bv1.append(l2)
        val r1=bv1.getNextShort()
        val r2=bv1.getNextShort()

        assertEquals(l1,r1)
        assertEquals(l2,r2)
    }
    @Test
    fun TestSpeedAppend(){
        val loop=1000000

        val t1=Date().time
        for(i in 0..loop) {
            var r = 18149322522L
            r = (r shr (ByteVector.BITS_IN_BYTE))
            var g = (r).toByte()
        }
        val t2=Date().time

        val t3=Date().time
        for(i in 0..loop) {
            var r = 18149322522L
            var g=((r shr (ByteVector.BITS_IN_BYTE*3)).toByte())
            g=((r shr (ByteVector.BITS_IN_BYTE*2)).toByte())
            g=((r shr ByteVector.BITS_IN_BYTE).toByte())
            g=g
        }
        val t4=Date().time

        System.out.println("first ${t2-t1}, second ${t4-t3}")
    }
    @Test
    fun TestMixVector(){
        val l=12412594833423L
        val i=23423521223
        val s:Short=i.toShort()
        val b:Byte=0b010101

        var bv= ByteVector(2)
        bv.append(l)
        bv.append(b)
        bv.append(l)
        bv.append(s)
        bv.append(b)

        val bv1=bv.copy()
        val l1=bv1.getNextLong()
        val b1=bv1.getNext()
        val l2=bv1.getNextLong()
        val s1=bv1.getNextShort()
        val b2=bv1.getNext()

        assertEquals(l,l1)
        assertEquals(l,l2)
        assertEquals(b,b1)
        assertEquals(b,b2)
        assertEquals(s,s1)
    }

}