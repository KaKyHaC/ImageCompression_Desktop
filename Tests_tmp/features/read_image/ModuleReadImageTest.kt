package features.read_image

import data_model.processing_data.ProcessingData
import org.junit.Test
import java.io.File


internal class ModuleReadImageTest {

    @Test
    fun testIO(){
        val module = ModuleReadImage(ModuleReadImage.Parameters("outTest.bmp"))
        val processDirectTyped = module.processDirectTyped(ProcessingData.File(File("jpegEncoder.jpeg")))
        module.processReverseTyped(processDirectTyped)
    }
}