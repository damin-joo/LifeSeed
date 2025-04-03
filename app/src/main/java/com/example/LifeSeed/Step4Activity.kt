package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityStep4Binding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions

class Step4Activity : AppCompatActivity() {
    private lateinit var binding: ActivityStep4Binding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityStep4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Retrieve previous activity data
        val p1Age = intent.getStringExtra("p1_age")
        val p1Ethnicity = intent.getStringExtra("p1_ethnicity")
        val p2Age = intent.getStringExtra("p2_age")
        val p2Ethnicity = intent.getStringExtra("p2_ethnicity")
        val cause = intent.getStringExtra("causeInfertility")
        val ivf = intent.getStringExtra("ivfCycles")
        val di = intent.getStringExtra("diCycles")
        val prevPreg = intent.getStringExtra("previousPregnancies")

        // Back Button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Set up Treatment Type Spinner
        val treatmentTypes = arrayOf("IVF", "ICSI", "IUI", "Frozen Embryo Transfer", "Other")
        binding.treatmentTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, treatmentTypes)

        // Set up Egg Source Spinner
        val eggSources = arrayOf("Own Eggs", "Donor Eggs")
        binding.eggSourceSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, eggSources)

        // Set up Sperm Source Spinner
        val spermSources = arrayOf("Own Sperm", "Donor Sperm")
        binding.spermSourceSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spermSources)

        // Next Button Click Listener
        binding.nextButton.setOnClickListener {
            val treatmentType = binding.treatmentTypeSpinner.selectedItem.toString()
            val singleEmbryo = binding.singleEmbryoToggle.isChecked
            val spermSource = binding.spermSourceSpinner.selectedItem.toString()
            val eggSource = binding.eggSourceSpinner.selectedItem.toString()

            // Print the entered input to the terminal
            Log.d("Step4Activity", "User input4:: treatment type: $treatmentType, single embryo: $singleEmbryo, sperm source: $spermSource, egg source: $eggSource")

            // Validate Inputs
            if (treatmentType.isEmpty() || eggSource.isEmpty() || spermSource.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveTreatmentDetailsToFirestore(treatmentType, singleEmbryo, eggSource, spermSource)

            val intent = Intent(this, ResultsActivity::class.java).apply {
                putExtra("p1_age", p1Age)
                putExtra("p1_ethnicity", p1Ethnicity)
                putExtra("p2_age", p2Age)
                putExtra("p2_ethnicity", p2Ethnicity)
                putExtra("causeInfertility", cause)
                putExtra("ivfCycles", ivf)
                putExtra("diCycles", di)
                putExtra("previousPregnancies", prevPreg)
                putExtra("treatmentType", treatmentType)
                putExtra("singleEmbryo", singleEmbryo)
                putExtra("spermSource", spermSource)
                putExtra("eggSource", eggSource)
            }
            startActivity(intent)
        }
    }

    private fun saveTreatmentDetailsToFirestore(treatmentType: String, singleEmbryo: Boolean, eggSource: String, spermSource: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val treatmentData = mapOf(
            "treatmentType" to treatmentType,
            "electiveSingleEmbryoTransfer" to if (singleEmbryo) "Yes" else "No",
            "eggSource" to eggSource,
            "spermSource" to spermSource
        )

        db.collection("User").document(userId)
            .set(treatmentData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Treatment details saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
