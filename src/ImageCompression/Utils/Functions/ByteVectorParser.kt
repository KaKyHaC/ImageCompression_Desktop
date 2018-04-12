package ImageCompression.Utils.Functions

import ImageCompression.Containers.ByteVector
import ImageCompression.Containers.ByteVectorContainer
import ImageCompression.Containers.Flag
import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.Utils.Objects.TimeManager

class ByteVectorParser private constructor(){
    companion object {
        @JvmStatic val instance= ByteVectorParser()
    }

    fun parseData(data:TripleDataOpcMatrix, flag: Flag, globalBaseW:Int, globalBaseH:Int): ByteVectorContainer {
        val v = ByteVector()

        if(flag.isChecked(Flag.Parameter.GlobalBase)&&flag.isChecked(Flag.Parameter.OneFile)){
            data.writeBaseToVector(v,flag,globalBaseW,globalBaseH)
        }
        data.writeToVector(v, flag)

//        TimeManager.Instance.append("box to vector")

//        val vw = ByteVectorFile(pathToName + ModuleFile.typeMain)
//        vw.write(v, flag)
        var vbase:ByteVector?=null
        if (!flag.isChecked(Flag.Parameter.OneFile)) {
            vbase = ByteVector()
            data.writeBaseToVector(vbase,flag,globalBaseW,globalBaseH)
            TimeManager.Instance.append("base to vector")
//            val bw = ByteVectorFile(pathToName + ModuleFile.typeSup)
//            bw.write(vbase, flag)
        }
        return ByteVectorContainer(v,vbase)
    }
    fun parseVector(container: ByteVectorContainer, flag:Flag): TripleDataOpcMatrix {
        val opcs= TripleDataOpcMatrix()
        val vmain=container.mainData


        if(flag.isChecked(Flag.Parameter.GlobalBase)&&flag.isChecked(Flag.Parameter.OneFile)){
            opcs.readBaseFromVector(vmain,flag)
        }
        opcs.readFromVector(vmain,flag)


        if(!flag.isChecked(Flag.Parameter.OneFile)){
//            val br=ByteVectorFile(pathToName+ ModuleFile.typeSup)
//            val bp=br.read()
//            val base=bp.first
            if(container.suportData==null)
                throw Exception("container.suportData==null")

            val base=container.suportData
            opcs.readBaseFromVector(base,flag)
        }
        return opcs
    }
}