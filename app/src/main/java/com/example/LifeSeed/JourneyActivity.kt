package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityJourneyBinding
import com.google.firebase.firestore.FirebaseFirestore


class JourneyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJourneyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityJourneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back Button Click Listener
        binding.backButton.setOnClickListener {
            // Navigate back to OptionActivity
            val intent = Intent(this, OptionActivity::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }

        // Create Journey Button Click Listener
        binding.createJourneyButton.setOnClickListener {
            Toast.makeText(this, "Journey Created Successfully!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, QuestionsActivity::class.java))
        }

    }
}
