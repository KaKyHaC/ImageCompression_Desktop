package ImageCompression.Steganography

import ImageCompression.Steganography.Containers.UnitContainer

class UnitContainerFactory private constructor(){

    companion object {
        @JvmStatic fun getContainers(data:Array<Array<Short>>,unit_Width:Int,unit_Height:Int)
                :Array<Array<UnitContainer<Short>>>{
            val resWidth=data.size/unit_Width+if(data.size%unit_Width!=0)1 else 0
            var resHeight=data[0].size/unit_Height+if(data[0].size%unit_Height!=0)1 else 0
            val res = Array<Array<UnitContainer<Short>>>(resWidth){ Array(resHeight){
                    val mat=Array<Array<Short>>(unit_Width){ Array(unit_Height){(0).toShort()} }
                UnitContainer(mat)
                }
            }

            for((i,st) in data.withIndex()){
                for((j,v) in st.withIndex()){
                    val r=res[i/unit_Width][j/unit_Height]
                    val g=r[i%unit_Width,j%unit_Height]
                    res[i/unit_Width][j/unit_Height][i%unit_Width,j%unit_Height] = data[i][j].toShort()
                }
            }
            return res
        }
        @JvmStatic fun  getData(data:Array<Array<UnitContainer<Short>>>, width:Int, height:Int)
                :Array<Array<Short>>{

            val unit_Width=data[0][0].width
            val unit_Height=data[0][0].height
            val res = Array<Array<Short>>(width){ Array(height){(0).toShort()}}

            for(i in 0 until width){
                for(j in 0 until height){
                    res[i][j]=data[i/unit_Width][j/unit_Height][i%unit_Width,j%unit_Height]?:0
                }
            }
            return res
        }
    }
}