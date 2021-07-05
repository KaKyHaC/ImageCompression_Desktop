package ImageCompressionLib.steganography.containers

interface IContainer<T> {
    var width:Int
    var height:Int
    operator fun get(i:Int,j:Int):T?
    operator fun set(i:Int,j:Int,value:T)

    fun forEach(func: (w: Int, h: Int) -> T?){
        for(i in 0 until width){
            for (j in 0 until height){
                val v =func(i,j)
                v?.let { set(i,j,v) }
            }
        }
    }
    fun forEach(func: (el:T?) -> T?){
        for(i in 0 until width){
            for (j in 0 until height){
                val v =func(get(i,j))
                v?.let { set(i,j,v) }

            }
        }
    }
}