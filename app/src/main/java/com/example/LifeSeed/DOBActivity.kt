package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityDobBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DOBActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDobBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            // Redirect to LoginActivity if not logged in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Inflate the layout using View Binding
        binding = ActivityDobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firestore instance
        val db = Firebase.firestore

        // Handle "Next" button click
        binding.nextButton.setOnClickListener {
            val day = binding.dateInput.text.toString()
            val month = binding.monthInput.text.toString()
            val year = binding.yearInput.text.toString()

            // Validate the inputs
            if (validateDOB(day, month, year)) {
                val dob = "$year-$month-$day" // Combine into YYYY-MM-DD format

                // Save the DOB to Firestore
                val userId = Firebase.auth.currentUser?.uid
                if (userId != null) {
                    db.collection("User").document(userId)
                        .update("dob", dob) // Add/Update the "dob" field
                        .addOnSuccessListener {
                            Toast.makeText(this, "Date of Birth saved successfully!", Toast.LENGTH_SHORT).show()

                            // Navigate to the Sign-Up activity
                            val intent = Intent(this, SignUpActivity::class.java)
                            startActivity(intent)
                            finish() // Close DOBActivity
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save DOB: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateDOB(day: String, month: String, year: String): Boolean {
        // Ensure all fields are filled
        if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        // Parse inputs to integers
        val dayInt = day.toIntOrNull()
        val monthInt = month.toIntOrNull()
        val yearInt = year.toIntOrNull()

        if (dayInt == null || monthInt == null || yearInt == null) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate ranges
        if (dayInt !in 1..31) {
            Toast.makeText(this, "Day must be between 1 and 31", Toast.LENGTH_SHORT).show()
            return false
        }

        if (monthInt !in 1..12) {
            Toast.makeText(this, "Month must be between 1 and 12", Toast.LENGTH_SHORT).show()
            return false
        }

        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        if (yearInt !in 1900..currentYear) {
            Toast.makeText(this, "Year must be between 1900 and $currentYear", Toast.LENGTH_SHORT).show()
            return false
        }

        // Date is valid
        return true
    }
}
