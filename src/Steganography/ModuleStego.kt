package Steganography

import Steganography.Containers.IContainer
import Steganography.Containers.OpcContainer
import Steganography.Containers.UnitContainer
import Steganography.Utils.ImageProcessorUtils

class ModuleStego(val dao: IDao) {
    interface IDao{
//        fun getUnitContainer():ImageProcessorUtils.Triple<IContainer<UnitContainer<Short>>>
//        fun getOpcContainer():ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>
        fun onOpcResult(result: ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>)
        fun onUnitResult(result: ImageProcessorUtils.Triple<IContainer<UnitContainer<Short>>>)
        fun onMessageRead(message: BooleanArray?)
    }
    //TODO make unsign
    val ipu= ImageProcessorUtils()
    fun direct(units: ImageProcessorUtils.Triple<IContainer<UnitContainer<Short>>>, message:BooleanArray?, isDivTwo:Boolean){
//        val units=dao.getUnitContainer()
        message?.let { val index=ipu.setMessage(units,message)}
        val opcs=ipu.directStego(units,isDivTwo)
        dao.onOpcResult(opcs)
    }
    fun reverse(opcs: ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>, unit_W: Int, unit_H: Int, isMultiTWO:Boolean) {
//        val opcs=dao.getOpcContainer()
        val units=ipu.reverceStegoOneThread(opcs, unit_W, unit_H, isMultiTWO)
        dao.onUnitResult(units)
        val message=ipu.getMessage(units)
        dao.onMessageRead(message)
    }
}