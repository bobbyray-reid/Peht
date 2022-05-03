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
import com.example.peht.database.AppDatabase
import com.example.peht.database.Pet
import com.example.peht.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: MyAdapter
    private val pets = mutableListOf<Pet>()

    override fun onResume() {

        super.onResume()
    }

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


        loadAllPets()


    }

    private val startForAddResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result : ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                loadAllPets()
            }
        }

    private fun loadAllPets() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.petDao()
            val results = dao.getAllPets()

            withContext(Dispatchers.Main){
                pets.clear()
                pets.addAll(results)
                adapter.notifyDataSetChanged()

                var msg = ""
                if(pets.size < 0){
                    msg = "Enter a pet to begin"
                }else{
                    msg = "Your ${pets.size} pet(s) are listed below"
                }
                binding.dogMessageTextView.setText(msg)
            }
        }
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

            holder.view.setText(
                "${pet.name} ${pet.birthDate.padStart(12)}"
            )
        }

        override fun getItemCount(): Int {
            return pets.size
        }

    }
}