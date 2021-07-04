package utils

import ImageCompressionLib.utils.functions.opc.IStegoMessageUtil

class StegoPassword : IStegoMessageUtil {
    val key:String

    constructor(key: String) {
        this.key = key
        curChar=if(key.length>0)key[0].toInt()else 0
    }
    var curChar:Int
    var counter=0;
    var curIndex=0
    override fun isUseNextBlock(): Boolean {
        return isUseNext()
    }
    private fun isUseNext():Boolean{
        if(curChar==counter){
            counter=0
            curChar=if(key.length>0)key[curIndex++%key.length].toInt()else 0
            return true
        }
        counter++
        return false
    }

}