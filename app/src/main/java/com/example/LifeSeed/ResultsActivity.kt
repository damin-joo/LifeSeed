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
        val ethnicityOptions = listOf("Other", "Black", "White", "Asian", "Mixed")
//        val ethnicityOptions = listOf("Asian", "European", "African", "Native American", "Latin American", "Other")   //fe
        val infertilityOptions = listOf("", "Tubal disease", "Ovulatory disorder", "Male factor", "Patient unexplained", "Endometriosis")
//        val infertilityOptions = listOf("Unexplained", "Male Factor", "Female Factor", "Combined Factors", "Other")       //fe
        val treatmentOptions = listOf("Unknown", "IVF", "ICSI", "DI")
        val spermOptions = listOf("Own Sperm", "Donor Sperm")
        val eggOptions = listOf("Own Eggs", "Donor Eggs")

        Log.d("DEBUG-5", "input-2: $p1_age, $p1_ethnicity_string, $p2_age, $p2_ethnicity_string, $causeInfertility_string, $ivfCycles, $diCycles, $previousPregnancies, $treatmentType_string, $singleEmbryo, $spermSource_string, $eggSource_string")

        // Categorize values
        val p1_age_cat = when {
            p1_age in 18..34 -> 0
            p1_age in 35..37 -> 1
            p1_age in 38..39 -> 2
            p1_age in 40..42 -> 3
            p1_age in 43..44 -> 4
            p1_age in 45..50 -> 5
            else -> 6  // If age is outside the predefined range
        }

        val p2_age_cat = when {
            p2_age in 18..34 -> 0
            p2_age in 35..37 -> 1
            p2_age in 38..39 -> 2
            p2_age in 40..42 -> 3
            p2_age in 43..44 -> 4
            p2_age in 45..50 -> 5
            p2_age in 51..55 -> 6
            p2_age in 56..60 -> 5
            else -> 8  // If age is outside the predefined range
        }

        val ivf_cat = ivfCycles.coerceAtMost(6)
        val di_cat = diCycles.coerceAtMost(6)
        val preg_cat = previousPregnancies.coerceAtMost(6)

        Log.d("DEBUG-F", "p1 age, p1 eth, p2 age, p2 eth, cause, ivf, di, prev, type, egg source, sperm source")
        val inputArray = floatArrayOf(
            p1_age_cat.toFloat(),
            encodeCategory(p1_ethnicity_string, ethnicityOptions),
            p2_age_cat.toFloat(),
            encodeCategory(p2_ethnicity_string, ethnicityOptions),
            encodeCategory(causeInfertility_string, infertilityOptions),
            ivf_cat.toFloat(),
            di_cat.toFloat(),
            preg_cat.toFloat(),
            encodeCategory(treatmentType_string, treatmentOptions),
            encodeCategory(eggSource_string, eggOptions),
            encodeCategory(spermSource_string, spermOptions)
        )

        // Run the TensorFlow Lite model
        val output = runTFLiteModel(inputArray)
        val successRate = 100 - (output * 100)

        // Log the received values for debugging
        Log.d("ResultsActivity", "DEBUG-input: ${inputArray.joinToString()}")
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
        val output = Array(1) { FloatArray(1) }
        interpreter.run(inputArray, output)
        val result = output[0][0]
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
}