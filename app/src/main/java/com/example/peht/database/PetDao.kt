package com.example.peht.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PetDao {

    @Insert
    fun addPet(dog : Pet) : Long

    @Delete
    fun deletePet(dog : Pet)

    @Query("SELECT * FROM pet")
    fun getAllPets() : List<Pet>

    @Query("SELECT * FROM pet WHERE id = :dogId")
    fun getPet(dogId : Long) : Pet
}