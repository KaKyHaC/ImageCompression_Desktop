package ImageCompressionLib.ProcessingModules.ModuleOPC

import ImageCompressionLib.Containers.*
import ImageCompressionLib.Steganography.Containers.Container
import ImageCompressionLib.Steganography.Containers.IContainer
import ImageCompressionLib.Steganography.Containers.OpcContainer
import ImageCompressionLib.Steganography.Containers.UnitContainer
import ImageCompressionLib.Steganography.ModuleStego
import ImageCompressionLib.Steganography.ModuleStego.IDao
import ImageCompressionLib.Steganography.Utils.ImageProcessorUtils
import ImageCompressionLib.Steganography.Utils.MessageParser
import ImageCompressionLib.Steganography.Utils.UnitContainerFactory

class ModuleSafeOPC :AbsModuleOPC{
    var message:String?=null
    var sameBase: Size
    var imageSize:Size?=null
    var isDivTwo=true
    var isMultiTWO=true

    val moduleStego:ModuleStego
    var onMessageReadedListener:((message:String)->Unit)?=null

    constructor(tripleShort: TripleShortMatrix, flag: Flag, message: String?, sameBase: Size) : super(tripleShort, flag) {
        this.message = message
        this.sameBase = sameBase
//        imageSize!!.imSize
    }
    constructor(tripleDataOpc: TripleDataOpcMatrix, flag: Flag, sameBase: Size,imSize: Size) : super(tripleDataOpc, flag) {
//        this.message = message
        this.sameBase = sameBase
        imageSize=imSize
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
                if(message!=null) {
                    val m = MessageParser().toString(message)
                    this@ModuleSafeOPC.message = m
                    onMessageReadedListener?.invoke(m)
                }
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
        moduleStego.reverse(tmp,sameBase.width, sameBase.height, isMultiTWO)
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
            val tmp= DataOpcOld()
            tmp.N=this.r!![w,h]!!.code
            tmp.base=this.r!![w,h]!!.base.toShortArray()
            tmp
        } }
        res.b=Array(this.g!!.width){w->Array(this.g!!.height){h->
            val tmp= DataOpcOld()
            tmp.N=this.g!![w,h]!!.code
            tmp.base=this.g!![w,h]!!.base.toShortArray()
            tmp
        } }
        res.c=Array(this.b!!.width){w->Array(this.b!!.height){h->
            val tmp= DataOpcOld()
            tmp.N=this.b!![w,h]!!.code
            tmp.base=this.b!![w,h]!!.base.toShortArray()
            tmp
        } }
        return res
    }
    
    fun TripleShortMatrix.toTripleContainer():ImageProcessorUtils.Triple<IContainer<UnitContainer<Short>>>{
//        val res= TripleShortMatrix(0,0,State.Yenl)
        val ca=Container<Short>(a.size,a[0].size){w, h ->  a[w][h]}
        val cb=Container<Short>(b.size,b[0].size){w, h ->  b[w][h]}
        val cc=Container<Short>(c.size,c[0].size){w, h ->  c[w][h]}
        val a=UnitContainerFactory.getContainers(ca,sameBase.width,sameBase.height)
        val b=UnitContainerFactory.getContainers(cb,sameBase.width,sameBase.height)
        val c=UnitContainerFactory.getContainers(cc,sameBase.width,sameBase.height)

        return ImageProcessorUtils.Triple(a,b,c)
    }
    fun ImageProcessorUtils.Triple<IContainer<UnitContainer<Short>>>.toTripleShortMatrix():TripleShortMatrix{
        val res=TripleShortMatrix(0,0,ImageCompressionLib.Constants.State.Yenl)
        val r= UnitContainerFactory.getData(this.r!!,imageSize!!.width,imageSize!!.height)
        val g= UnitContainerFactory.getData(this.g!!,imageSize!!.width,imageSize!!.height)
        val b= UnitContainerFactory.getData(this.b!!,imageSize!!.width,imageSize!!.height)

        res.a=Array(r.width){i->ShortArray(r.height){j->r[i,j]!!}}
        res.b=Array(g.width){i->ShortArray(g.height){j->g[i,j]!!}}
        res.c=Array(b.width){i->ShortArray(b.height){j->b[i,j]!!}}

        return res
    }

}