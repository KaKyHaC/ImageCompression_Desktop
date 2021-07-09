package factory

import data_model.processing_data.ProcessingData
import features.dct.ModuleDct
import features.enlargement.ModuleEnlargement
import features.image_format.ModuleRgbToYCbCr
import features.opc2.ModuleOpc2
import features.opc_format.ModuleOpcToBytes
import features.quantization.ModuleQuantization
import features.read_image.ModuleReadImage
import features.write_bytes.ModuleWriteBytes
import features.write_bytes.ModuleWriteBytesTest
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals


internal class MainProcessorModulesTest {

    val moduleReadImage = ModuleReadImage()
    val moduleRgbToYCbCr = ModuleRgbToYCbCr()
    val moduleEnlargement = ModuleEnlargement()
    val moduleDct = ModuleDct()
    val moduleQuantization = ModuleQuantization()
    val moduleOpc2 = ModuleOpc2()
    val moduleOpcToBytes = ModuleOpcToBytes()
    val moduleWriteBytes = ModuleWriteBytes()

    @Test
    fun testDefault() {
        val readData = ProcessingData.File(File("outTest.bmp"))

        val imageOrigin = moduleReadImage.processDirectTyped(readData)
        val ybr = moduleRgbToYCbCr.processDirectTyped(imageOrigin)
        val enl = moduleEnlargement.processDirectTyped(ybr)
        val dct = moduleDct.processDirectTyped(enl)
        val qnt = moduleQuantization.processDirectTyped(dct)
        val opc = moduleOpc2.processDirectTyped(qnt)
        val bytes = moduleOpcToBytes.processDirectTyped(opc)
        val outFile = moduleWriteBytes.processDirectTyped(bytes)
        println("startReverse")
        val rBytes = moduleWriteBytes.processReverseTyped(outFile)
        val rOpc = moduleOpcToBytes.processReverseTyped(rBytes)
        val rQnt = moduleOpc2.processReverseTyped(rOpc)
        val rDct = moduleQuantization.processReverseTyped(rQnt)
        val rEnl = moduleDct.processReverseTyped(rDct)
        val rYbr = moduleEnlargement.processReverseTyped(rEnl)
        val rRgb = moduleRgbToYCbCr.processReverseTyped(rYbr)
        moduleReadImage.processReverseTyped(rRgb)
        println("start equals")

        assertEquals(bytes, rBytes)
        assertEquals(opc, rOpc)
        assertEquals(qnt, rQnt)
    }
}