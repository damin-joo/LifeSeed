package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityQuestionsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions

class QuestionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuestionsBinding
    private lateinit var db: FirebaseFirestore
    private var treatmentStatus: String? = null
    private var medicationStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Back Button Click Listener
        binding.backButton.setOnClickListener {
            finish() // Go back to the previous screen
        }

        // Treatment Selection Handlers
        binding.yesTreatmentButton.setOnClickListener {
            treatmentStatus = "Yes"
            Toast.makeText(this, "Selected: Treatment Started: Yes", Toast.LENGTH_SHORT).show()
        }

        binding.noTreatmentButton.setOnClickListener {
            treatmentStatus = "No"
            Toast.makeText(this, "Selected: Treatment Started: No", Toast.LENGTH_SHORT).show()
        }

        // Medication Selection Handlers
        binding.yesMedicationButton.setOnClickListener {
            medicationStatus = "Yes"
            Toast.makeText(this, "Selected: Medication Started: Yes", Toast.LENGTH_SHORT).show()
        }

        binding.noMedicationButton.setOnClickListener {
            medicationStatus = "No"
            Toast.makeText(this, "Selected: Medication Started: No", Toast.LENGTH_SHORT).show()
        }

        // Next Button Click Listener
        binding.createJourneyButton.setOnClickListener {
            if (treatmentStatus == null || medicationStatus == null) {
                Toast.makeText(this, "Please select both treatment and medication status!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveTreatmentDataToFirestore()
        }
    }

    private fun saveTreatmentDataToFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val treatmentData = mapOf(
            "treatmentStarted" to treatmentStatus,
            "medicationStarted" to medicationStatus
        )

        db.collection("User").document(userId)
            .set(treatmentData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Treatment details saved!", Toast.LENGTH_SHORT).show()
                navigateToNextStep()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToNextStep() {
        val intent = Intent(this, HomeActivity::class.java) // Replace with your actual next activity
        startActivity(intent)
        finish()
    }
}
