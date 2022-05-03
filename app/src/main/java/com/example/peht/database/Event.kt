package com.example.peht.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = arrayOf(
    ForeignKey(entity = Pet::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("eventId"),
        onDelete = ForeignKey.CASCADE))
)
class Event (
    @PrimaryKey(autoGenerate = true) val eventId : Long,
    @ColumnInfo val pId : Long,
    @ColumnInfo val title : String,
    @ColumnInfo val description : String,
    @ColumnInfo val date : String
        ){

}