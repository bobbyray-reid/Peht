package com.example.peht.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PetDao {

    @Insert
    fun addDog(dog : Pet) : Long

    @Delete
    fun deleteDog(dog : Pet)

    @Query("SELECT * FROM pet")
    fun getAllDogs() : List<Pet>

    @Query("SELECT * FROM pet WHERE id = :dogId")
    fun getDog(dogId : Long) : Pet
}