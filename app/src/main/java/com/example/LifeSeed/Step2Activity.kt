package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityStep2Binding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions

class Step2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityStep2Binding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step2)
        val ageEditText = findViewById<EditText>(R.id.ageEditText).text.toString()
        val ethnicitySpinner = findViewById<Spinner>(R.id.ethnicitySpinner).toString()
        val intent = Intent(this, Step3Activity::class.java)
        intent.putExtra("p2_age", ageEditText)
        intent.putExtra("p2_ethnicity", ethnicitySpinner)
        startActivity(intent)

        // Inflate the layout using View Binding
        binding = ActivityStep2Binding.inflate(layoutInflater)
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
        val p1Age = intent.getStringExtra("p1_age")
        val p1Ethnicity = intent.getStringExtra("p1_ethnicity")
        binding.nextButton.setOnClickListener {
            val p2_age = binding.ageEditText.text.toString().toIntOrNull()
            val p2_eth = binding.ethnicitySpinner.selectedItem.toString()

            if (p2_age == null || p2_age <= 0) {
                Toast.makeText(this, "Please enter a valid partner age.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, Step3Activity::class.java)
            intent.putExtra("p1_age", p1Age)
            intent.putExtra("p1_ethnicity", p1Ethnicity)
            intent.putExtra("p2_age", p2_age)
            intent.putExtra("p2_ethnicity", p2_eth)
            startActivity(intent)
//            savePartnerDetailsToFirestore(p2_age, p2_eth)
        }
    }

    private fun savePartnerDetailsToFirestore(partnerAge: Int, partnerEthnicity: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val partnerData = mapOf(
            "partnerAge" to partnerAge,
            "partnerEthnicity" to partnerEthnicity
        )

        db.collection("User").document(userId)
            .set(partnerData, SetOptions.merge()) // Merge with existing patient data
            .addOnSuccessListener {
                Toast.makeText(this, "Partner details saved!", Toast.LENGTH_SHORT).show()
                goToNextStep()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToNextStep() {
        val intent = Intent(this, Step3Activity::class.java)
        startActivity(intent)
    }
}
