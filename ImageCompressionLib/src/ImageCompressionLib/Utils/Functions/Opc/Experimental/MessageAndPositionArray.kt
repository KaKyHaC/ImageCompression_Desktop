package ImageCompressionLib.Utils.Functions.Opc.Experimental

class MessageAndPositionArray {
    data class MandP(var message: Boolean,val position: Int)
    val data:MutableSet<MandP> = HashSet(0)

    fun isHadMessage(pos:Int):Boolean{
        for( a in data)
            if(a.position==pos)
                return true
        return false
    }
    fun getMessageAt(pos:Int):Boolean{
        for (a in data)
            if(a.position==pos)
                return a.message
        return false
    }
    fun setMessageAt(pos:Int,message: Boolean){
        for (a in data)
            if(a.position==pos)
                a.message=message
    }
    fun addMessageAndPosition(message: Boolean,pos: Int){
        data.add(MandP(message,pos))
    }
    fun copy():MessageAndPositionArray{
        val res=MessageAndPositionArray()
        for( a in data){
            res.addMessageAndPosition(a.message,a.position)
        }
        return res
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageAndPositionArray) return false

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String {
        return "MessageAndPositionArray(data=$data)"
    }

    companion object {
        @JvmStatic
        fun createRandom(size:Int): MessageAndPositionArray {
            val res= MessageAndPositionArray()
            for(i in 0 until size)
                res.addMessageAndPosition(true,i)
            return res
        }
    }
}