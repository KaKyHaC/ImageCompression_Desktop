package features.read_image

import data_model.processing_data.ProcessingData
import org.junit.Test
import java.io.File


internal class ModuleReadImageTest {

    @Test
    fun testIO(){
        val readModule = ModuleReadImage(ModuleReadImage.Parameters("jpegEncoder.jpeg"))
        val writeModule = ModuleReadImage(ModuleReadImage.Parameters("outTest.bmp"))
        val processDirectTyped = readModule.processDirectTyped(ProcessingData.File(File("")))
        writeModule.processReverseTyped(processDirectTyped)
    }
}