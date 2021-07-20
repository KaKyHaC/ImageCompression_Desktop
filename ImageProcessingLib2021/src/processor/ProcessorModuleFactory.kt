package processor

import features.IDataProcessor
import features.dct.ModuleDct
import features.enlargement.ModuleEnlargement
import features.image_format.ModuleRgbToYCbCr
import features.opc2.ModuleBasesOpc2
import features.opc2.ModuleOpc2
import features.opc2_god_format.ModuleOpc2God
import features.opc_format.ModuleOpcToBytes
import features.quantization.ModuleQuantization
import features.read_image.ModuleReadImage
import features.write_bytes.ModuleWriteBytes

object ProcessorModuleFactory {

    fun getModule(params: ModuleParams): IDataProcessor {
        return when(params) {
            is ModuleParams.Dct -> ModuleDct(params.params)
            is ModuleParams.Enlargement -> ModuleEnlargement(params.params)
            is ModuleParams.Opc -> ModuleOpc2(params.params)
            is ModuleParams.OpcBases -> ModuleBasesOpc2(params.params)
            is ModuleParams.OpcToBytes -> ModuleOpcToBytes(params.params)
            is ModuleParams.Quantization -> ModuleQuantization(params.params)
            is ModuleParams.ReadImage -> ModuleReadImage(params.params)
            is ModuleParams.RgbToYbr -> ModuleRgbToYCbCr(params.params)
            is ModuleParams.WriteBytes -> ModuleWriteBytes(params.params)
            is ModuleParams.OpcToBytesCascade -> ModuleOpc2God(params.params)
        }
    }

    sealed class ModuleParams {
        data class ReadImage(val params: ModuleReadImage.Parameters = ModuleReadImage.Parameters()) : ModuleParams()
        data class RgbToYbr(val params: ModuleRgbToYCbCr.Parameters = ModuleRgbToYCbCr.Parameters()) : ModuleParams()
        data class Enlargement(val params: ModuleEnlargement.Parameters = ModuleEnlargement.Parameters()) : ModuleParams()
        data class Dct(val params: ModuleDct.Parameters = ModuleDct.Parameters()) : ModuleParams()
        data class Quantization(val params: ModuleQuantization.Parameters = ModuleQuantization.Parameters()) : ModuleParams()
        data class Opc(val params: ModuleOpc2.Parameters = ModuleOpc2.Parameters()) : ModuleParams()
        data class OpcBases(val params: ModuleBasesOpc2.Parameters = ModuleBasesOpc2.Parameters()) : ModuleParams()
        data class OpcToBytes(val params: ModuleOpcToBytes.Parameters = ModuleOpcToBytes.Parameters()) : ModuleParams()
        data class OpcToBytesCascade(val params: ModuleOpc2God.Parameters = ModuleOpc2God.Parameters()) : ModuleParams()
        data class WriteBytes(val params: ModuleWriteBytes.Parameters = ModuleWriteBytes.Parameters()) : ModuleParams()
    }
}