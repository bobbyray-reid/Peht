package com.example.peht

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.peht.database.AppDatabase
import com.example.peht.databinding.ActivityProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private var petId : Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        petId = intent.getLongExtra(
            "pet id",
            -1
        )

        CoroutineScope(Dispatchers.IO).launch {
            val pet = AppDatabase.getDatabase(applicationContext)
                .petDao()
                .getPet(petId)


        }

    }
}