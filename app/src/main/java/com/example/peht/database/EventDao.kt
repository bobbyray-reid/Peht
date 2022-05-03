package com.example.peht.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventDao {

    @Insert
    fun addEvent(event : Event) : Long

    @Delete
    fun deleteEvent(event : Event)

    @Query("SELECT * FROM event")
    fun getAllEvents() : List<Event>
}