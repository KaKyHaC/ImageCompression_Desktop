package ImageCompressionLib.Steganography.Containers

import java.util.*

data class UnitContainer<T>(private val data:Array<Array<T>>,var message:Boolean=false): IContainer<T> {
    override var width:Int=0
        get()= data.size
    override var height:Int=0
        get() = data[0].size

    override operator fun get(i: Int,j:Int):T?{return if(i<width&&j<height) data[i][j] else null}//data[j][i]
    override operator fun set(i: Int,j: Int,value:T){if(i<width&&j<height)data[i][j]=value }
    fun getMatrix(): Array<Array<T>> {return data}

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UnitContainer<*>

        if (message != other.message) return false
        for((i,e) in data.withIndex()){
            for((j) in e.withIndex()){
                if(data[i][j]!=other.data[i][j])return false
            }
        }

        return true
    }
    override fun hashCode(): Int {
        var result = Arrays.hashCode(data)
        result = 31 * result + message.hashCode()
        return result
    }

    override fun toString(): String {
        val sb=StringBuilder()
        for((i ,s) in data.withIndex()){
            for((j ,v) in s.withIndex()){
                sb.append("$v,")
            }
            sb.append("\n")
        }
        return sb.toString()
    }


}