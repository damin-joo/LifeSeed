package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, Step1Activity::class.java)
        startActivity(intent)

    }
    // ✅ Function to load TensorFlow Lite model properly
    private fun loadModelFile(): Interpreter {
        val assetFileDescriptor = assets.openFd("your_model.tflite")
        val inputStream = assetFileDescriptor.createInputStream()
        val byteArray = inputStream.readBytes()
        val buffer = ByteBuffer.allocateDirect(byteArray.size).apply {
            order(ByteOrder.nativeOrder())
            put(byteArray)
        }
        return Interpreter(buffer)
    }

    // ✅ Function to preprocess input
    private fun preprocessInput(vararg inputs: String): Array<FloatArray> {
        val values = inputs.map { it.toFloatOrNull() ?: 0.0f }
        return arrayOf(values.toFloatArray()) // Ensure correct shape for model
    }

    // ✅ Function to run inference
    private fun runInference(input: Array<FloatArray>): Float {
        val output = Array(1) { FloatArray(1) }
        interpreter.run(input, output)
        return output[0][0] * 100 // Convert to percentage
    }

    // ✅ Function to open ResultActivity
    private fun openResultActivity(successRate: Float) {
        val intent = Intent(this, ResultsActivity::class.java)
        intent.putExtra("SUCCESS_RATE", successRate)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        interpreter.close()
    }
}
