package com.example.LifeSeed
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityResultsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var interpreter: Interpreter  // Declare interpreter globally

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Load TensorFlow model once in onCreate
        interpreter = loadModelFile()

        // Retrieve values passed from previous activities
        val p1_age = intent.getIntExtra("p1_age", -1)
        val p2_age = intent.getIntExtra("p2_age", -1)
        val ivfCycles = intent.getIntExtra("ivfCycles", 0)
        val diCycles = intent.getIntExtra("diCycles", 0)
        val previousPregnancies = intent.getIntExtra("previousPregnancies", 0)

        val p1_ethnicity_string = intent.getStringExtra("p1_ethnicity") ?: ""
        val p2_ethnicity_string = intent.getStringExtra("p2_ethnicity") ?: ""
        val causeInfertility_string = intent.getStringExtra("causeInfertility") ?: ""
        val treatmentType_string = intent.getStringExtra("treatmentType") ?: ""
        val singleEmbryo = intent.getBooleanExtra("singleEmbryo", false)
        val spermSource_string = intent.getStringExtra("spermSource") ?: ""
        val eggSource_string = intent.getStringExtra("eggSource") ?: ""

        // Encode categorical features and prepare input array
        val ethnicityOptions = listOf("Asian", "European", "African", "Native American", "Latin American", "Other")
        val infertilityOptions = listOf("Unexplained", "Male Factor", "Female Factor", "Combined Factors", "Other")
        val treatmentOptions = listOf("IVF", "ICSI", "IUI", "Frozen Embryo Transfer", "Other")
        val sourceOptions = listOf("Own Sperm", "Donor Sperm")


        val inputArray = floatArrayOf(
            p1_age.toFloat(),
            p2_age.toFloat(),
            encodeCategory(p1_ethnicity_string, ethnicityOptions),
            encodeCategory(p2_ethnicity_string, ethnicityOptions),
            encodeCategory(causeInfertility_string, infertilityOptions),
            encodeCategory(treatmentType_string, treatmentOptions),
            if (singleEmbryo) 1f else 0f,
            encodeCategory(spermSource_string, sourceOptions),
            encodeCategory(eggSource_string, sourceOptions),
            ivfCycles.toFloat(),
            diCycles.toFloat(),
            previousPregnancies.toFloat()
        )

        // Run the TensorFlow Lite model
        val output = runTFLiteModel(inputArray)
        val successRate = output * 100

        // Log the received values for debugging
        Log.d("ResultsActivity", "DEBUG-input: $inputArray")
        Log.d("ResultsActivity", "DEBUG-output: $successRate")

        // Display result
        binding.resultValue.text = when {
            successRate >= 80 -> "Excellent Chances: %.2f%%".format(successRate)
            successRate >= 50 -> "Moderate Chances: %.2f%%".format(successRate)
            else -> "Low Chances: %.2f%%".format(successRate)
        }

        // Back Button Listener
        binding.backButton.setOnClickListener { finish() }

        // Home Button Listener
        binding.homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    // Model inference function
    private fun runTFLiteModel(inputArray: FloatArray): Float {
        // Prepare output array and run inference
        val outputArray = FloatArray(1) // Assuming the output is a scalar
        interpreter.run(inputArray, outputArray) // Run inference with the interpreter

        return outputArray[0] // Return the first element (single output value)
    }

    private fun loadModelFile(): Interpreter {
        val modelBytes = assets.open("tensorflow_model.tflite").readBytes()
        val modelBuffer = ByteBuffer.allocateDirect(modelBytes.size).apply {
            order(ByteOrder.nativeOrder())
            put(modelBytes)
        }
        return Interpreter(modelBuffer)
    }

    private fun encodeCategory(value: String, options: List<String>): Float {
        return options.indexOf(value).toFloat()
    }
}