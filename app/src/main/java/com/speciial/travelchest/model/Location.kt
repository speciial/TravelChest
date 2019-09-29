package com.speciial.travelchest.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "location_id")
    val uid:Long,
    val latitude:Double,
    val longitude:Double
)