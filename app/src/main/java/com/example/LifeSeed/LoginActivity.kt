package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener { loginUser() }
        binding.createAccountBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish() // Close LoginActivity
        }
    }

    private fun loginUser() {
        val email = binding.email.editText?.text.toString().trim()
        val password = binding.pass.editText?.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all the details.")
            return
        }

        // Disable the login button to prevent multiple clicks
        binding.loginBtn.isEnabled = false

        // Perform login operation
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Re-enable the login button after the task is complete
                binding.loginBtn.isEnabled = true

                if (task.isSuccessful) {
                    showToast("Login Successful!")
                    startActivity(Intent(this, OptionActivity::class.java))
                    finish()  // Close the LoginActivity
                } else {
                    handleFirebaseError(task.exception)
                }
            }
    }

    private fun handleFirebaseError(exception: Exception?) {
        val message = when (exception) {
            is FirebaseAuthInvalidUserException -> "Invalid email or user not found."
            is FirebaseAuthInvalidCredentialsException -> "Invalid password."
            is FirebaseAuthUserCollisionException -> "User already exists."
            is FirebaseAuthWeakPasswordException -> "Weak password, try again."
            else -> exception?.localizedMessage ?: "Login failed."
        }
        showToast(message)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}