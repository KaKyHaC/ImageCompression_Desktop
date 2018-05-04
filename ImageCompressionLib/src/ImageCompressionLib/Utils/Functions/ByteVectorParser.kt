package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.ByteVectorContainer
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.TripleDataOpcMatrixOld
import ImageCompressionLib.Utils.Objects.TimeManager

class ByteVectorParser private constructor(){
    companion object {
        @JvmStatic val instance= ByteVectorParser()
    }

    fun parseData(dataOld: TripleDataOpcMatrixOld, flag: Flag, globalBaseW:Int, globalBaseH:Int): ByteVectorContainer {
        val v = ByteVector()

        if(flag.isChecked(Flag.Parameter.GlobalBase)&&flag.isChecked(Flag.Parameter.OneFile)){
            dataOld.writeBaseToVector(v,flag,globalBaseW,globalBaseH)
        }
        dataOld.writeToVector(v, flag)

//        TimeManager.Instance.append("box to vector")

//        val vw = ByteVectorFile(pathToName + ModuleFile.typeMain)
//        vw.write(v, flag)
        var vbase: ByteVector?=null
        if (!flag.isChecked(Flag.Parameter.OneFile)) {
            vbase = ByteVector()
            dataOld.writeBaseToVector(vbase,flag,globalBaseW,globalBaseH)
            TimeManager.Instance.append("base to vector")
//            val bw = ByteVectorFile(pathToName + ModuleFile.typeSup)
//            bw.write(vbase, flag)
        }
        return ByteVectorContainer(v,vbase)
    }
    fun parseVector(container: ByteVectorContainer, flag: Flag): TripleDataOpcMatrixOld {
        val opcs= TripleDataOpcMatrixOld()
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