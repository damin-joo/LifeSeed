package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back Button Click Listener
        binding.backButton.setOnClickListener {
            finish() // Go back to the QuestionsActivity
        }

        // Medication Tracker Button
        binding.medicationTrackerButton.setOnClickListener {
            Toast.makeText(this, "Medication Tracker Clicked!", Toast.LENGTH_SHORT).show()
            // Navigate to Medication Tracker Activity (if required)
        }

        // Treatment Milestones Button


        // FAQ Section Button
        binding.faqSectionButton.setOnClickListener {
            startActivity(Intent(this, FAQActivity::class.java))
        }


        // Bottom Navigation
        binding.homeIcon.setOnClickListener {
            Toast.makeText(this, "Home Icon Clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.calendarIcon.setOnClickListener {
            Toast.makeText(this, "Calendar Icon Clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.calendarIcon.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        binding.settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.medicationTrackerButton.setOnClickListener {
            val intent = Intent(this, Step1Activity::class.java)
            startActivity(intent)
        }


    }
}
