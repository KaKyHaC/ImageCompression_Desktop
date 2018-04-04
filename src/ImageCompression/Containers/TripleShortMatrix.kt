package ImageCompression.Containers

import ImageCompression.Constants.State
import java.util.*

class TripleShortMatrix(var Width: Int, var Height: Int, flag: Flag, var state: State) {
    var a: Array<ShortArray>
    var b: Array<ShortArray>
    var c: Array<ShortArray>
    var f: Flag//TODO remove flag

    init {
        f= Flag(flag.flag)
        a = Array(Width) { ShortArray(Height) }
        b = Array(Width) { ShortArray(Height) }
        c = Array(Width) { ShortArray(Height) }
    }

    fun assertMatrixInRange(tripleShortMatrix: TripleShortMatrix, range: Int):Boolean{
        if (Width != tripleShortMatrix.Width) return false
        if (Height != tripleShortMatrix.Height) return false
        if (f != tripleShortMatrix.f) return false
        if (state != tripleShortMatrix.state) return false

        if(!a.inRannge(tripleShortMatrix.a,range))return false
        if(!b.inRannge(tripleShortMatrix.b,range))return false
        if(!c.inRannge(tripleShortMatrix.c,range))return false

        return true
    }
    fun Array<ShortArray>.inRannge(a:Array<ShortArray>,range:Int):Boolean{
        if(size!=a.size)return false
        if(this[0].size!=a[0].size)return false

        for(i in 0..size-1){
            for(j in 0..this[0].size-1){
                var isEqual=false
                for(d in -range..range)
                    if(this[i][j]==(a[i][j]+d).toShort())
                        isEqual=true
                if(!isEqual) {
                    throw Exception("d[$i][$j](${this[i][j]})!=other[$i][$j](${a[i][j]}) in range=$range")
//                    return false
                }
            }
        }
        return true
    }
    fun Array<ShortArray>.copy():Array<ShortArray>{
        return Array<ShortArray>(size,{x->kotlin.ShortArray(this[0].size,{y->this[x][y]})})
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TripleShortMatrix

        if (Width != other.Width) return false
        if (Height != other.Height) return false
        if (f != other.f) return false
        if (state != other.state) return false

        if(!a.inRannge(other.a,0))return false
        if(!b.inRannge(other.b,0))return false
        if(!c.inRannge(other.c,0))return false

        return true
    }
    override fun hashCode(): Int {
        var result = Width
        result = 31 * result + Height
        result = 31 * result + f.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + Arrays.hashCode(a)
        result = 31 * result + Arrays.hashCode(b)
        result = 31 * result + Arrays.hashCode(c)
        return result
    }

    fun copy(): TripleShortMatrix {
        var res= TripleShortMatrix(Width,Height,f,state)
        res.a=a.copy()
        res.b=b.copy()
        res.c=c.copy()
        return res
    }
}
