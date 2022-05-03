package com.example.peht

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.peht.database.AppDatabase
import com.example.peht.database.Pet
import com.example.peht.databinding.ActivityProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

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

            binding.editButton.setOnClickListener {
                submitChanges()
           }
            binding.eventButton.setOnClickListener {
                val intent = Intent(applicationContext, AddEventActivity::class.java)
                intent.putExtra(
                    getString(R.string.intent_key_pet_id),
                    petId
                )
                startActivity(intent)
            }
            binding.deleteButton.setOnClickListener {
                deletePet(pet)

            }

            withContext(Dispatchers.Main){
                binding.fullNameEditText.setText(pet.name)
                binding.breedEditText.setText(pet.breed)
                binding.genderEditText.setText(pet.gender)
                binding.birthDatePickerButton.setText(pet.birthDate)
            }
        }
    }

    private fun deletePet(pet : Pet){
        val name = binding.fullNameEditText.toString()
        val builder = AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete ${pet.name}?")
            .setNegativeButton(android.R.string.cancel,null)
            .setPositiveButton(android.R.string.ok){
                dialogInterface, whichButton ->

                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.getDatabase(applicationContext)
                        .petDao().deletePet(pet)

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.putExtra(
                        "pet id",
                        petId
                    )
                    withContext(Dispatchers.Main){
                        setResult(RESULT_OK, intent)
                        startActivity(intent)
                    }
                }
            }
            builder.show()
    }

    private fun submitChanges() {
        val name = binding.fullNameEditText.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "Name cannot be empty.", Toast.LENGTH_LONG
            ).show()
            return
        }

        val breed = binding.breedEditText.text.toString()
        if (breed.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "A breed type must be entered.", Toast.LENGTH_LONG
            ).show()
            return
        }
        val gender = binding.genderEditText.text.toString().trim()
        if (gender.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "You must enter Male or Female for gender.", Toast.LENGTH_LONG
            ).show()
            return
        }

        val bday = binding.birthDatePickerButton.text.toString()
        if (bday.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "You must enter a Date of Birth.", Toast.LENGTH_LONG
            ).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val petDao = AppDatabase.getDatabase(applicationContext).petDao()

            val pet = Pet(petId, name, breed, gender, bday)
            petDao.updatePet(pet)

            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra(
                "pet id",
                petId
            )
            withContext(Dispatchers.Main){
                setResult(RESULT_OK, intent)
                startActivity(intent)
            }

        }
    }
}