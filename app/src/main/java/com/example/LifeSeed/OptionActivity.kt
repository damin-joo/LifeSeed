package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityOptionBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions

class OptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptionBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Back Button Click Listener
        binding.previousImage.setOnClickListener {
            finish() // Go back to the previous screen
        }

        // IVF Experience Selection Handlers
        binding.newToIVFButton.setOnClickListener { saveIVFExperience("Completely new to IVF") }
        binding.knowIVFButton.setOnClickListener { saveIVFExperience("Know a bit about IVF") }
        binding.expertIVFButton.setOnClickListener { saveIVFExperience("Expert on IVF") }

        // Navigate to JourneyActivity
        binding.finishProfileSetupButton.setOnClickListener {
            val intent = Intent(this, JourneyActivity::class.java)
            startActivity(intent)
            finish() // Close OptionActivity
        }
    }

    private fun saveIVFExperience(experience: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val experienceData = mapOf("ivfExperience" to experience)

        db.collection("User").document(userId)
            .set(experienceData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Saved: $experience", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
