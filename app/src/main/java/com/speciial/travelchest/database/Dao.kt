package com.speciial.travelchest.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.speciial.travelchest.model.*

interface TripDao {
    @Query("SELECT * FROM Trip")
    fun getAll(): LiveData<List<Trip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trip: Trip): Long

    @Update
    fun update(trip: Trip)

    @Delete
    fun delete(trip: Trip)
}
interface FileDao {
    @Query("SELECT * FROM File")
    fun getAll(): LiveData<List<File>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(file: File): Long

    @Update
    fun update(file: File)

    @Delete
    fun delete(file: File)
}
interface TypeDao {
    @Query("SELECT * FROM Type")
    fun getAll(): LiveData<List<Type>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(type: Type): Long

    @Insert
    fun insertAll(listType:List<Type>):Long

    @Update
    fun update(type: Type)

    @Delete
    fun delete(type: Type)
}
interface LocationDao {
    @Query("SELECT * FROM Location")
    fun getAll(): LiveData<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: Location): Long

    @Update
    fun update(location: Location)

    @Delete
    fun delete(location: Location)
}