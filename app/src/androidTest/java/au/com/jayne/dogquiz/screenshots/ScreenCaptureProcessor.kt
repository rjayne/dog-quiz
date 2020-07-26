package au.com.jayne.dogquiz.screenshots

import android.content.Context
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import java.io.File

class ScreenCaptureProcessor(context: Context): BasicScreenCaptureProcessor() {

    val externalFilesDirectory: String

    init {
        externalFilesDirectory = context.getExternalFilesDir(null)!!.absolutePath
        this.mDefaultScreenshotPath = File(getScreenshotFilesDirectoryPath())
    }

    override fun getFilename(prefix: String): String = prefix

    fun getScreenshotFilesDirectoryPath(): String {
        return externalFilesDirectory + SCREENSHOT_FOLDER
    }

    companion object
    {
        private const val SCREENSHOT_FOLDER = "/DoggyQuizScreenshot"
    }

}