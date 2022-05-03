package com.example.peht

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import com.example.peht.database.AppDatabase
import com.example.peht.database.Pet
import com.example.peht.databinding.ActivityAddPetBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*

class AddPetActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddPetBinding
    private lateinit var datePickerDialog : DatePickerDialog
    private lateinit var dateButton : Button

    override fun onResume() {
        super.onResume()

        val breeds = resources.getStringArray(R.array.breeds)
        val arrayAdapter = ArrayAdapter(applicationContext, R.layout.dropdown_item, breeds)
        binding.breedAutoCompleteTextView.setAdapter(arrayAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDatePicker()
        dateButton = findViewById(R.id.datePickerButton) as Button
        dateButton.setText(getTodaysDate())
        dateButton.setOnClickListener {
            datePickerDialog.show()
        }
    }


    private fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        var month = cal.get(Calendar.MONTH)
        month = month + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day,month,year)
    }

    private fun initDatePicker() {
        var dateSetListener = DatePickerDialog.OnDateSetListener {datePicker, year, m, day ->
            var month = m + 1
            val date : String = makeDateString(day, month, year)
            dateButton.setText(date)
        }

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val style = AlertDialog.THEME_HOLO_LIGHT

        datePickerDialog = DatePickerDialog(this,style,dateSetListener,year,month,day)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis())
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun getMonthFormat(month: Int) : String {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        return "JAN";
    }

    override fun onBackPressed() {
        val name = binding.fullNameEditText.text.toString().trim()
        if(name.isEmpty()){
            Toast.makeText(applicationContext,
            "Name cannot be empty.", Toast.LENGTH_LONG).show()
            return
        }

        val breed = binding.breedAutoCompleteTextView.text.toString()
        if(breed.isEmpty()){
            Toast.makeText(applicationContext,
                "A breed must be selected.", Toast.LENGTH_LONG).show()
            return
        }
        var gender = ""
        val male = binding.maleButton
        val female = binding.femaleButton
        if(male.isChecked){
            gender = "Male"
        }else if(female.isChecked){
            gender = "Female"
        }else{
            Toast.makeText(applicationContext,
                "A gender must be selected.", Toast.LENGTH_LONG).show()
            return
        }

        val bday = binding.datePickerButton.text.toString()
        if(bday.isEmpty()){
            Toast.makeText(applicationContext,
                "You must enter a Date of Birth.", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val petDao = AppDatabase.getDatabase(applicationContext).petDao()
            var petId : Long

            val pet = Pet(0, name, breed, gender, bday)
            petId = petDao.addPet(pet)

            val intent = Intent()
            intent.putExtra(
                "pet id",
                petId
            )
            withContext(Dispatchers.Main){
                setResult(RESULT_OK,intent)
                super.onBackPressed()
            }
        }

    }

}