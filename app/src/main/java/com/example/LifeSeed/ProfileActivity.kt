package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load current user data
        loadUserData()

        // Save Name
        binding.saveNameButton.setOnClickListener {
            val newName = binding.nameEditText.text.toString().trim()
            if (newName.isNotEmpty()) {
                updateField("name", newName, "Name updated successfully!")
            } else {
                Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        // Save Email
        binding.saveEmailButton.setOnClickListener {
            val newEmail = binding.emailEditText.text.toString().trim()
            if (newEmail.isNotEmpty()) {
                updateEmail(newEmail)
            } else {
                Toast.makeText(this, "Email cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        // Save DOB
        binding.saveDobButton.setOnClickListener {
            val newDob = binding.dobEditText.text.toString().trim()
            if (newDob.isNotEmpty()) {
                updateField("dob", newDob, "Date of Birth updated successfully!")
            } else {
                Toast.makeText(this, "Date of Birth cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        // Save Password
        binding.savePasswordButton.setOnClickListener {
            val newPassword = binding.passwordEditText.text.toString().trim()
            if (newPassword.length >= 6) {
                updatePassword(newPassword)
            } else {
                Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show()
            }
        }

        // Logout button
        binding.logoutButton.setOnClickListener {
            logoutUser()
        }

        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid
        userId?.let {
            db.collection("User").document(it).get()
                .addOnSuccessListener { document ->
                    binding.nameEditText.setText(document.getString("name") ?: "")
                    binding.emailEditText.setText(document.getString("email") ?: "")
                    binding.dobEditText.setText(document.getString("dob") ?: "")
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load data: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateField(field: String, value: String, successMessage: String) {
        val userId = auth.currentUser?.uid
        userId?.let {
            db.collection("User").document(it).update(field, value)
                .addOnSuccessListener {
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error updating $field: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateEmail(newEmail: String) {
        auth.currentUser?.updateEmail(newEmail)
            ?.addOnSuccessListener {
                updateField("email", newEmail, "Email updated successfully!")
            }
            ?.addOnFailureListener {
                Toast.makeText(this, "Error updating email: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updatePassword(newPassword: String) {
        auth.currentUser?.updatePassword(newPassword)
            ?.addOnSuccessListener {
                Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
            }
            ?.addOnFailureListener {
                Toast.makeText(this, "Error updating password: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(this, DOBActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Close ProfileActivity
    }
}
