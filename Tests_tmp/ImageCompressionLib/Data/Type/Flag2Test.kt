package ImageCompressionLib.Data.Type

import ImageCompressionLib.Utils.Convertors.TimeManager
import org.junit.Before

import org.junit.Test

class Flag2Test {

    @Before
    fun setUp() {
    }

    @Test
    fun speedTest(){
        val t=TimeManager.Instance
        val f=Flag()
        val f2=Flag2()
        val loop=1000000
        t.startNewTrack("speed test")
        for(i in 0 until loop)
            for(p in Flag.Parameter.values())
                f.setTrue(p)
        t.append("flag1")
        for(i in 0 until loop)
            for(p in Flag2.Parameter.values())
                f2.setTrue(p)
        t.append("flag2")
        print(t.getInfoInSec())
    }
}