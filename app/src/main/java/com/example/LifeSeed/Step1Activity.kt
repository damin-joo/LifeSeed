package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityStep1Binding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class Step1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityStep1Binding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Back Button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Populate Spinner with Ethnicity Options
        val ethnicityOptions = arrayOf("Asian", "European", "African", "Native American", "Latin American", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ethnicityOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ethnicitySpinner.adapter = adapter

        // Next Button Click Listener
        binding.nextButton.setOnClickListener {
            val age = binding.ageEditText.text.toString().toIntOrNull()
            val ethnicity = binding.ethnicitySpinner.selectedItem.toString()

            // Print the entered input to the terminal
            Log.d("Step1Activity", "User input in Step1Activity:: age: $age, ethnicity: $ethnicity")

            if (age == null || age <= 0) {
                Toast.makeText(this, "Please enter a valid age.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            savePatientDetailsToFirestore(age, ethnicity)
        }
    }

    private fun savePatientDetailsToFirestore(age: Int, ethnicity: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val patientData = mapOf(
            "patientAge" to age,
            "patientEthnicity" to ethnicity
        )

        db.collection("User").document(userId)
            .set(patientData, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Patient details saved!", Toast.LENGTH_SHORT).show()
                goToNextStep()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToNextStep() {
        val intent = Intent(this, Step2Activity::class.java)
        startActivity(intent)
    }
}