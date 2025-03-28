package com.example.LifeSeed
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        val tvOutput = findViewById<TextView>(R.id.resultValue)
        // Retrieve all values passed from previous activities
        val p1_age = intent.getStringExtra("p1_age")?.toFloatOrNull() ?: 0f
        val p2_age = intent.getStringExtra("p2_age")?.toFloatOrNull() ?: 0f
        val p1_ethnicity = intent.getStringExtra("p1_ethnicity")?.toFloatOrNull() ?: 0f
        val p2_ethnicity = intent.getStringExtra("p2_ethnicity")?.toFloatOrNull() ?: 0f
        val p1_ethnicity_string = intent.getStringExtra("p1_ethnicity") ?: ""
        val p2_ethnicity_string = intent.getStringExtra("p2_ethnicity") ?: ""
        val causeInfertility = intent.getStringExtra("causeInfertility")?.toFloatOrNull() ?: 0f
        val causeInfertility_string = intent.getStringExtra("causeInfertility") ?: ""
        val ivfCycles = intent.getStringExtra("ivfCycles")?.toFloatOrNull() ?: 0f
        val diCycles = intent.getStringExtra("diCycles")?.toFloatOrNull() ?: 0f
        val previousPregnancies = intent.getStringExtra("previousPregnancies")?.toFloatOrNull() ?: 0f
        val treatmentType = intent.getStringExtra("treatmentType")?.toFloatOrNull() ?: 0f
        val treatmentType_string = intent.getStringExtra("treatmentType") ?: ""
        val singleEmbryo = intent.getStringExtra("singleEmbryo")?.toFloatOrNull() ?: 0f
        val spermSource = intent.getStringExtra("spermSource")?.toFloatOrNull() ?: 0f
        val eggSource = intent.getStringExtra("eggSource")?.toFloatOrNull() ?: 0f
        val singleEmbryo_string = intent.getStringExtra("singleEmbryo") ?: ""
        val spermSource_string = intent.getStringExtra("spermSource") ?: ""
        val eggSource_string = intent.getStringExtra("eggSource") ?: ""

//      Val options
        val ethnicityOptions = listOf("Asian", "European", "African", "Native American", "Latin American", "Other")
        val infertilityOptions = listOf("Unexplained", "Male Factor", "Female Factor", "Combined Factors", "Other")
        val treatmentOptions = listOf("IVF", "ICSI", "IUI", "Frozen Embryo Transfer", "Other")
        val sourceOptions = listOf("Own Sperm", "Donor Sperm") // same for eggs

        // Prepare input data for TensorFlow Lite Model
//        val inputArray = floatArrayOf(p1_age, p2_age, p1_ethnicity, p2_ethnicity,causeInfertility, treatmentType, singleEmbryo, spermSource, eggSource ,ivfCycles, diCycles, previousPregnancies)
        val inputArray = floatArrayOf(
            p1_age,
            p2_age,
            encodeCategory(p1_ethnicity_string, ethnicityOptions),
            encodeCategory(p2_ethnicity_string, ethnicityOptions),
            encodeCategory(causeInfertility_string, infertilityOptions),
            encodeCategory(treatmentType_string, treatmentOptions),
            if (singleEmbryo_string == "Yes") 1f else 0f,
            encodeCategory(spermSource_string, sourceOptions),
            encodeCategory(eggSource_string, sourceOptions),
            ivfCycles,
            diCycles,
            previousPregnancies
        )
// Log input array
        Log.d("MODEL_INPUT", "Input values: ${inputArray.contentToString()}")


//        Wrapping for tfl model input shape
        val modelBytes = assets.open("tensorflow_model.tflite").readBytes()
        val modelBuffer = ByteBuffer.allocateDirect(modelBytes.size).apply {
            order(ByteOrder.nativeOrder())
            put(modelBytes)
        }
        val interpreter = Interpreter(modelBuffer)
        val modelInput = arrayOf(inputArray)
        val modelOutput = FloatArray(1)
        interpreter.run(modelInput, modelOutput)
        val successRate = modelOutput[0] * 100

        // Run TensorFlow Lite Model
        val output = runTFLiteModel(inputArray)
        // Log output result
        Log.d("MODEL_OUTPUT", "Success Rate: $output")

        // Display result
        binding.resultValue.text = "Success Rate: %.2f%%".format(successRate)
        tvOutput.text = when {
            output >= 80 -> "Excellent Chances: %.2f%%".format(output)
            output >= 50 -> "Moderate Chances: %.2f%%".format(output)
            else -> "Low Chances: %.2f%%".format(output)
        }
        findViewById<TextView>(R.id.resultValue).text = "Success Rate: ${output}%"

        ///////////////////////////////////////---------------

        // Inflate the layout using View Binding
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Retrieve and Display Result
        retrieveResultFromFirestore()

        // Back Button Listener
        binding.backButton.setOnClickListener {
            finish() // Go back to the previous screen
        }

        // Home Button Listener
        binding.homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun runTFLiteModel(inputArray: FloatArray): Float {
        // Load model from assets into ByteBuffer
        val modelInput = assets.open("tensorflow_model.tflite").readBytes()
        val modelBuffer = ByteBuffer.allocateDirect(modelInput.size).apply {
            order(ByteOrder.nativeOrder())
            put(modelInput)
        }

        // Correctly initialize Interpreter with ByteBuffer
        val interpreter = Interpreter(modelBuffer)

        // Prepare output array and run inference
        val outputArray = FloatArray(1)
        interpreter.run(inputArray, outputArray)
        interpreter.close()

        return outputArray[0]
    }

    // ✅ Function to load TensorFlow Lite model properly
    private fun loadModelFile(): Interpreter {
        val assetFileDescriptor = assets.openFd("app/src/main/assets/tensorflow_model.tflite")
        val inputStream = assetFileDescriptor.createInputStream()
        val byteArray = inputStream.readBytes()
        val buffer = ByteBuffer.allocateDirect(byteArray.size).apply {
            order(ByteOrder.nativeOrder())
            put(byteArray)
        }
        return Interpreter(buffer)
    }

//    Helper function to turn into floats
    private fun encodeCategory(value: String, options: List<String>): Float {
        return options.indexOf(value).toFloat()
    }

    private fun retrieveResultFromFirestore() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            binding.resultValue.text = "Unknown"
            return
        }

        // Fetch the result from Firestore
        db.collection("User")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val successRate = document.getDouble("successRate") ?: 0.0

                    // Display success rate as a percentage
                    binding.resultValue.text = "Success Rate: ${"%.2f".format(successRate)}%"

                    // Recommend clinics based on success rate
                    recommendClinics(successRate)
                } else {
                    binding.resultValue.text = "No Result Available"
                    Toast.makeText(this, "No result found for this user.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                binding.resultValue.text = "Error"
                Toast.makeText(this, "Error retrieving result: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirestoreError", "Error fetching result: ${e.message}")
            }
    }

    private fun recommendClinics(successRate: Double) {
        val clinics = listOf(
            "Toronto Fertility Clinic - +1 416-123-4567",
            "Maple Leaf IVF Center - +1 416-987-6543",
            "Downtown Reproductive Health - +1 416-222-3333"
        )

        val recommendation = when {
            successRate >= 80 -> "Great chances! Here’s a clinic to consider:\n${clinics[0]}"
            successRate in 50.0..79.9 -> "Good chances! You might want to consult:\n${clinics[1]}"
            else -> "Consider expert advice. Try:\n${clinics[2]}"
        }


    }
}
