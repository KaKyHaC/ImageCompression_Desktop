package ImageCompressionLib.Containers

import com.sun.java.browser.plugin2.DOM
import java.util.*

class DataOpcMatrix {
    private val matrix:Array<Array<DataOpc>>
    val width:Int
        get() = matrix.size
    val height:Int
        get() = matrix[0].size

    constructor(matrix: Array<Array<DataOpc>>) {
        this.matrix = matrix
    }
    constructor(w:Int,h:Int,untiSize: Size){
        matrix= Array(w){Array(h){ DataOpc(untiSize) }}
    }
    constructor(w:Int,h:Int,init:(i:Int,j:Int)->DataOpc){
        matrix= Array(w){i->Array(h){j->init.invoke(i,j)}}
    }



    operator fun get(i:Int,j:Int): DataOpc {
        return matrix[i][j]
    }
    operator fun get(i:Int): Array<DataOpc> {
        return matrix[i]
    }
    operator fun set(i: Int,j: Int,value:DataOpc){
        matrix[i][j]=value
    }

    fun forEach(invoke:(i:Int,j:Int,value:DataOpc)->DataOpc?){
        for(i in 0 until width){
            for(j in 0 until height){
                matrix[i][j]=invoke.invoke(i,j,matrix[i][j])?:matrix[i][j]
            }
        }
    }

    fun copy():DataOpcMatrix{
        return DataOpcMatrix(width,height){i, j -> matrix[i][j].copy() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataOpcMatrix

        if (!Arrays.equals(matrix, other.matrix)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(matrix)
    }

    fun rect(wStart:Int,hStart:Int,wEnd:Int,hEnd:Int):DataOpcMatrix{
        return DataOpcMatrix(wEnd-wStart,hEnd-hStart){i, j ->matrix[i+wStart][j+hStart]}
    }
    fun rectSave(wStart:Int,hStart:Int,wEnd:Int,hEnd:Int):DataOpcMatrix{
        var wtmp=wEnd
        var htmp=hEnd
        var wtmpS=wStart
        var htmpS=hStart
        if(wEnd>=width)
            wtmp=width-1
        if(hEnd>=height)
            htmp=height-1

        if(wStart<0)
            wtmpS=0
        if(hStart<0)
            htmpS=0

        var w=wtmp-wtmpS
        var h=htmp-htmpS
        return DataOpcMatrix(w,h){i, j ->matrix[i+wStart][j+hStart]}
    }
    fun split(horizontalStep:Int,verticalStep:Int):Array<Array<DataOpcMatrix>>{
        var w=width/horizontalStep
        var h=height/verticalStep
        if(width%horizontalStep!=0)w++
        if(height%verticalStep!=0)h++
        val res=Array(w){i->Array(h){j->
            rectSave(i*horizontalStep,j*verticalStep,i*horizontalStep+horizontalStep,j*verticalStep+verticalStep)
        }}
        return res
    }
}