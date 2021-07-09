package factory

import data_model.processing_data.ProcessingData
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
    fun testOpcOnly() {
        val mainProcessor = MainProcessor(MainProcessor.Params(ProcessorModuleFactorySamples.opcOnlyList))
        mainProcessor.run(ProcessingData.File(File("outTest.bmp")))
        mainProcessor.run(ProcessingData.File(File("image.bar")), MainProcessor.Mode.REVERSE)
    }
}