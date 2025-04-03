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
    private lateinit var interpreter: Interpreter  // Declare interpreter globally

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Load TensorFlow model
        interpreter = loadModelFile()

        // Retrieve all values passed from previous activities
        val p1_age = intent.getStringExtra("p1_age")?.toFloatOrNull() ?: 0f
        val p2_age = intent.getStringExtra("p2_age")?.toFloatOrNull() ?: 0f
        val p1_ethnicity_string = intent.getStringExtra("p1_ethnicity") ?: ""
        val p2_ethnicity_string = intent.getStringExtra("p2_ethnicity") ?: ""
        val causeInfertility_string = intent.getStringExtra("causeInfertility") ?: ""
        val ivfCycles = intent.getStringExtra("ivfCycles")?.toFloatOrNull() ?: 0f
        val diCycles = intent.getStringExtra("diCycles")?.toFloatOrNull() ?: 0f
        val previousPregnancies = intent.getStringExtra("previousPregnancies")?.toFloatOrNull() ?: 0f
        val treatmentType_string = intent.getStringExtra("treatmentType") ?: ""
        val singleEmbryo = intent.getBooleanExtra("singleEmbryo", false) // Correct way to get Boolean
        val spermSource_string = intent.getStringExtra("spermSource") ?: ""
        val eggSource_string = intent.getStringExtra("eggSource") ?: ""

        // Encode categorical features and prepare input array
        val ethnicityOptions = listOf("Asian", "European", "African", "Native American", "Latin American", "Other")
        val infertilityOptions = listOf("Unexplained", "Male Factor", "Female Factor", "Combined Factors", "Other")
        val treatmentOptions = listOf("IVF", "ICSI", "IUI", "Frozen Embryo Transfer", "Other")
        val sourceOptions = listOf("Own Sperm", "Donor Sperm") // same for eggs

        val inputArray = floatArrayOf(
            p1_age,
            p2_age,
            encodeCategory(p1_ethnicity_string, ethnicityOptions),
            encodeCategory(p2_ethnicity_string, ethnicityOptions),
            encodeCategory(causeInfertility_string, infertilityOptions),
            encodeCategory(treatmentType_string, treatmentOptions),
            if (singleEmbryo) 1f else 0f,  // Direct Boolean check
            encodeCategory(spermSource_string, sourceOptions),
            encodeCategory(eggSource_string, sourceOptions),
            ivfCycles,
            diCycles,
            previousPregnancies
        )

        // Run the TensorFlow Lite model
        val output = runTFLiteModel(inputArray)
        val successRate = output * 100

        // Log the received values for debugging
        Log.d("ResultsActivity", "singleEmbryo: $singleEmbryo")
        Log.d("ResultsActivity", "input: $p1_age, $p1_ethnicity_string, $p2_age, $p2_ethnicity_string, $causeInfertility_string, $treatmentType_string, $singleEmbryo, $spermSource_string, $eggSource_string, $ivfCycles, $diCycles, $previousPregnancies")
        Log.d("ResultsActivity", "output: $successRate")

        // Display result
        binding.resultValue.text = when {
            successRate >= 80 -> "Excellent Chances: %.2f%%".format(successRate)
            successRate >= 50 -> "Moderate Chances: %.2f%%".format(successRate)
            else -> "Low Chances: %.2f%%".format(successRate)
        }

        // Retrieve previous results from Firestore
        retrieveResultFromFirestore()

        // Back Button Listener
        binding.backButton.setOnClickListener {
            finish()
        }

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
        // Prepare the output tensor
        val outputArray = Array(1) { FloatArray(1) }
        interpreter.run(inputArray, outputArray)
        val result = outputArray[0][0]
        Log.d("InferenceResult", "Model output: $result")
        return result
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

    private fun retrieveResultFromFirestore() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            binding.resultValue.text = "Unknown"
            return
        }

        db.collection("User").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val successRate = document.getDouble("successRate") ?: 0.0
                    binding.resultValue.text = "Success Rate: ${"%.2f".format(successRate)}%"
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
}