package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityStep3Binding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions

class Step3Activity : AppCompatActivity() {
    private lateinit var binding: ActivityStep3Binding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Retrieve data from previous steps
        val p1Age = intent.getIntExtra("p1_age", -1)
        val p1Ethnicity = intent.getStringExtra("p1_ethnicity")
        val p2Age = intent.getIntExtra("p2_age", -1)
        val p2Ethnicity = intent.getStringExtra("p2_ethnicity")
        Log.d("PREV-3", "Previous values3:: $p1Age, $p1Ethnicity, $p2Age, $p2Ethnicity")

        // Back Button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Populate Spinner with Causes of Infertility
        val infertilityCauses = arrayOf("Tubal disease", "Ovulatory disorder", "Male factor", "Patient unexplained", "Endometriosis")
//        val infertilityCauses = arrayOf("Unexplained", "Male Factor", "Female Factor", "Combined Factors", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, infertilityCauses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.causeInfertilitySpinner.adapter = adapter

        // Next Button Click Listener
        binding.nextButton.setOnClickListener {
            val cause = binding.causeInfertilitySpinner.selectedItem.toString()
            val ivfCycles = binding.ivfCyclesEditText.text.toString().toIntOrNull()
            val diCycles = binding.diCyclesEditText.text.toString().toIntOrNull()
            val previousPregnancies = binding.previousPregnanciesEditText.text.toString().toIntOrNull()

            // Print the entered input to the terminal
            Log.d("Step3Activity", "User input3:: cause: $cause, ivf cycles: $ivfCycles, di cycles: $diCycles, previous pregnancies: $previousPregnancies")

//            saveInfertilityDataToFirestore(cause, ivfCycles, diCycles, previousPregnancies)

            val intent = Intent(this, Step4Activity::class.java)
            intent.putExtra("p1_age", p1Age)
            intent.putExtra("p1_ethnicity", p1Ethnicity)
            intent.putExtra("p2_age", p2Age)
            intent.putExtra("p2_ethnicity", p2Ethnicity)
            intent.putExtra("causeInfertility", cause)
            intent.putExtra("ivfCycles", ivfCycles)
            intent.putExtra("diCycles", diCycles)
            intent.putExtra("previousPregnancies", previousPregnancies)

            Log.d("DEBUG-3", "User input3:: $p1Age, $p1Ethnicity, $p2Age, $p2Ethnicity, $cause, $ivfCycles, $diCycles, $previousPregnancies")
            startActivity(intent)
        }
    }

    private fun saveInfertilityDataToFirestore(cause: String, ivfCycles: Int, diCycles: Int, previousPregnancies: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        // Data to Save
        val infertilityData = mapOf(
            "infertilityCause" to cause,
            "ivfCycles" to ivfCycles,
            "diCycles" to diCycles,
            "previousPregnancies" to previousPregnancies
        )

        // Merge Data into User Document
        db.collection("User").document(userId)
            .set(infertilityData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Infertility history saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}