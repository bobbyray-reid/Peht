package com.example.peht

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peht.database.AppDatabase
import com.example.peht.database.Event
import com.example.peht.databinding.ActivityAddEventBinding
import com.example.peht.databinding.DialogLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddEventBinding
    private var petId : Long = -1
    private lateinit var adapter: EventAdapter
    private val events = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        petId = intent.getLongExtra(
            "pet id",
            -1
        )

        val layoutManager = LinearLayoutManager(this)
        binding.eventRecyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
            applicationContext, layoutManager.orientation
        )
        binding.eventRecyclerView.addItemDecoration(dividerItemDecoration)
        adapter = EventAdapter()
        binding.eventRecyclerView.adapter = adapter


        loadPet(petId)

        binding.addEventButton.setOnClickListener(AddEvent())

    }

    inner class AddEvent : View.OnClickListener {
        override fun onClick(v: View?) {
            val builder = AlertDialog.Builder(binding.root.context)

            val listener = DialogInterface.OnClickListener{
                dialog, which ->

            }

            val dialogBinding = DialogLayoutBinding.inflate(layoutInflater)

            builder
                .setTitle("Enter an Event")
                .setView(dialogBinding.root)
                .setPositiveButton("Save", listener)
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }

    inner class EventViewHolder(val view: TextView) :
        RecyclerView.ViewHolder(view), View.OnLongClickListener {
            init {
                view.setOnLongClickListener(this)
            }

        override fun onLongClick(v: View?): Boolean {
            TODO("Not yet implemented")
        }
        }

    inner class EventAdapter : RecyclerView.Adapter<EventViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.event_view,parent,false) as TextView
            return EventViewHolder(view)
        }

        override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
            val event = events[position]
            val date = event.date

            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val dateInDatabase : Date = parser.parse(date)
            val displayFormat = SimpleDateFormat("HH:mm a MM/yyyy")
            val displayDate : String = displayFormat.format(dateInDatabase)

            holder.view.setText(
                "${event.title}\n${displayDate}"
            )
        }

        override fun getItemCount(): Int {
            return events.size
        }

    }

    private fun loadPet(petId : Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val pet = AppDatabase.getDatabase(applicationContext)
                .petDao()
                .getPet(petId)

            withContext(Dispatchers.Main){
                binding.petNameTextView.setText(pet.name)
            }
        }
    }
}