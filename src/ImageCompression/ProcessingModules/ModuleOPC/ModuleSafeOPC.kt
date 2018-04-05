package ImageCompression.ProcessingModules.ModuleOPC

import ImageCompression.Constants.State
import ImageCompression.Containers.DataOpc
import ImageCompression.Containers.Flag
import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Containers.TripleShortMatrix
import ImageCompression.Steganography.Containers.Container
import ImageCompression.Steganography.Containers.IContainer
import ImageCompression.Steganography.Containers.OpcContainer
import ImageCompression.Steganography.Containers.UnitContainer
import ImageCompression.Steganography.ModuleStego
import ImageCompression.Steganography.ModuleStego.IDao
import ImageCompression.Steganography.Utils.ImageProcessorUtils
import ImageCompression.Steganography.Utils.MessageParser
import ImageCompression.Steganography.Utils.UnitContainerFactory

class ModuleSafeOPC :AbsModuleOPC{
    var message:String?
    data class Info(var unit_H:Int,var unit_W:Int,var width:Int,var height:Int)
    var info:Info
    var isDivTwo=true
    var isMultiTWO=true
    val moduleStego:ModuleStego

    constructor(tripleShort: TripleShortMatrix?, flag: Flag, message: String?, info: Info) : super(tripleShort, flag) {
        this.message = message
        this.info = info
    }
    constructor(tripleDataOpc: TripleDataOpcMatrix?, flag: Flag, message: String?, info: Info) : super(tripleDataOpc, flag) {
        this.message = message
        this.info = info
    }
//    val ipu=ImageProcessorUtils()
    



    init {
        val dao=object :IDao{
            override fun onOpcResult(result: ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>) {
                tripleDataOpc=result.toTripleDataOpcMatrix()
            }

            override fun onUnitResult(result: ImageProcessorUtils.Triple<IContainer<UnitContainer<Short>>>) {
                tripleShort=result.toTripleShortMatrix()
            }

            override fun onMessageRead(message: BooleanArray?) {
                if(message!=null)this@ModuleSafeOPC.message=MessageParser().toString(message)
            }

        }
        moduleStego= ModuleStego(dao)
    }


    override fun direct(tripleShort: TripleShortMatrix): TripleDataOpcMatrix {
        val tmp= tripleShort.toTripleContainer()
        val m= if(message!=null)MessageParser().fromString(message!!) else null
        moduleStego.direct(tmp,m,isDivTwo)
        return tripleDataOpc!!
    }

    override fun reverce(tripleDataOpc: TripleDataOpcMatrix): TripleShortMatrix {
        val tmp = tripleDataOpc.toTripleContainer()
        moduleStego.reverse(tmp,info.unit_W, info.unit_H, isMultiTWO)
        return tripleShort!!
    }

    fun  TripleDataOpcMatrix.toTripleContainer():ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>{
        val a= Container<OpcContainer<Short>>(this.a!!.size,this.a!![0].size)
        {w: Int, h: Int -> OpcContainer(this@toTripleContainer.a!![w][h].N,this.a!![w][h].base.toTypedArray()) }
        val b= Container<OpcContainer<Short>>(this.b!!.size,this.b!![0].size)
        {w: Int, h: Int -> OpcContainer(this@toTripleContainer.b!![w][h].N,this.b!![w][h].base.toTypedArray()) }
        val c= Container<OpcContainer<Short>>(this.c!!.size,this.c!![0].size)
        {w: Int, h: Int -> OpcContainer(this@toTripleContainer.c!![w][h].N,this.c!![w][h].base.toTypedArray()) }

        return ImageProcessorUtils.Triple(a,b,c)
    }
    fun ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>.toTripleDataOpcMatrix():TripleDataOpcMatrix{
        val res=TripleDataOpcMatrix()
        res.a=Array(this.r!!.width){w->Array(this.r!!.height){h->
            val tmp=DataOpc()
            tmp.N=this.r!![w,h]!!.code
            tmp.base=this.r!![w,h]!!.base.toShortArray()
            tmp
        } }
        res.b=Array(this.g!!.width){w->Array(this.g!!.height){h->
            val tmp=DataOpc()
            tmp.N=this.g!![w,h]!!.code
            tmp.base=this.g!![w,h]!!.base.toShortArray()
            tmp
        } }
        res.c=Array(this.b!!.width){w->Array(this.b!!.height){h->
            val tmp=DataOpc()
            tmp.N=this.b!![w,h]!!.code
            tmp.base=this.b!![w,h]!!.base.toShortArray()
            tmp
        } }
        return TripleDataOpcMatrix()
    }
    
    fun TripleShortMatrix.toTripleContainer():ImageProcessorUtils.Triple<IContainer<UnitContainer<Short>>>{
//        val res= TripleShortMatrix(0,0,State.Yenl)
        val ca=Container<Short>(a.size,a[0].size){w, h ->  a[w][h]}
        val cb=Container<Short>(b.size,b[0].size){w, h ->  b[w][h]}
        val cc=Container<Short>(c.size,c[0].size){w, h ->  c[w][h]}
        val a=UnitContainerFactory.getContainers(ca,info.unit_W,info.unit_H)
        val b=UnitContainerFactory.getContainers(cb,info.unit_W,info.unit_H)
        val c=UnitContainerFactory.getContainers(cc,info.unit_W,info.unit_H)

        return ImageProcessorUtils.Triple(a,b,c)
    }
    fun ImageProcessorUtils.Triple<IContainer<UnitContainer<Short>>>.toTripleShortMatrix():TripleShortMatrix{
        val res=TripleShortMatrix(0,0,State.Yenl)
        val r= UnitContainerFactory.getData(this.r!!,info.width,info.height)
        val g= UnitContainerFactory.getData(this.g!!,info.width,info.height)
        val b= UnitContainerFactory.getData(this.b!!,info.width,info.height)

        res.a=Array(r.width){i->ShortArray(r.height){j->r[i,j]!!}}
        res.b=Array(g.width){i->ShortArray(g.height){j->g[i,j]!!}}
        res.c=Array(b.width){i->ShortArray(b.height){j->b[i,j]!!}}

        return res
    }

}