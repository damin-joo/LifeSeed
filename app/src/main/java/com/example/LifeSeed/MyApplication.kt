package com.example.LifeSeed

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MyApplication : Application() {
    // Declare Firestore instance
    lateinit var db: FirebaseFirestore

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance()
    }
}
