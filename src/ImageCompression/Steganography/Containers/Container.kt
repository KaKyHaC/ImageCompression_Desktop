package ImageCompression.Steganography.Containers

import java.util.*

class Container<T>(override var width: Int, override var height: Int) :IContainer<T> {
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
    override fun get(i: Int, j: Int): T? {
        return if(i<width&&j<height) container[i][j] else null
    }

    override fun set(i: Int, j: Int, value: T) {
        if(i<width&&j<height) container[i][j]=value
    }
}