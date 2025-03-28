package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.LifeSeed.Models.User
import com.example.LifeSeed.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val email = binding.email.editText?.text.toString()
            val password = binding.pass.editText?.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Navigate to OptionActivity after successful login
                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, OptionActivity::class.java))
                            finish() // Close LoginActivity
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                task.exception?.localizedMessage ?: "Login failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        binding.createAccountBtn.setOnClickListener {
            // Navigate to SignUpActivity
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish() // Close LoginActivity
        }
    }
}
