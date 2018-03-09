package ImageCompression.Steganography

import org.junit.Test

import org.junit.Assert.*

class UnitContainerFactoryTest {

    @Test
    fun testReflection() {
        mainTest(8,8,8,8,0)
    }
    @Test
    fun testNormal() {
        mainTest(1920,1080,8,8,0)


    }
    @Test
    fun testLine(){
        mainTest(1920,1080,8,1,0)
        mainTest(1920,1080,1,8,0)
    }
    @Test
    fun testOutOfRange(){
        mainTest(5,5,4,4,0)
        mainTest(7,7,4,4,0)
        mainTest(7,7,9,9,0)
        mainTest(1920,1080,3,3,0)
//        mainTest(1920,1080,9,9,0)
    }
    @Test
    fun testBigSize(){
        mainTest(19,10,9,9,0)
        mainTest(192,108,9,9,0)
        mainTest(1920,1080,9,9,0)
    }
    fun mainTest(w:Int,h:Int,unit_W:Int,unit_H:Int,range: Int){
        val data=Array(w){Array(h){(it+1).toShort()}}
        val uc=UnitContainerFactory.getContainers(data,unit_W,unit_H)
        val res=UnitContainerFactory.getData(uc,w,h)
        assertTrue(data.inRange(res,range))
    }
    fun Array<Array<Short>>.inRange(other:Array<Array<Short>>,range: Int):Boolean{
        for((i,e) in this.withIndex()){
            for((j) in e.withIndex()){
                var isInRange=false
                for(r in -range..range) {
                    if((this[i][j].toInt() + r).toShort() ==other[i][j])isInRange=true
                }
                if(!isInRange)throw Exception("data[$i][$j]${this[i][j]}!=${other[i][j]}")
            }
        }
        return true
    }
}