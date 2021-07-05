package steganography.containers

import org.junit.Test

import org.junit.Assert.*

class ContainerTest {

    @Test
    fun get() {
        val c= Container<Int>(5, 5)
        assertTrue(c[3,3]==null)
        assertTrue(c[6,6]==null)

        c[3,3]=5
        assertTrue(c[3,3]==5)
    }
}