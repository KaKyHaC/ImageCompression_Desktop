package ImageCompression.Steganography

import ImageCompression.Steganography.Containers.Container
import ImageCompression.Steganography.Containers.IContainer
import ImageCompression.Steganography.Containers.UnitContainer

class UnitContainerFactory private constructor(){

    companion object {
        @JvmStatic fun getContainers(data: IContainer<Short>, unit_Width:Int, unit_Height:Int)
                :IContainer<UnitContainer<Short>>{
            val resWidth=data.width/unit_Width+if(data.width%unit_Width!=0)1 else 0
            val resHeight=data.height/unit_Height+if(data.height%unit_Height!=0)1 else 0
            val res = Container<UnitContainer<Short>>(resWidth,resHeight){i,j->
                    val mat=Array(unit_Width){ Array(unit_Height){(0).toShort()} }
                UnitContainer(mat)
                }


            for(i in 0 until data.width){
                for(j in 0 until data.height){
//                    val r=res[i/unit_Width][j/unit_Height]
//                    val g=r[i%unit_Width,j%unit_Height]
                    res[i/unit_Width,j/unit_Height]!![i%unit_Width,j%unit_Height] = data[i,j]!!.toShort()
                }
            }
            return res
        }
        @JvmStatic fun  getData(data:IContainer<UnitContainer<Short>>, width:Int, height:Int)
                :IContainer<Short>{

            val unit_Width=data[0,0]!!.width
            val unit_Height=data[0,0]!!.height
            val res = Container<Short>(width,height){i,j->(0).toShort()}

            for(i in 0 until width){
                for(j in 0 until height){
                    res[i,j]=data[i/unit_Width,j/unit_Height]!![i%unit_Width,j%unit_Height]?:0
                }
            }
            return res
        }
    }
}