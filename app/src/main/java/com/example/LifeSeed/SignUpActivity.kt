package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.LifeSeed.Models.User
import com.example.LifeSeed.databinding.ActivitySignUpBinding
import com.example.LifeSeed.utils.USER_NODE
import com.example.LifeSeed.utils.USER_PROFILE__FOLDER
import com.example.LifeSeed.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    lateinit var user: User
    private var launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE__FOLDER) { url ->
                if (url == null) {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                } else {
                    user.image = url
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Format login text with HTML styling
        val text =
            "<font color=#fff>Already have an Account</font> <font color=#4CAF50>Login ?</font>"
        binding.login.setText(Html.fromHtml(text))
        user = User()

        // Add padding to the view to account for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sign up button click listener
        binding.signUpBtn.setOnClickListener {
            if (binding.name.editText?.text.isNullOrEmpty() ||
                binding.Email.editText?.text.isNullOrEmpty() ||
                binding.Password.editText?.text.isNullOrEmpty()
            ) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Please fill all the information",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.Email.editText?.text.toString(),
                    binding.Password.editText?.text.toString()
                ).addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        user.name = binding.name.editText?.text.toString()
                        user.email = binding.Email.editText?.text.toString()
                        user.password = binding.Password.editText?.text.toString()

                        Firebase.firestore.collection(USER_NODE)
                            .document(FirebaseAuth.getInstance().currentUser!!.uid)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            result.exception?.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // Add image click listener
        binding.addImage.setOnClickListener {
            launcher.launch("image/*")
        }

        // Navigate to Login Activity
        binding.login.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish() // Close SignUpActivity
        }

        // Navigate back to DOBActivity using the Back Button
        binding.previousImage.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, DOBActivity::class.java))
            finish() // Close SignUpActivity
        }
    }
}
