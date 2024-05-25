package com.example.projectworkpcparts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.projectworkpcparts.databinding.ActivityMainBinding
import com.example.projectworkpcparts.databinding.ActivityUserprofileBinding
import com.google.firebase.auth.FirebaseAuth


class UserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUserprofileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserprofileBinding.inflate(layoutInflater)
        ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.userIcon.setOnClickListener {
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser

        currentUser?.let {
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl

            binding.userEmail.text = email
            binding.userName.text = name

            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .into(binding.userPhoto)
            }
        }
    }
}