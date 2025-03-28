import android.content.Context
import android.content.res.AssetManager
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object TFLiteHelper {
    private var tflite: Interpreter? = null

    // Initialize the TensorFlow Lite interpreter
    fun initialize(context: Context, modelPath: String) {
        if (tflite == null) {
            val model = loadModelFile(context.assets, modelPath)
            tflite = Interpreter(model, Interpreter.Options())
        }
    }

    // Function to load model file
    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Function to run inference (input & output should match model's format)
    fun runInference(input: Array<FloatArray>, output: Array<FloatArray>) {
        tflite?.run(input, output)
    }

    // Close the interpreter when no longer needed
    fun close() {
        tflite?.close()
        tflite = null
    }
}