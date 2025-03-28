package com.example.LifeSeed

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityCalendarBinding
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle calendar date selection
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth-${month + 1}-$year"
            binding.selectedDateText.text = "Selected Date: $selectedDate"
            Toast.makeText(this, "Date Selected: $selectedDate", Toast.LENGTH_SHORT).show()
        }

        // Back Button to return to HomeActivity
        binding.backButton.setOnClickListener {
            finish() // Close CalendarActivity
        }
    }
}
