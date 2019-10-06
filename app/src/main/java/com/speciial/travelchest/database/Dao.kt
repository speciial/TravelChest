package com.speciial.travelchest.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.speciial.travelchest.model.File
import com.speciial.travelchest.model.Trip

@Dao
interface TripDao {
    @Query("SELECT * FROM Trip")
    fun getAll(): LiveData<List<Trip>>

    @Query("SELECT * FROM Trip where trip_id = :tripId LIMIT 1")
    fun get(tripId: Long): Trip

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trip: Trip): Long

    @Update
    fun update(trip: Trip)

    @Delete
    fun delete(trip: Trip)


}
@Dao
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
