package com.example.peht.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Pet (
    @PrimaryKey(autoGenerate = true) val id : Long,
    @ColumnInfo val name : String,
    @ColumnInfo val breed : String,
    @ColumnInfo val gender : String,
    @ColumnInfo(name = "birth_date") val birthDate : String,
    @ColumnInfo val age : Long
        ){
    override fun toString(): String {
        return "${name} ${breed} ${gender} ${birthDate} ${age} ${id}"
    }
}