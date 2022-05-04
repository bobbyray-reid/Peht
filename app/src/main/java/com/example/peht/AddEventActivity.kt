package com.example.peht

import android.app.DatePickerDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
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
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEventBinding
    private var petId: Long = -1
    private lateinit var adapter: EventAdapter
    private val events = mutableListOf<Event>()

    var button_date: Button? = null
    var textview_date: TextView? = null
    var cal = Calendar.getInstance()
    private lateinit var selectedDate: String

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
        loadAllEvents()

        binding.addEventButton.setOnClickListener(AddEvent(petId))

    }

    private fun loadAllEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.eventDao()
            val results = dao.getAllEvents()

            withContext(Dispatchers.Main) {
                events.clear()
                events.addAll(results)
                adapter.notifyDataSetChanged()
            }
        }
    }

    inner class AddEvent(dogId: Long) : View.OnClickListener {
        val pId = dogId
        override fun onClick(v: View?) {
            val builder = AlertDialog.Builder(binding.root.context)
            val dialogBinding = DialogLayoutBinding.inflate(layoutInflater)

            textview_date = dialogBinding.dateTextView
            button_date = dialogBinding.dateButton

            textview_date!!.setText("--/--/----")

            val dateSetListener = object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, day)
                    updateDateInView()
                }
            }

            button_date!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    DatePickerDialog(
                        this@AddEventActivity,
                        dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            })

            val listener = DialogInterface.OnClickListener { dialog, which ->


                val name = dialogBinding.eventNameEditText.text.toString().trim()
                val desc = dialogBinding.descriptionEditText.text.toString().trim()
                val date = dialogBinding.dateTextView.text.toString()

                if (name.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Name cannot be empty.", Toast.LENGTH_LONG
                    ).show()
                    return@OnClickListener
                }
                if (desc.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Description cannot be empty.", Toast.LENGTH_LONG
                    ).show()
                    return@OnClickListener
                }
                if (date.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Date cannot be empty.", Toast.LENGTH_LONG
                    ).show()
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val eventDao = AppDatabase.getDatabase(applicationContext).eventDao()
                    var eventId: Long
                    val event = Event(0, pId, name, desc, date)
                    eventId = eventDao.addEvent(event)

                    withContext(Dispatchers.Main) {
                        loadAllEvents()
                    }


                }
            }



            builder
                .setTitle("Enter an Event")
                .setView(dialogBinding.root)
                .setPositiveButton("Save", listener)
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }

        private fun updateDateInView() {
            val myFormat = "MM/dd/yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            selectedDate = sdf.format((cal.getTime()))
            textview_date!!.text = selectedDate
        }
    }

    inner class EventViewHolder(val view: TextView) :
        RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val event = events[adapterPosition]

            val builder = AlertDialog.Builder(v!!.context)
                .setTitle("Event Details")
                .setMessage("Title:\n${event.title}\n\nDescription:\n${event.description}\n\nDate:\n${event.date}")
                .setPositiveButton(android.R.string.ok,null)
                .show()
        }

        override fun onLongClick(v: View?): Boolean {
            val event = events[adapterPosition]

            val builder = AlertDialog.Builder(v!!.context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete ${event.title}")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { dialogInterface, whichButton ->

                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.getDatabase(applicationContext)
                            .eventDao().deleteEvent(event)
                        loadAllEvents()
                    }
                }
            builder.show()
            return true
        }
    }
        inner class EventAdapter : RecyclerView.Adapter<EventViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.event_view, parent, false) as TextView
                return EventViewHolder(view)
            }

            override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
                val event = events[position]


                holder.view.setText(
                    "${event.title}\n${event.date}"
                )
            }

            override fun getItemCount(): Int {
                return events.size
            }

        }

    private fun loadPet(petId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val pet = AppDatabase.getDatabase(applicationContext)
                .petDao()
                .getPet(petId)

            withContext(Dispatchers.Main) {
                binding.petNameTextView.setText(pet.name)
            }
        }
    }
}