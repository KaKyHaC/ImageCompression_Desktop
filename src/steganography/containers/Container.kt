package steganography.containers

import java.util.*

class Container<T>(override var width: Int, override var height: Int) : IContainer<T> {
    private val container:Vector<Vector<T?>>
    init {
        container= Vector<Vector<T?>>(width)
        for (i in 0..width-1){
            container.addElement(Vector<T?>(height))
            for (j in 0..height-1){
                container[i].addElement(null)
            }
        }
    }
    constructor(width: Int,height: Int,init:(w:Int,h:Int)->T) : this(width, height) {
        for(i in 0 until width){
            for (j in 0 until height){
                val t= init(i,j)
                container[i][j]=t
            }
        }
    }
    override fun get(i: Int, j: Int): T? {
        return if(i<width&&j<height) container[i][j] else null
    }

    override fun set(i: Int, j: Int, value: T) {
        if(i<width&&j<height) container[i][j]=value
    }


    fun inRange(other: IContainer<T>, range:Int):Boolean{
        if(width!=other.width||height!=other.height)return false

        for(i in 0 until width){
            for(j in 0 until height){
                var inRange:Boolean=false

                for(r in -range..range){
                    val v=this[i,j] as Number
                    val v1=other[i,j]as Number
                    if((v.toInt()+r)==v1!!.toInt())
                        inRange=true
                }
                if(!inRange)return false
            }
        }
        return true
    }

    override fun toString(): String {
        val sb=StringBuilder()
        for((i ,s) in container.withIndex()){
            for((j ,v) in s.withIndex()){
                sb.append("$v,")
            }
            sb.append("\n")
        }
        return sb.toString()
    }

}