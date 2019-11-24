package ImageCompressionLib.Data.Type

class MyBufferedImage (val width:Int,val height:Int){
    companion object {
        val ROLL_ALFA=24
        val ROLL_RED=16
        val ROLL_GREEN=8
        val ROLL_BLUE=0
    }

    private val data:Array<IntArray>
    init{
        data= Array(width){ IntArray(height) }
    }

    fun setRGB(i:Int,j:Int,rgb:Int){
        data[i][j]=rgb
    }
    fun getRGB(i: Int,j: Int):Int{
        return data[i][j]
    }
    fun getDataBuffer():ByteArray{
        val v= ByteVector()
        for(i in 0 until width)
            for(j in 0 until height){
                v.append(getB(i,j).toByte())
                v.append(getG(i,j).toByte())
                v.append(getR(i,j).toByte())
            }
        return v.toByteArray()
    }
    fun getR(i: Int,j: Int):Int{
        return data[i][j] shr ROLL_RED and 0xff
    }
    fun getG(i: Int,j: Int):Int{
        return data[i][j] shr ROLL_GREEN and 0xff
    }
    fun getB(i: Int,j: Int):Int{
        return data[i][j] shr ROLL_BLUE and 0xff
    }
    fun getData():Array<IntArray>{
        return data
    }
    fun getIntArray():IntArray{
        val res=IntArray(width*height)
        for(i in 0 until width)
            for(j in 0 until height)
                res[i+width*j]=data[i][j]
        return res
    }
    fun forEach(doThigs:(i:Int,j:Int,value:Int)->Int?){
        for(i in 0 until width)
            for(j in 0 until height)
                data[i][j]=doThigs(i,j,data[i][j])?:data[i][j]
    }
    //TODO add seters for R,G,B

    fun copy():MyBufferedImage{
        val res=MyBufferedImage(width, height)
        res.forEach(){ i, j, value ->  return@forEach data[i][j]}
        return res
    }
    fun assertInRange(other:MyBufferedImage,range:Int){
        forEach(){i, j, value ->
            var RinRange=false
            var GinRange=false
            var BinRange=false
            for(r in -range..range){
                if(getR(i,j)+r==other.getR(i,j))
                    RinRange=true
                if(getG(i,j)+r==other.getG(i,j))
                    GinRange=true
                if(getB(i,j)+r==other.getB(i,j))
                    BinRange=true
            }
            if(!(RinRange&&BinRange&&GinRange))
                throw Exception("data[$i][$j]:$value!=${other.getRGB(i,j)}")

            return@forEach null
        }
    }

    operator fun minus(other: MyBufferedImage):MyBufferedImage{
        val res=copy()
        res.forEach(){i, j, value ->
             return@forEach value-other.getRGB(i,j)
        }
        return res
    }
    }