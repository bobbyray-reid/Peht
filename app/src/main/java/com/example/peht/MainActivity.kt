package com.example.peht

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peht.database.Pet
import com.example.peht.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: MyAdapter
    private val pets = mutableListOf<Pet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(
            applicationContext, layoutManager.orientation
        )
        binding.recyclerView.addItemDecoration(dividerItemDecoration)

        adapter = MyAdapter()
        binding.recyclerView.adapter = adapter

        val fab = binding.fab.setOnClickListener {
            val intent = Intent(applicationContext, AddPetActivity::class.java)
            startForAddResult.launch(intent)
        }

//        loadAllDogs()

    }

    private val startForAddResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result : ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                loadAllDogs()
            }
        }

    private fun loadAllDogs() {
        TODO("Not yet implemented")
    }

    inner class MyViewHolder(val view: TextView) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener{

        init{
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }

    }

    inner class MyAdapter :
        RecyclerView.Adapter<MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.dog_view, parent, false) as TextView
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val pet = pets[position]
            val birthDate = pet.birthDate
            val today : Date  = Date()

            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val dateInDatabase : Date = parser.parse(birthDate)
            val diff : Long = today.time - dateInDatabase.time
            val sec = diff.toDouble()/1000
            val min = sec / 60
            val hours = min / 60
            val day = hours / 24
            val year = day / 365
            val age = Math.floor(year)
            val displayFormat = SimpleDateFormat("MM/dd/yyyy")
            val displayDate : String = displayFormat.format(dateInDatabase)

            holder.view.setText(
                "${pet.name} ${age}"
            )
        }

        override fun getItemCount(): Int {
            return pets.size
        }

    }
}