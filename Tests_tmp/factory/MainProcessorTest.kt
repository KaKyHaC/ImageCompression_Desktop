package factory

import data_model.processing_data.ProcessingData
import features.read_image.ModuleReadImage
import org.junit.Test
import java.io.File


internal class MainProcessorTest {

    @Test
    fun testDefault() {
        val mainProcessor = MainProcessor(MainProcessor.Params(ProcessorModuleFactorySamples.defaultList))
        mainProcessor.run(ProcessingData.File(File("outTest.bmp")))
        mainProcessor.run(ProcessingData.File(File("image.bar")), MainProcessor.Mode.REVERSE)
    }


    @Test
    fun testDefaultWithGod() {
        val mainProcessor = MainProcessor(MainProcessor.Params(ProcessorModuleFactorySamples.defaultListWithGod))
        mainProcessor.run(ProcessingData.File(File("outTest.bmp")))
        mainProcessor.run(ProcessingData.File(File("image.bar")), MainProcessor.Mode.REVERSE)
    }

    @Test
    fun testOpcOnly() {
        val mainProcessor = MainProcessor(MainProcessor.Params(ProcessorModuleFactorySamples.opcOnlyList))
        mainProcessor.run(ProcessingData.File(File("outTest.bmp")))
        mainProcessor.run(ProcessingData.File(File("image.bar")), MainProcessor.Mode.REVERSE)
    }

    @Test
    fun testYbr() {
        val list = listOf(
                ProcessorModuleFactory.ModuleParams.ReadImage(ModuleReadImage.Parameters("testYbr.bmp")),
                ProcessorModuleFactory.ModuleParams.RgbToYbr()
        )
        test(list)
    }

    @Test
    fun testEnl() {
        val list = listOf(
                ProcessorModuleFactory.ModuleParams.ReadImage(ModuleReadImage.Parameters("testEnl.bmp")),
                ProcessorModuleFactory.ModuleParams.RgbToYbr(),
                ProcessorModuleFactory.ModuleParams.Enlargement()
        )
        test(list)
    }


    @Test
    fun testDct() {
        val list = listOf(
                ProcessorModuleFactory.ModuleParams.ReadImage(ModuleReadImage.Parameters("testDct.bmp")),
                ProcessorModuleFactory.ModuleParams.RgbToYbr(),
                ProcessorModuleFactory.ModuleParams.Enlargement(),
                ProcessorModuleFactory.ModuleParams.Dct()
        )
        test(list)
    }


    @Test
    fun testQuant() {
        val list = listOf(
                ProcessorModuleFactory.ModuleParams.ReadImage(ModuleReadImage.Parameters("testQuant.bmp")),
                ProcessorModuleFactory.ModuleParams.RgbToYbr(),
                ProcessorModuleFactory.ModuleParams.Enlargement(),
                ProcessorModuleFactory.ModuleParams.Dct(),
                ProcessorModuleFactory.ModuleParams.Quantization()
        )
        test(list)
    }


    @Test
    fun testOpc() {
        val list = listOf(
                ProcessorModuleFactory.ModuleParams.ReadImage(ModuleReadImage.Parameters("testOpc.bmp")),
                ProcessorModuleFactory.ModuleParams.RgbToYbr(),
                ProcessorModuleFactory.ModuleParams.Enlargement(),
                ProcessorModuleFactory.ModuleParams.Dct(),
                ProcessorModuleFactory.ModuleParams.Quantization(),
                ProcessorModuleFactory.ModuleParams.Opc()
        )
        test(list)
    }

    @Test
    fun testRgbOpc() {
        val list = listOf(
                ProcessorModuleFactory.ModuleParams.ReadImage(ModuleReadImage.Parameters("testRgbOpc.bmp")),
                ProcessorModuleFactory.ModuleParams.Opc()
        )
        test(list)
    }

    @Test
    fun testOpcToBytes() {
        val list = listOf(
                ProcessorModuleFactory.ModuleParams.ReadImage(ModuleReadImage.Parameters("testOpcToBytes.bmp")),
                ProcessorModuleFactory.ModuleParams.RgbToYbr(),
                ProcessorModuleFactory.ModuleParams.Enlargement(),
                ProcessorModuleFactory.ModuleParams.Dct(),
                ProcessorModuleFactory.ModuleParams.Quantization(),
                ProcessorModuleFactory.ModuleParams.Opc(),
                ProcessorModuleFactory.ModuleParams.OpcToBytes()
        )
        test(list)
    }

    fun test(list: List<ProcessorModuleFactory.ModuleParams>) {
        val mainProcessor = MainProcessor(MainProcessor.Params(list))
        val run = mainProcessor.run(ProcessingData.File(File("outTest.bmp")))
        mainProcessor.run(run, MainProcessor.Mode.REVERSE)
    }
}