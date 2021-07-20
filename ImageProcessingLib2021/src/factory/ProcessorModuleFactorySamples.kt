package factory

object ProcessorModuleFactorySamples {

    val defaultList = listOf(
            ProcessorModuleFactory.ModuleParams.ReadImage(),
            ProcessorModuleFactory.ModuleParams.RgbToYbr(),
            ProcessorModuleFactory.ModuleParams.Enlargement(),
            ProcessorModuleFactory.ModuleParams.Dct(),
            ProcessorModuleFactory.ModuleParams.Quantization(),
            ProcessorModuleFactory.ModuleParams.Opc(),
            ProcessorModuleFactory.ModuleParams.OpcToBytes(),
            ProcessorModuleFactory.ModuleParams.WriteBytes()
    )

    val defaultListWithGod = listOf(
            ProcessorModuleFactory.ModuleParams.ReadImage(),
            ProcessorModuleFactory.ModuleParams.RgbToYbr(),
            ProcessorModuleFactory.ModuleParams.Enlargement(),
            ProcessorModuleFactory.ModuleParams.Dct(),
            ProcessorModuleFactory.ModuleParams.Quantization(),
            ProcessorModuleFactory.ModuleParams.Opc(),
            ProcessorModuleFactory.ModuleParams.OpcToBytesCascade(),
            ProcessorModuleFactory.ModuleParams.WriteBytes()
    )

    val opcOnlyList = listOf(
            ProcessorModuleFactory.ModuleParams.ReadImage(),
            ProcessorModuleFactory.ModuleParams.Opc(),
            ProcessorModuleFactory.ModuleParams.OpcToBytes(),
            ProcessorModuleFactory.ModuleParams.WriteBytes()
    )
}