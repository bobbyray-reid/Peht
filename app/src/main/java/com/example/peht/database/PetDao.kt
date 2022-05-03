package com.example.peht.database

import androidx.room.*

@Dao
interface PetDao {

    @Insert
    fun addPet(dog : Pet) : Long

    @Update
    fun updatePet(pet : Pet)

    @Delete
    fun deletePet(dog : Pet)

    @Query("SELECT * FROM pet")
    fun getAllPets() : List<Pet>

    @Query("SELECT * FROM pet WHERE id = :dogId")
    fun getPet(dogId : Long) : Pet
}